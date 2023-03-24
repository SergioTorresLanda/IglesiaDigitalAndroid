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

class ViewModelDonor(val repositoryEvent: RepositoryEvent) : ViewModel() {

    val responseGeneric: EAMXGenericRequest<EAMXGenericResponse<EventResponse, String, Event>> =
        EAMXGenericRequest()
    val errorResponse = repositoryEvent.errorResponse
    val saveResponse = repositoryEvent.saveResponse
    var validateForm = MutableLiveData<HashMap<String, String>>()
    var showLoaderView = MutableLiveData<Boolean>()

    //Get Event
    val responseAllDin = repositoryEvent.allDiner
    val responseAllDonor = repositoryEvent.allDonor

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

    fun requestAllDonor(donorId: Int) {
        viewModelScope.launch {
            val response = repositoryEvent.getAllDonor(donorId)
            if (response.sucess)
                responseAllDonor.postValue(response.data ?: listOf())
            else
                repositoryEvent.errorResponse.postValue(response.exception?.message)
        }

        //GlobalScope.launch {
        //   repositoryEvent.getAllDiner()
        //}
    }

    private fun observeEventResponse() =
        Observer<EAMXGenericResponse<EventResponse, String, Event>> {
            responseGeneric.postValue(it)
        }

    fun validateFormRegister(
        bancarios: String,
        comentarios: String,
        correo: String,
        comedor_id: Int,
        nombre: String,
        telefono: String,
        tipo_don: String,
        user_id: Int,
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
        /*
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

         */

        validateForm.toString().log()
        // if (validateForm.size > 0) {
        if (validateForm.size > 0) {
            this.validateForm.value = validateForm
        } else {

            this.showLoaderView.value = true

            val eventRegisterModel = Donor(
                bancarios = bancarios,
                comentarios = comentarios,
                correo = correo,
                comedor_id = comedor_id,
                nombre = nombre,
                telefono = telefono,
                tipo_don = tipo_don,
                user_id = userId

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
        direccion: String,
        multiuser: String,
        correo: String,
        comedor_id: String,
        nombre_voluntario: String,
        telefono: String,
        responsable: String,
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
        /*
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

         */

        validateForm.toString().log()
        // if (validateForm.size > 0) {
        if (validateForm.size > 0) {
            this.validateForm.value = validateForm
        } else {

            this.showLoaderView.value = true

            val eventRegisterModel = Volunteer(
                correo = correo,
                direccion = direccion,
                multiuser = multiuser,
                comedor_id = comedor_id,
                nombre_voluntario = nombre_voluntario,
                responsable = responsable,
                telefono = telefono

            )
            GlobalScope.launch {
                if (id == 0) {
                    repositoryEvent.saveVolunteer(eventRegisterModel)
                } else {
                    id?.let { repositoryEvent.UpdateVolunteer(it, eventRegisterModel) }
                }
            }
        }
    }
}