package mx.arquidiocesis.eamxevent.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.api.core.request.EAMXGenericRequest
import mx.arquidiocesis.eamxcommonutils.api.core.response.EAMXGenericResponse
import mx.arquidiocesis.eamxcommonutils.api.core.status.EAMXStatusRequestEnum
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.log
import mx.arquidiocesis.eamxevent.constants.Constants
import mx.arquidiocesis.eamxevent.repository.RepositoryEvent


class ViewModelEvent(val repositoryEvent: RepositoryEvent) : ViewModel() {

    val responseGeneric: EAMXGenericRequest<EAMXGenericResponse<EventResponse, String, Event>> =
        EAMXGenericRequest()
    val errorResponse = repositoryEvent.errorResponse
    val saveResponse = repositoryEvent.saveResponse
    var validateForm = MutableLiveData<HashMap<String, String>>()
    var showLoaderView = MutableLiveData<Boolean>()

    //Get Event
    val responseAllDin = repositoryEvent.allDiner
    val responseAllPan = repositoryEvent.allPantry
    val responseAllDon = repositoryEvent.allDonor
    val responseAllVol = repositoryEvent.allVolunteer

    fun getFine(): Boolean {
        return true
    }

    /**
    Metodo ejecutado desde EventFragment
     */
    fun requestEvent(requestModel: Event) {
        responseGeneric.postValue(
            EAMXGenericResponse(
                EAMXStatusRequestEnum.LOADING,
                requestData = requestModel
            )
        )
        repositoryEvent.callServiceEvent(requestModel, observeEventResponse())
    }

    fun requestAllDiner(dinerId: Int) {
        viewModelScope.launch {
            val response = repositoryEvent.getAllDiner(dinerId)
            if (response.sucess)
                responseAllDin.postValue(response.data ?: listOf())
            else
                repositoryEvent.errorResponse.postValue(response.exception?.message)
        }
    }

    //Get by id and get all
    fun requestAllPantry(pantryId: Int) {
        viewModelScope.launch {
            val response = repositoryEvent.getAllPantry(pantryId)
            if (response.sucess)
                responseAllPan.postValue(response.data ?: listOf())
            else
                repositoryEvent.errorResponse.postValue(response.exception?.message)
        }
    }

    fun requestAllDonorbyDiner(dinerId: Int, type: String) {
        viewModelScope.launch {
            val response = repositoryEvent.getDonorbyDiner(dinerId, type)
            if (response.sucess)
                responseAllDon.postValue(response.data ?: listOf())
            else
                repositoryEvent.errorResponse.postValue(response.exception?.message)
        }
    }

    fun requestAllVolunteerbyDiner(dinerId: Int, type: String) {
        viewModelScope.launch {
            val response = repositoryEvent.getVolunteerbyDiner(dinerId, type)
            if (response.sucess)
                responseAllVol.postValue(response.data ?: listOf())
            else
                repositoryEvent.errorResponse.postValue(response.exception?.message)
        }
    }

    private fun observeEventResponse() =
        Observer<EAMXGenericResponse<EventResponse, String, Event>> {
            responseGeneric.postValue(it)
        }

    fun validateFormRegister(
        name: String,
        user_id: Int,
        schedule: MutableList<Schedules>,
        responsability: String,
        email: String,
        phone: String,
        address: String,
        longitude: String,
        latitude: String,
        amount: String,
        requeriments: String,
        volunteers: Int,
        donors: ArrayList<Int> = ArrayList(),
        status: Int,
        id: Int? = 0,
        zone_id: Int,

        ) {
        val userId =
            eamxcu_preferences.getData(EAMXEnumUser.USER_ID.name, EAMXTypeObject.INT_OBJECT) as Int
        val email =
            eamxcu_preferences.getData(
                EAMXEnumUser.USER_EMAIL.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String
        val phone =
            eamxcu_preferences.getData(
                EAMXEnumUser.USER_PHONE.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String

        val validateForm: HashMap<String, String> = HashMap()
        var descriptionValidate = ""

        if (name.isEmpty())
            validateForm[Constants.KEY_NAME] = Constants.EMPTY_FIELD

        val filter = schedule[0].days?.filter { it.checked }

        if (filter != null) {
            if (filter.size == 0)
                validateForm[Constants.KEY_DAYS] = Constants.EMPTY_FIELD
        }
        // if ((schedule[0].days.filter { it.checked == true }).size == 0)

        if (schedule[0].hour_start == "00:00")
            validateForm[Constants.KEY_HOUR_FIRST] = Constants.EMPTY_FIELD

        if (schedule[0].hour_end == "00:00")
            validateForm[Constants.KEY_HOUR_END] = Constants.EMPTY_FIELD

        if (zone_id == 0)
            validateForm[Constants.KEY_ZONE] = Constants.EMPTY_FIELD


        if (responsability.isEmpty())
            validateForm[Constants.KEY_RESPONSABILITY] = Constants.EMPTY_FIELD

        if (email.isEmpty()) {
            validateForm[Constants.KEY_EMAIL] = Constants.EMPTY_FIELD
        } else {
            if (!Constants.EMAIL_ADDRESS.matcher(email).matches())
                validateForm[Constants.KEY_EMAIL] = Constants.INVALID_EMAIL
        }

        if (phone.isEmpty())
            validateForm[Constants.KEY_PHONE] = Constants.EMPTY_FIELD

        if (phone.length < 10)
            validateForm[Constants.KEY_PHONE] = Constants.INVALID_PHONE

        if (address.isEmpty())
            validateForm[Constants.KEY_ADDRESS] = Constants.EMPTY_FIELD

        if (longitude.isEmpty())
            validateForm[Constants.KEY_LONGITUDE] = Constants.EMPTY_FIELD

        if (latitude.isEmpty())
            validateForm[Constants.KEY_LATITUDE] = Constants.EMPTY_FIELD

        if (requeriments.isEmpty()) {
            descriptionValidate =
                "Presentarse puntual para garantizar el servicio. No asistir en estado de ebriedad o bajo la influencia de estupefacientes. Seguir las indicaciones de los administradores en todo momento. Actitud de respeto y cordialidad con el resto de los participantes."
        } else {
            descriptionValidate = requeriments
        }

        validateForm.toString().log()
        // if (validateForm.size > 0) {
        if (validateForm.size > 0) {
            this.validateForm.value = validateForm
        } else {

            this.showLoaderView.value = true

            val eventRegisterModel = Event(
                name = name,
                user_id = userId,
                schedule = schedule,
                responsability = responsability,
                email = email,
                phone = phone,
                address = address,
                longitude = longitude,
                latitude = latitude,
                amount = amount,
                requirements = descriptionValidate,
                volunteers = volunteers,
                donors = donors,
                zone_id = zone_id,
                status = status
            )
            GlobalScope.launch {
                if (id == 0) {
                    repositoryEvent.saveEventDiner(eventRegisterModel)
                } else {
                    id?.let { repositoryEvent.UpdateEventDiner(it, eventRegisterModel) }
                }
            }
        }
    }

    //Save and update of pantry
    fun validateFormRegisterPantry(
        schedule: MutableList<Schedules>,
        responsability: String,
        email: String,
        phone: String, //¿No existe?
        address: String,
        longitude: String,
        latitude: String,
        zone_id: Int,
        status: Int, //Not validate, ¿no existe?
        required_armed: Int, //Not validate if is 0
        required_delivery: Int, //Not validate if is 0
        required_donor: Int, //Not validate if is 0
        distributed: String?,
        received: Process, //
        armed: Process,
        delivery: Process,
        description_requirements: String,
        address_delivery: String,
        requirements_donor: String,
        longitude_delivery: String,
        latitude_delivery: String,
        id: Int = 0, //Solo para update
    ) {
        val validateForm: HashMap<String, String> = HashMap()

        //Responsable
        if (responsability.isEmpty()) {
            validateForm[Constants.KEY_RESPONSABILITY] = Constants.EMPTY_FIELD
        }

        //Contacto
        if (email.isEmpty()) {
            validateForm[Constants.KEY_EMAIL] = Constants.EMPTY_FIELD
        } else {
            if (!Constants.EMAIL_ADDRESS.matcher(email).matches())
                validateForm[Constants.KEY_EMAIL] = Constants.INVALID_EMAIL
        }
        if (phone.isEmpty())
            validateForm[Constants.KEY_PHONE] = Constants.EMPTY_FIELD

        if (phone.length < 10)
            validateForm[Constants.KEY_PHONE] = Constants.INVALID_PHONE

        if (address.isEmpty())
            validateForm[Constants.KEY_ADDRESS] = Constants.EMPTY_FIELD

        if (longitude.isEmpty())
            validateForm[Constants.KEY_LONGITUDE] = Constants.EMPTY_FIELD

        if (latitude.isEmpty())
            validateForm[Constants.KEY_LATITUDE] = Constants.EMPTY_FIELD

        if (zone_id == 0) {
            validateForm[Constants.KEY_ZONE] = Constants.EMPTY_FIELD
        }
        //Procesos
        if (received == Process())
        //if (received.isEmpty())
          validateForm[Constants.KEY_RECEIVED] = Constants.EMPTY_FIELD
        //Horarios
        if (schedule[0].hour_start == "00:00")
            validateForm[Constants.KEY_HOUR_FIRST_RECEIVED] = Constants.EMPTY_FIELD
        if (schedule[0].hour_end == "00:00")
            validateForm[Constants.KEY_HOUR_END_RECEIVED] = Constants.EMPTY_FIELD
        val filter_0 = schedule[0].days?.filter { it.checked }
        if (filter_0 != null)
            if (filter_0.size == 0)
                validateForm[Constants.KEY_DAYS_RECEIVED] = Constants.EMPTY_FIELD

        if (required_armed == 1) {
            if (armed == Process())
            //if (armed.isEmpty())
                validateForm[Constants.KEY_ARMED] = Constants.EMPTY_FIELD
            //Horarios
            if (schedule[1].hour_start == "00:00")
                validateForm[Constants.KEY_HOUR_FIRST_ARMED] = Constants.EMPTY_FIELD
            if (schedule[1].hour_end == "00:00")
                validateForm[Constants.KEY_HOUR_END_ARMED] = Constants.EMPTY_FIELD
            val filter_1 = schedule[1].days?.filter { it.checked }
            if (filter_1 != null)
                if (filter_1.size == 0)
                    validateForm[Constants.KEY_DAYS_ARMED] = Constants.EMPTY_FIELD
        }
        if (required_delivery == 1) {
            if (delivery == Process())
            //if (delivery.isEmpty())
                validateForm[Constants.KEY_DELIVERY] = Constants.EMPTY_FIELD
            //Horarios
            if (schedule[2].hour_start == "00:00")
                validateForm[Constants.KEY_HOUR_FIRST_DELIVERY] = Constants.EMPTY_FIELD
            if (schedule[2].hour_end == "00:00")
                validateForm[Constants.KEY_HOUR_END_DELIVERY] = Constants.EMPTY_FIELD
            val filter_2 = schedule[2].days?.filter { it.checked }
            if (filter_2 != null)
                if (filter_2.size == 0)
                    validateForm[Constants.KEY_DAYS_DELIVERY] = Constants.EMPTY_FIELD
        }
            //If is donor
            if (required_donor == 1)
                if (requirements_donor.isEmpty())
                    validateForm[Constants.KEY_REQUISIT] = Constants.EMPTY_FIELD

            //If is delivery
            if (required_delivery == 1)
                if (address.isEmpty())
                    validateForm[Constants.KEY_ADDRESS_DELIVERY] = Constants.EMPTY_FIELD
            if (longitude_delivery.isEmpty())
                validateForm[Constants.KEY_LONGITUDE_DELIVERY] = Constants.EMPTY_FIELD
            if (latitude_delivery.isEmpty())
                validateForm[Constants.KEY_LATITUDE_DELIVERY] = Constants.EMPTY_FIELD

            if (validateForm.size > 0) {
                this.validateForm.value = validateForm
            } else {
                this.showLoaderView.value = true
                val eventRegisterModel = Pantry(
                    user_id = eamxcu_preferences.getData(
                        EAMXEnumUser.USER_ID.name,
                        EAMXTypeObject.INT_OBJECT
                    ) as Int,
                    schedule = schedule,
                    responsability = responsability,
                    email = email,
                    phone = "+52${phone}",
                    address = address,
                    longitude = longitude,
                    latitude = latitude,
                    zone_id = zone_id,
                    status = status,
                    required_armed = required_armed,
                    required_delivery = required_delivery,
                    required_donor = required_donor,
                    distributed = distributed,
                    received = received,
                    armed = if (required_armed != 1) Process() else armed,
                    delivery = if (required_delivery != 1) Process() else delivery,
                    description_requirements = description_requirements,
                    address_delivery = address_delivery,
                    requirements_donor = requirements_donor,
                    longitude_delivery = longitude_delivery,
                    latitude_delivery = latitude_delivery,
                )
                GlobalScope.launch {
                    if (id == 0) {
                        repositoryEvent.saveEventPantry(eventRegisterModel)
                    } else {
                        repositoryEvent.UpdateEventPantry(id, eventRegisterModel)
                    }
                }
            }
        }

    fun validateFormRegisterDonor(
        name: String,
        comment: String,
        comedor: Int,
        correo: String,
        telefono: String,
        bancarios: String,
        tipo_don: String,
        id: Int? = 0,
    ) {
        val userId =
            eamxcu_preferences.getData(EAMXEnumUser.USER_ID.name, EAMXTypeObject.INT_OBJECT) as Int
        val email =
            eamxcu_preferences.getData(
                EAMXEnumUser.USER_EMAIL.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String
        val phone =
            eamxcu_preferences.getData(
                EAMXEnumUser.USER_PHONE.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String

        val validateForm: HashMap<String, String> = HashMap()
        var descriptionValidate = ""

        if (name.isEmpty())
            validateForm[Constants.KEY_NAME] = Constants.EMPTY_FIELD

        if (tipo_don.isEmpty())
            validateForm[Constants.KEY_TYPEDON] = Constants.EMPTY_FIELD

        /*
        if (email.isEmpty()) {
            validateForm[Constants.KEY_EMAIL] = Constants.EMPTY_FIELD
        } else {
            if (!Constants.EMAIL_ADDRESS.matcher(email).matches())
                validateForm[Constants.KEY_EMAIL] = Constants.INVALID_EMAIL
        }


         */
        if (telefono.isEmpty()) {
            validateForm[Constants.KEY_PHONE] = Constants.EMPTY_FIELD

        } else if (telefono.length < 10) {
            validateForm[Constants.KEY_PHONE] = Constants.INVALID_PHONE
        }

        if (comment.isEmpty())
            validateForm[Constants.KEY_COMMENT] = Constants.EMPTY_FIELD


        validateForm.toString().log()
        // if (validateForm.size > 0) {
        if (validateForm.size > 0) {
            this.validateForm.value = validateForm
        } else {

            this.showLoaderView.value = true

            val eventRegisterModel = Donor(
                nombre = name,
                user_id = userId,
                bancarios = bancarios,
                correo = correo,
                telefono = "+52${telefono}",
                comedor_id = comedor,
                comentarios = comment,
                tipo_don = tipo_don
            )
            GlobalScope.launch {
                if (id == 0) {
                    repositoryEvent.saveDonor(eventRegisterModel)
                } else {
                    id?.let { repositoryEvent.UpdateDonor(it, eventRegisterModel) }
                }
            }
        }
    }

    fun validateFormRegisterVolunteer(
        responsable: String,
        direccion: String,
        comedor: String,
        name: String,
        telefono: String,
        multiusuario: MutableList<GuestModel>,
        correo: String,
        id: Int? = 0,
    ) {
        val userId =
            eamxcu_preferences.getData(EAMXEnumUser.USER_ID.name, EAMXTypeObject.INT_OBJECT) as Int
        val email =
            eamxcu_preferences.getData(
                EAMXEnumUser.USER_EMAIL.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String
        val phone =
            eamxcu_preferences.getData(
                EAMXEnumUser.USER_PHONE.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String

        val validateForm: HashMap<String, String> = HashMap()

        if (name.isEmpty())
            validateForm[Constants.KEY_NAME] = Constants.EMPTY_FIELD

        if (correo.isEmpty()) {
            validateForm[Constants.KEY_EMAIL] = Constants.EMPTY_FIELD
        } else {
            if (!Constants.EMAIL_ADDRESS.matcher(email).matches())
                validateForm[Constants.KEY_EMAIL] = Constants.INVALID_EMAIL
        }

        if (telefono.isEmpty()) {
            validateForm[Constants.KEY_PHONE] = Constants.EMPTY_FIELD

        } else if (telefono.length < 10) {
            validateForm[Constants.KEY_PHONE] = Constants.INVALID_PHONE
        }

        validateForm.toString().log()
        // if (validateForm.size > 0) {
        if (validateForm.size > 0) {
            this.validateForm.value = validateForm
        } else {

            this.showLoaderView.value = true

            val eventRegisterModelVolunteer = Volunteer(
                user_id = userId,
                responsable = responsable,
                direccion = direccion,
                comedor_id = comedor,
                nombre_voluntario = name,
                telefono = "+52${telefono}",
                multiuser = multiusuario,
                correo = correo
            )
            GlobalScope.launch {
                if (id == 0) {
                    repositoryEvent.saveVolunteer(eventRegisterModelVolunteer)
                } else {
                    id?.let { repositoryEvent.UpdateVolunteer(it, eventRegisterModelVolunteer) }
                }
            }
        }
    }
}