package mx.arquidiocesis.misiglesias.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.misiglesias.model.ChurchDetaillModel
import mx.arquidiocesis.misiglesias.model.EditarIglesiasModel
import mx.arquidiocesis.misiglesias.model.HoraryModelItem
import mx.arquidiocesis.misiglesias.repository.Repository

class EditarIglesiaViewModel(private val repository: Repository) : ViewModel() {

    val response = repository.iglesiasPutResponse
    var errorResponse = repository.errorResponse
    var validateForm = MutableLiveData<HashMap<String, String>>()
    val catalogoMassesResponse = repository.catalogoMassesResponse
    val catalogoServiciosResponse = repository.catalogoServiciosResponse
    val responseDetalle = repository.detalleResponse
    val reponsePrients = repository.priestResponse
    val locationResponse = repository.locationResponse

    fun getCatalogServicios() {
        GlobalScope.launch {
            repository.getCatalogServicios()
        }
    }

    fun getpriests(name: String) {
        GlobalScope.launch {
            repository.getpriests(name)
        }
    }

    fun editIglesias(item: ChurchDetaillModel) {
        var jsonRegistry: JsonObject = JsonObject()
        jsonRegistry.addProperty("description", item.description)
        jsonRegistry.addProperty("email", item.email)
        jsonRegistry.addProperty("phone", item.phone)
        if(!item.stream.isNullOrEmpty()){
            jsonRegistry.addProperty("stream", item.stream)
        }else{
            jsonRegistry.remove("stream")
        }
        if(!item.website.isNullOrEmpty()){
            jsonRegistry.addProperty("website", item.website)
        }else{
            jsonRegistry.addProperty("website", "")
        }
        if(!item.facebook.isNullOrEmpty()){
            jsonRegistry.addProperty("facebook", item.facebook)
        }else{
            jsonRegistry.addProperty("facebook", "")
        }
        if(!item.twitter.isNullOrEmpty()){
            jsonRegistry.addProperty("twitter", item.twitter)
        }else{
            jsonRegistry.addProperty("twitter", "")
        }
        if(!item.instagram.isNullOrEmpty()){
            jsonRegistry.addProperty("instagram", item.instagram)
        }else{
            jsonRegistry.addProperty("instagram", "")
        }

        jsonRegistry.addProperty("bank_account", item.bank_account)
        var principal = JsonObject()
        principal.addProperty("id", item.principal?.id)
        jsonRegistry.add("principal", principal)

        var schedules = JsonArray()
        if (!item.schedules.isNullOrEmpty()) {
            schedules = putHorary(item.schedules!!)
            jsonRegistry.add("schedules", schedules)
        }

        var attention = JsonArray()
        if (!item.attention.isNullOrEmpty()) {
            attention = putHorary(item.attention!!)
            jsonRegistry.add("attention", attention)
        }

        var masses = JsonArray()
        if (!item.masses.isNullOrEmpty()) {
            masses = putHorary(item.masses!!)
            jsonRegistry.add("masses", masses)
        }

        var services = JsonArray()
        if (!item.services.isNullOrEmpty()) {
            item.services!!.forEach {
                var type = JsonObject()
                type.addProperty("id", it.type.id)
                type.addProperty("name", it.type.name)
                var schedules = JsonArray()
                if (!it.schedules!!.isNullOrEmpty()) {
                    schedules = putHorary(it.schedules)
                }
                var jsonObject = JsonObject()
                jsonObject.add("type", type)
                jsonObject.add("schedules", schedules)
                jsonObject.addProperty("geared_toward", it.description)
                jsonObject.addProperty("description", it.geared_toward)
                services.add(jsonObject)
            }
            jsonRegistry.add("services", services)

        }
        GlobalScope.launch {
            repository.putIglesias(item.id!!, jsonRegistry)
        }
    }

    fun putHorary(item: List<HoraryModelItem>): JsonArray {
        var jsonArray = JsonArray()
        item.forEach {
            var jsonObject = JsonObject()
            var daysArray = JsonArray()
            it.days.forEach {
                var days = JsonObject()
                days.addProperty("id", it.id)
                days.addProperty("name", it.name)
                days.addProperty("checked", it.checked)
                daysArray.add(days)
            }
            jsonObject.add("days", daysArray)
            jsonObject.addProperty("hour_start", it.hour_start)
            jsonObject.addProperty("hour_end", it.hour_end)
            jsonArray.add(jsonObject)

        }
        return jsonArray

    }

    fun putIglesias(id: Int, editarIglesiasModel: EditarIglesiasModel) {
        val validateForm: HashMap<String, String> = HashMap()

        if (editarIglesiasModel.description.isEmpty()) {
            validateForm.put("description", "Error description")
        }

        if (editarIglesiasModel.email.isEmpty()) {
            validateForm.put("email", "Error email")
        }

        // TODO Verificar el formato que tendra este campos si una URL o como
        if (editarIglesiasModel.image.isEmpty()) {
            validateForm.put("image", "Error image")
        }

        if (editarIglesiasModel.services.isEmpty()) {
            validateForm.put("services", "Error services")
        }

        if (editarIglesiasModel.masses.isEmpty()) {
            validateForm.put("masses", "Error masses")
        }

        editarIglesiasModel.attention.forEach {
            /*if (it.day.isEmpty() || it.hourStart.isEmpty() || it.hourEnd.isEmpty()) {
                validateForm.put("masses", "Error masses")
            }*/
        }


        if (editarIglesiasModel.phone.isEmpty()) {
            validateForm.put("phone", "Error phone")
        }


        if (editarIglesiasModel.stream.isEmpty()) {//|| editarIglesiasModel.stream.hours.isEmpty()) {
            validateForm.put("stream", "Error stream")
        }

        if (editarIglesiasModel.priest.isEmpty()) {
            validateForm.put("priest", "Error priest")
        }

        if (editarIglesiasModel.bankAccount.isEmpty()) {
            validateForm.put("bankAccount", "Error bankAccount")
        }

        if (validateForm.size > 0) {
            this.validateForm.value = validateForm
        } else {
            GlobalScope.launch {
                //  repository.putIglesias(id, editarIglesiasModel)
            }
        }
    }

    fun getLocation() {
        repository.getLocation()
    }

    fun obtenerDetalle(id: Int) {
        GlobalScope.launch {
            repository.obtenerDetalle(id)
        }
    }
}