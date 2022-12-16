package mx.arquidiocesis.sos.viewmodel

import android.location.Location
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.sos.adapters.FaithfulProfileAdapter
import mx.arquidiocesis.sos.model.Service
import mx.arquidiocesis.sos.model.ServiceDetalleModel
import mx.arquidiocesis.sos.repository.SOSRepository
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
const val  LOCATIONP = 100

class SOSServicesFaithfulViewModel(val repository: SOSRepository) : ViewModel() {
    var errorResponse = repository.errorResponse
    var errorResponseExit = repository.errorResponse
    val successRegistrySOS = repository.successRegistrySOS
    val locationResponse = repository.locationResponse
    val locationsSOS = repository.locationsSOS
    val devoteeServices = repository.priestServices
    val serviceList = repository.getLiveData()
    val serviceDetalle = repository.getServiceDetalle
    val updateStatus = repository.actualizarEstatus
    val servicioPendiente = repository.servicioPendiente
    val priestList = repository.priestList
    val imagen = repository.imagen

    var counterMinutes: MutableLiveData<String> = MutableLiveData()
    var counterSeconds: MutableLiveData<String> = MutableLiveData()
    var launchMessageCounter: MutableLiveData<String> = MutableLiveData()
    var latitudeFaithful: Double = 0.0
    var longitudeFaithful: Double = 0.0
    private var userId: Int? = null


    private var adapter: MutableLiveData<FaithfulProfileAdapter> = MutableLiveData()
    private lateinit var adapterServices: FaithfulProfileAdapter
    var twiceInvoke = false
    var itemService: Service? = null


    fun liveAdapter(): LiveData<FaithfulProfileAdapter> {
        return adapter
    }

    fun pendiente(id: Int, role: String, idService: Int) {
        GlobalScope.launch {
            repository.getSOSPendiente(id, role, idService)
        }
    }

    fun getUserId(): Int {
        userId = userId ?: eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID.name,
            EAMXTypeObject.INT_OBJECT
        ) as Int
        return userId!!
    }

    fun getServicesList() {
        twiceInvoke = true
        GlobalScope.launch {
            repository.getTypeServicesSOS()
        }
    }

    fun setLocation(location: Location) {
        latitudeFaithful = location.latitude
        longitudeFaithful = location.longitude
    }
    fun setLocation(latitude: Double,longitude: Double) {
        latitudeFaithful = latitude
        longitudeFaithful = longitude
    }

    fun getLocationsSOS(latitude: Double=latitudeFaithful,longitude: Double=longitudeFaithful) {
        GlobalScope.launch {
            repository.getLocationsSOS(latitude,longitude)
        }
    }

    fun setServices(serviceId: Int, status: String) {
        GlobalScope.launch {
            repository.setServices(getUserId(), serviceId, status)
        }
    }

    fun getServices(serviceId: Int) {
        GlobalScope.launch {
            repository.getServices(getUserId(), serviceId)
        }
    }

    fun getPrientsList() {
        GlobalScope.launch {
            repository.getPrientsList(getUserId())
        }
    }

    fun parseDateToddMMyy(time: String): String {
        val inputPattern = "yyyy-MM-dd HH:mm:ss"
        val outputPattern = "yyyy-MM-dd HH:mm"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)
        var date: Date
        var str: String = time
        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }

    fun registrySOS(
        devoteeId: Int,
        phone: String,
        serviceId: Int,
        contact_id: Int,
        latitude: Double,
        longitude: Double,
        latitudeDirrecion: Double,
        longitudeDirrecion: Double,
        funeral: String?,
        description: String?
    ) {


        var jsonRegistrySOS: JsonObject = JsonObject()
        var devotee: JsonObject = JsonObject()

        devotee.addProperty("devotee_id", devoteeId)
        devotee.addProperty("phone", phone)
        jsonRegistrySOS.add("devotee", devotee)

        var service: JsonObject = JsonObject()
        service.addProperty("id", serviceId)
        service.addProperty("type", "SOS")
        jsonRegistrySOS.add("service", service)

        var priest: JsonObject = JsonObject()
        priest.addProperty("contact_id", contact_id)
        jsonRegistrySOS.add("support_contact", priest)

        jsonRegistrySOS.addProperty("funeral_home", funeral)


        var address: JsonObject = JsonObject()
        address.addProperty("description", description)
        address.addProperty("longitude", longitudeDirrecion)
        address.addProperty("latitude", latitudeDirrecion)
        jsonRegistrySOS.add("address", address)

        jsonRegistrySOS.addProperty("latitude", latitude)
        jsonRegistrySOS.addProperty("longitude", longitude)


        GlobalScope.launch {
            repository.postRegistrySOS(getUserId(), jsonRegistrySOS)
        }
    }

    fun actualizaHora(
        id: Int,
        idPriest: Int,
        date: String
    ) {

        var jsonRegistrySOS: JsonObject = JsonObject()

        jsonRegistrySOS.addProperty("scheduled_date", date)
        var priest: JsonObject = JsonObject()
        priest.addProperty("id", idPriest)
        jsonRegistrySOS.add("priest", priest)


        GlobalScope.launch {
            repository.setServicesProgres(getUserId(), id, jsonRegistrySOS, 1)
        }
    }

    fun actualizaCalificacion(
        id: Int,
        rating: Int,
        comment: String
    ) {

        var jsonRegistrySOS: JsonObject = JsonObject()

        jsonRegistrySOS.addProperty("rating", rating)
        jsonRegistrySOS.addProperty("comments", comment)

        GlobalScope.launch {
            repository.setServicesProgres(getUserId(), id, jsonRegistrySOS, 2)
        }
    }

    fun progresStatus(
        id: Int,
        priestId: Int,
        locationId: Int

    ) {

        var jsonRegistrySOS: JsonObject = JsonObject()

        jsonRegistrySOS.addProperty("status", "IN_PROGRESS")
        var priest: JsonObject = JsonObject()
        priest.addProperty("priest_id", priestId)
        jsonRegistrySOS.add("priest", priest)

        var location: JsonObject = JsonObject()
        location.addProperty("id", locationId)
        jsonRegistrySOS.add("location", location)

        GlobalScope.launch {
            repository.setServicesProgres(getUserId(), id, jsonRegistrySOS, 0)
        }
    }

    fun getLocation() {
        repository.getLocation()
    }

    fun getPriestServicesWithDevotee(userId: Int, status: String) {
        GlobalScope.launch {
            repository.getPriestServices(userId, status)
        }
    }

    fun initCounter(): Job {
        var minutes = 59
        var seconds = 59

        counterMinutes.value = "0$minutes"
        counterSeconds.value = seconds.toString()
        val globalScope = GlobalScope.launch(Dispatchers.IO) {
            while (minutes >= 0) {
                Thread.sleep(1000)
                if (seconds == 0) {
                    if (minutes == 0) {
                        withContext(Dispatchers.Main) {
                            launchMessageCounter.value = "¿RECIBISTE LA LLAMADA DEL SACERDOTE?"
                        }
                        break
                    } else {
                        minutes--
                        val minuteString = if (minutes < 10) {
                            "0$minutes"
                        } else {
                            minutes.toString()
                        }
                        seconds = 59
                        withContext(Dispatchers.Main) {
                            counterMinutes.value = minuteString
                            counterSeconds.value = seconds.toString()
                        }
                    }
                } else {
                    seconds--
                    val secondString = if (seconds < 10) {
                        "0$seconds"
                    } else {
                        seconds.toString()
                    }
                    withContext(Dispatchers.Main) {
                        counterSeconds.value = secondString
                    }
                }
            }
        }
        return globalScope
    }

    fun setObserver(viewLifecycleOwner: LifecycleOwner) {
        serviceList.observe(viewLifecycleOwner) {
            var adapterLocal = FaithfulProfileAdapter(it) { item ->
                itemService = item.second
                adapterServices.update(item.first)
            }
            this.adapterServices = adapterLocal
            adapter.value = this.adapterServices
        }
    }

    fun createImage(serviceDetalleModel: ServiceDetalleModel, estado: String) {

        repository.createImage(serviceDetalleModel, estado)
    }

}
