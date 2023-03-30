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

    fun validateFormRegisterDonor(
        name: String,
        comment: String,
        comedor : Int,
        correo : String,
        telefono: String,
        bancarios : String,
        tipo_don : String,
        id: Int? = 0
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

        }else if (telefono.length < 13) {
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
                telefono = telefono,
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
        direccion : String,
        comedor : String,
        name: String,
        telefono: String,
        multiusuario : MutableList<GuestModel>,
        correo : String,
        id: Int? = 0
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

        }else if (telefono.length < 13) {
            validateForm[Constants.KEY_PHONE] = Constants.INVALID_PHONE
        }

        validateForm.toString().log()
        // if (validateForm.size > 0) {
        if (validateForm.size > 0) {
            this.validateForm.value = validateForm
        } else {

            this.showLoaderView.value = true

            val eventRegisterModelVolunteer = Volunteer(
                user_id= userId,
                responsable = responsable,
                direccion = direccion,
                comedor_id = comedor,
                nombre_voluntario = name,
                telefono = telefono,
                multiuser = multiusuario,
                correo = correo
            )
            GlobalScope.launch {
                if (id == 0) {
                    repositoryEvent.saveVolunteer(eventRegisterModelVolunteer)
                } else {
                    id?.let{repositoryEvent.UpdateVolunteer(it, eventRegisterModelVolunteer) }
                }
            }
        }
    }
}