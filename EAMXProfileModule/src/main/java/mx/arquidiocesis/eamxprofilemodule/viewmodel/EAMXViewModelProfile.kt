package mx.arquidiocesis.eamxprofilemodule.viewmodel

import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxprofilemodule.adapter.ActivitiesAdapter
import mx.arquidiocesis.eamxprofilemodule.adapter.ChurchAdapter
import mx.arquidiocesis.eamxprofilemodule.databinding.EamxProfileInfoFragmentBinding
import mx.arquidiocesis.eamxprofilemodule.model.*
import mx.arquidiocesis.eamxprofilemodule.model.update.base.*
import mx.arquidiocesis.eamxprofilemodule.model.update.userupdatecaselaicconsecratedreligious.UserLaic
import mx.arquidiocesis.eamxprofilemodule.model.update.userupdatecaselaicconsecratedreligious.UserLaicConsecratedReligious
import mx.arquidiocesis.eamxprofilemodule.model.update.userupdatecasesinglemarriedwidower.ServicesProvided
import mx.arquidiocesis.eamxprofilemodule.model.update.userupdatecasesinglemarriedwidower.UserSingleMarriedWidower
import mx.arquidiocesis.eamxprofilemodule.model.update.userupdatediacono.UserDiacono
import mx.arquidiocesis.eamxprofilemodule.repository.RepositoryProfile


const val SELECT_ITEM = "Selecciona"
const val SELECT_OTHER = "Otros"
const val SINGLE = "Soltero"
const val MARRIED = "Casado"
const val WIDOWER = "Viudo"
const val LAIC_CONSECRATED = "Laico consagrado"
const val LAIC = "Laico"
const val RELIGIOUS = "Religioso (a)"
const val DIACO = "Diácono (Transitorio o permanente)"
const val PRIEST = "Sacerdote"

class EAMXViewModelProfile(val repositoryProfile: RepositoryProfile) : ViewModel() {

    val text_message_requiered = "Campo obligatorio"
    val errorResponse = repositoryProfile.errorResponse
    val updateMessageResponse = repositoryProfile.updateMessage

    val responseUserDetail = repositoryProfile.userDetailResponse
    val congregationsResponse = repositoryProfile.congregationsResponse
    val servicesModel = repositoryProfile.providedServicesResponse
    val stateLifeModel = repositoryProfile.lifeStateResponse
    val topicsModel = repositoryProfile.topicResponse
    val prefixModel = repositoryProfile.prefixResponse
    val errorForm = MutableLiveData<HashMap<Int, String>>()
    private var userId: Int? = null

    var congregationItem: CongregationModel? = CongregationModel("", 0)
    private var interestTopicList: List<InterestTopic> = listOf()

    private fun getUserId(): Int {
        userId = userId ?: eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID.name,
            EAMXTypeObject.INT_OBJECT
        ) as Int
        return userId ?: 0
    }

    fun saveData(binding: EamxProfileInfoFragmentBinding, adapter: ActivitiesAdapter) {
        interestTopicList = this.getTopicUser(adapter)
        eamxcu_preferences.saveData("PHONE_NUMBER", binding.etTelephoneNumber.text.toString())
        eamxcu_preferences.saveData(
            "LIFE_STATUS",
            (binding.spStyleLife.selectedItem as DataWithDescriptionCode).id
        )
        eamxcu_preferences.saveData("INTEREST_TOPICS", Gson().toJson(interestTopicList))
    }

    fun getUserDetail() {
        GlobalScope.launch {
            if (getUserId() > 0) {
                repositoryProfile.getUserDetail(getUserId())
            }
        }
    }

    fun getUserDetailAndSaveProfile(useResponse: Boolean = false) {
        GlobalScope.launch {
            if (getUserId() > 0) {
                repositoryProfile.getUserDetailAndSaveProfile(getUserId(), useResponse)
            }
        }
    }

    fun getCongregations() {
        GlobalScope.launch {
            repositoryProfile.getCongregations()
        }
    }

    fun getStateLife() {
        GlobalScope.launch {
            repositoryProfile.getLifeSateList()
        }
    }

    fun getPrefix(lifeState: String) {
        GlobalScope.launch {
            repositoryProfile.getCatalogPrefix(lifeState,userId ?: 0)
        }
    }

    fun getTopics() {
        GlobalScope.launch {
            repositoryProfile.getTopics()
        }
    }

    fun getProvidedServices() {
        GlobalScope.launch {
            repositoryProfile.getProvidedServices()
        }
    }

    fun updateUser(
        binding: EamxProfileInfoFragmentBinding,
        adapter: ActivitiesAdapter,
        idComunnity: Int?,
        diaconoChurchList: List<ChurchModel>,
        churchList: List<ChurchAndDescriptionModel>,
        listener: () -> Unit
    ) {

        if (!validInputs(binding)) {
            return
        }

        if (!validSpinners(binding, adapter, churchList)) {
            return
        }

        if (!validDataByUser(
                binding,
                binding.spStyleLife.selectedItem,
                diaconoChurchList,
                churchList
            )
        ) {
            return
        }


        val userName = eamxcu_preferences.getData(
            EAMXEnumUser.USER_EMAIL.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String
        val styleUserItem = binding.spStyleLife.selectedItem as DataWithDescriptionCode
        val prefixUserItem = if(binding.spPrefix.selectedItem != null) binding.spPrefix.selectedItem as DataWithDescription else null
        this.interestTopicList = this.getTopicUser(adapter)
        val numberPhone = "${binding.etTelephoneNumber.text}"
        when (styleUserItem.description) {
            SINGLE, MARRIED, WIDOWER -> {
                listener()
                val servicesProvidedList = this.setServicesProvidedList(churchList)
                val user = UserSingleMarriedWidower(
                    id = getUserId(),
                    username = userName,
                    name = binding.etName.text.toString(),
                    first_surname = binding.etFatherSurname.text.toString(),
                    second_surname = binding.etMotherSurname.text.toString(),
                    phone_number = numberPhone,
                    email = binding.etEmail.text.toString(),
                    life_status = LifeStatus(id = styleUserItem.id),
                    interest_topics = this.interestTopicList,
                    services_provided = servicesProvidedList
                )
                GlobalScope.launch {
                    repositoryProfile.updateUserSingleMarriedWidower(user)
                }
            }
            LAIC_CONSECRATED, RELIGIOUS -> {
                listener()
                val user = UserLaicConsecratedReligious(
                    id = getUserId(),
                    username = userName,
                    name = binding.etName.text.toString(),
                    first_surname = binding.etFatherSurname.text.toString(),
                    second_surname = binding.etMotherSurname.text.toString(),
                    phone_number = numberPhone,
                    location_id = idComunnity,
                    email = binding.etEmail.text.toString(),
                    life_status = LifeStatus(id = styleUserItem.id),
                    interest_topics = this.interestTopicList,
                    congregation = Congregation(id = congregationItem?.id ?: -1),
                    pastoral_work = binding.etPastoralActivity.text.toString(),
                    profile = if (binding.swResponsibleCommunity.isChecked) "COMMUNITY_RESPONSIBLE" else "COMMUNITY_MEMBER",
                    prefix = Prefix(id = prefixUserItem?.id ?: 0)
                )
                GlobalScope.launch {
                    repositoryProfile.updateUserLaicConsecratedReligious(user)
                }
            }
           LAIC -> {
               listener()
               val congregation = null

               val location =
                   when {
                       binding.rbYes.isChecked -> null
                       idComunnity == 0 -> null
                       else -> idComunnity
                   }

               val pastoralWork = when {
                   binding.etPastoralActivity.text.toString()
                       .isNotEmpty() -> binding.etPastoralActivity.text.toString()
                   else -> null
               }

               val isAdmin =  if (binding.rbYesC.isChecked){
                   binding.swResponsibleCommunity.isChecked
               }else null

               val servicesProvidedList = this.setServicesProvidedList(churchList)
                val user = UserLaic(
                    id = getUserId(),
                    username = userName,
                    name = binding.etName.text.toString(),
                    first_surname = binding.etFatherSurname.text.toString(),
                    second_surname = binding.etMotherSurname.text.toString(),
                    phone_number = numberPhone,
                    location_id = location,
                    email = binding.etEmail.text.toString(),
                    life_status = LifeStatus(id = styleUserItem.id),
                    interest_topics = this.interestTopicList,
                    congregation = congregation,
                    pastoral_work = pastoralWork,
                    is_admin = isAdmin,
                    service_provider = if (binding.rbYes.isChecked) "CHURCH" else if (binding.rbYesC.isChecked) "COMMUNITY" else "NO",
                    services_provided = servicesProvidedList
                )
                GlobalScope.launch {
                    repositoryProfile.updateUserLaic(user)
                }
            }
            DIACO -> {
                listener()
                val locations = this.getLocations(diaconoChurchList)
                val user = UserDiacono(
                    id = getUserId(),
                    username = userName,
                    name = binding.etName.text.toString(),
                    first_surname = binding.etFatherSurname.text.toString(),
                    second_surname = binding.etMotherSurname.text.toString(),
                    phone_number = numberPhone,
                    email = binding.etEmail.text.toString(),
                    life_status = LifeStatus(id = styleUserItem.id),
                    interest_topics = this.interestTopicList,
                    locations = locations,
                    prefix = Prefix(id = prefixUserItem?.id ?: 0)
                )
                GlobalScope.launch {
                    repositoryProfile.updateUserDiacono(user)
                }
            }
            PRIEST -> {
                listener()
            }
        }
    }

    private fun setServicesProvidedList(churchList: List<ChurchAndDescriptionModel>): List<ServicesProvided> {
        return churchList.map { item ->
            ServicesProvided(
                service_id = if(item.activity.id == 0) null else item.activity.id,
                location_id = item.church.id,
                service_description = if (item.activity.description == SELECT_OTHER) item.activity.otherDescription else null
            )
        }
    }

    private fun validDataByUser(
        binding: EamxProfileInfoFragmentBinding,
        styleLifeData: Any?,
        diaconoChurchList: List<ChurchModel>,
        churchList: List<ChurchAndDescriptionModel>
    ): Boolean {
        val styleLife = styleLifeData as DataWithDescriptionCode
        if(binding.rbYes.isChecked){
            if(churchList.isEmpty()){
                errorResponse.value = "Debe seleccionar una iglesia"
                return false
            }
        }

        if(binding.rbYesC.isChecked ){
            if(binding.etSearchCommunity.text.toString() == ""){
                errorResponse.value = "Debe seleccionar una comunidad"
                return false
            }
        }

        when (styleLife.description) {
            SINGLE, MARRIED, WIDOWER -> {
                if (binding.rbYes.isChecked && churchList.isEmpty()) {
                    errorResponse.value = "Falta iglesia/comunidad en la que prestas servicio"
                    return false
                }
                if (binding.rbYes.isChecked && churchList.isEmpty()) {
                    errorResponse.value = "Falta iglesia/comunidad en la que prestas servicio"
                    return false
                }

                for (item in churchList) {
                    if (item.activity.description == SELECT_ITEM) {
                        errorResponse.value = "Falta completar tu actividad para la iglesia"
                        return false
                    }

                    val otherDescription = item.activity.otherDescription ?: ""
                    if (item.activity.description == SELECT_OTHER && otherDescription.isEmpty()) {
                        errorResponse.value = "Falta proporcionar una descripción para tu actividad"
                        return false
                    }
                }
            }
            LAIC_CONSECRATED, RELIGIOUS -> {
                if (congregationItem == null) {
                    errorResponse.value = "Falta congregación en la que prestas servicio"
                    return false
                }
            }
            DIACO -> {
                if (diaconoChurchList.isEmpty()) {
                    errorResponse.value = "Falta iglesia en la que prestas servicio"
                    return false
                }
            }
        }

        return true
    }

    private fun validInputs(binding: EamxProfileInfoFragmentBinding): Boolean {
        val mapErrors = HashMap<Int, String>()

        if (binding.etName.text.isEmpty()) {
            mapErrors[binding.etName.id] = "Falta tu nombre(s)"
        }

        if (binding.etFatherSurname.text.isEmpty()) {
            mapErrors[binding.etFatherSurname.id] = "Falta tu apellido paterno"
        }

        if (binding.etTelephoneNumber.text.isEmpty()) {
            mapErrors[binding.etTelephoneNumber.id] = "Falta tu número de celular"
        }

        if (binding.etTelephoneNumber.text.length.minus(13) != 0) {
            mapErrors[binding.etTelephoneNumber.id] = "El teléfono debe ser de 10 dígitos"
        }

        errorForm.value = mapErrors

        return mapErrors.isEmpty()
    }


    private fun validSpinners(
        binding: EamxProfileInfoFragmentBinding,
        adapter: ActivitiesAdapter,
        churchList: List<ChurchAndDescriptionModel>
    ): Boolean {

        val typeUserUpdateSelect = binding.spStyleLife.selectedItem

        if (typeUserUpdateSelect == null || typeUserUpdateSelect.toString().equals(SELECT_ITEM)) {
            errorResponse.value = "Falta completar tu estado de vida"
            return false
        }

        val lifeStyleSelect = binding.spStyleLife.selectedItem as DataWithDescriptionCode
        if (lifeStyleSelect.id == 0) {
            errorResponse.value = "Falta completar tu estado de vida"
            return false
        }
        if (binding.rbYes.isChecked && churchList.isNotEmpty()) {
            for (item in churchList) {
                if (item.activity.description == SELECT_ITEM) {
                    errorResponse.value = "Falta completar el servicio que prestas"
                    return false
                }
            }
//            val servicesProvides = binding.spServicesChurch.selectedItem as DataWithDescription
//            Log.d("servicesProvides", servicesProvides.toString())
//            if ( servicesProvides.description.equals(SELECT_ITEM)) {
//                errorResponse.value = "Falta completar tu servicio que prestas"
//                return false
//            }
        }
        if (binding.rbYesC.isChecked && churchList.isNotEmpty()) {
            for (item in churchList) {
                if (item.activity.description == SELECT_ITEM) {
                    errorResponse.value = "Falta completar el servicio que prestas"
                    return false
                }
            }
        }




        if(binding.spPrefix.isVisible) {
            val prefixSelect = binding.spPrefix.selectedItem as DataWithDescription
            if (prefixSelect.id == 0) {
                errorResponse.value = "Falta completar tu prefijo"
                return false
            }
        }

        val aux = this.getTopicUser(adapter)
        if (aux.isEmpty()) {
            errorResponse.value = "Falta elegir los temas de interés"
            return false
        }

        return true
    }

    private fun getTopicUser(adapter: ActivitiesAdapter): List<InterestTopic> {
        return adapter.getList().map { InterestTopic(id = it.id) }
    }

    private fun getLocations(list: List<ChurchModel>): List<location> {
        return list.map { item -> location(id = item.id) }
    }

    fun executeUpdateProfile(data: Any?): Boolean {
        val typeUser = data as DataWithDescriptionCode

        when (typeUser.description) {
            SELECT_ITEM,
            SINGLE, MARRIED, WIDOWER,
            LAIC_CONSECRATED, RELIGIOUS, LAIC,
            DIACO -> return true
            PRIEST -> return false
        }
        return false
    }

    fun userCompleteProfile() {
        eamxcu_preferences.saveData(EAMXEnumUser.USER_NEED_COMPLETE_PROFILE.name, false)
    }

    fun userNeedCompletedProfile(): Boolean {
        return eamxcu_preferences.getData(
            EAMXEnumUser.USER_NEED_COMPLETE_PROFILE.name,
            EAMXTypeObject.BOOLEAN_OBJECT
        ) as Boolean
    }

    fun setUpdateChurchInModuleMyChurch() {
        eamxcu_preferences.saveData(EAMXEnumUser.CHURCH_UPDATE_FROM_PROFILE.name, true)
    }
}