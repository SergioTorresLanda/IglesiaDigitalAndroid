package mx.arquidiocesis.registrosacerdote.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.registrosacerdote.constants.Constants
import mx.arquidiocesis.registrosacerdote.model.ActivitiesModel
import mx.arquidiocesis.registrosacerdote.model.catalog.InterestTopic
import mx.arquidiocesis.registrosacerdote.model.update.base.BaseOnlyId
import mx.arquidiocesis.registrosacerdote.model.update.userupdatepriest.UserPriest
import mx.arquidiocesis.registrosacerdote.repository.Repository


class PriestRegisterViewModel(val repository: Repository) : ViewModel() {

    var errorResponse = repository.errorResponse
    var activitiesLiveData = repository.activitiesResponse
    var registerResponse = repository.registerResponse
    var validateForm = MutableLiveData<HashMap<String, String>>()
    var showLoaderView = MutableLiveData<Boolean>()
    val congregationsResponse = repository.congregationsResponse

    fun getActivitiesList() {
        GlobalScope.launch {
            repository.getActivitiesList()
        }
    }

    fun getCongregations (){
        GlobalScope.launch {
            repository.getCongregations()
        }
    }

    fun validateFormPriestRegister(
        name: String,
        firstSurname: String,
        secondSurname: String,
        description: String,
        birthDate: String,
        ordinationDate: String,
        email: String,
        activities: List<ActivitiesModel>,
        congregation: BaseOnlyId,
        stream: String,
        flagDiocesanOrReligious: Int,
        lifeStatus: BaseOnlyId,
        interestTopics: List<InterestTopic>
    ) {
        val validateForm: HashMap<String, String> = HashMap()
        var descriptionValidate = ""
        var streamValidate = ""

        if (name.isEmpty())
            validateForm[Constants.KEY_NAME] = Constants.EMPTY_FIELD

        if (firstSurname.isEmpty())
            validateForm[Constants.KEY_FIRST_SURNAME] = Constants.EMPTY_FIELD

        if (secondSurname.isEmpty())
            validateForm[Constants.KEY_SECOND_SURNAME] = Constants.EMPTY_FIELD

        if (email.isEmpty()) {
            validateForm[Constants.KEY_EMAIL] = Constants.EMPTY_FIELD
        } else {
            if (!Constants.EMAIL_ADDRESS.matcher(email).matches())
                validateForm[Constants.KEY_EMAIL] = Constants.INVALID_EMAIL
        }

        if (birthDate.isEmpty() || birthDate == Constants.DATE_MACK)
            validateForm[Constants.KEY_BIRTHDATE] = Constants.EMPTY_FIELD

        if (ordinationDate.isEmpty() || birthDate == Constants.DATE_MACK)
            validateForm[Constants.KEY_ORDINATION] = Constants.EMPTY_FIELD

        if (congregation.id == 0 && flagDiocesanOrReligious != 0) {
            validateForm[Constants.KEY_CONGREGATION] = Constants.EMPTY_FIELD
        }

        if(activities.isEmpty()){
            validateForm[Constants.KEY_ACTIVITY] = Constants.KEY_ACTIVITY
        }

        if(description.isEmpty()){
            descriptionValidate = "Without"
        } else {
            descriptionValidate = description
        }

        if(stream.isEmpty()){
            streamValidate = "Without"
        }else{
            streamValidate = stream
        }


        val userId =
            eamxcu_preferences.getData(EAMXEnumUser.USER_ID.name, EAMXTypeObject.INT_OBJECT) as Int
        val userName =
            eamxcu_preferences.getData(EAMXEnumUser.USER_EMAIL.name, EAMXTypeObject.STRING_OBJECT) as String
        val phoneNumber = eamxcu_preferences.getData(
            EAMXEnumUser.USER_PHONE.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String

        if (validateForm.size > 0) {
            this.validateForm.value = validateForm
        } else {

            this.showLoaderView.value = true

            val priestRegisterModel: UserPriest
            //Diocesano
            if (flagDiocesanOrReligious == 0) {
                priestRegisterModel = UserPriest(
                    id = userId,
                    username = userName,
                    email = email,
                    first_surname = firstSurname,
                    name = name,
                    phone_number = phoneNumber,
                    second_surname = secondSurname,
                    life_status = lifeStatus,
                    interest_topics = interestTopics,
                    birthdate = birthDate,
                    ordination_date = ordinationDate,
                    description = descriptionValidate,
                    position = "Without",
                    stream = streamValidate,
                    congregation = null,
                    activities = activities
                )
            } else {
                priestRegisterModel = UserPriest(
                    id = userId,
                    username = userName,
                    email = email,
                    first_surname = firstSurname,
                    name = name,
                    phone_number = phoneNumber,
                    second_surname = secondSurname,
                    life_status = lifeStatus,
                    interest_topics = interestTopics,
                    birthdate = birthDate,
                    ordination_date = ordinationDate,
                    description = descriptionValidate,
                    position = "Without",
                    stream = streamValidate,
                    congregation = congregation,
                    activities = activities
                )
            }

            GlobalScope.launch {
                repository.updateUsePriest(priestRegisterModel)
            }
        }
    }
}