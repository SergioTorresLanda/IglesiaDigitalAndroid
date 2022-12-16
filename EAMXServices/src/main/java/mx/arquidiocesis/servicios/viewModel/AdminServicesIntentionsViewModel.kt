package mx.arquidiocesis.servicios.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXMessageError
import mx.arquidiocesis.eamxcommonutils.common.EAMXProfile
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.servicios.model.admin.api.mapToAdminIntentionDetailModel
import mx.arquidiocesis.servicios.model.admin.api.mapToAdminIntentionGeneralModel
import mx.arquidiocesis.servicios.model.admin.view.AdminIntentionDetailModel
import mx.arquidiocesis.servicios.model.admin.view.AdminIntentionGeneralModel
import mx.arquidiocesis.servicios.repository.AdminServicesIntentionsRepository

class AdminServicesIntentionsViewModel(private val repository: AdminServicesIntentionsRepository) :
    ViewModel() {

    private val responseIntentions = MutableLiveData<List<AdminIntentionGeneralModel>>()
    val responseIntentionsP: LiveData<List<AdminIntentionGeneralModel>>
        get() = responseIntentions

    private val responseIntentionDetail = MutableLiveData<AdminIntentionDetailModel>()
    val responseIntentionDetailModelP: LiveData<AdminIntentionDetailModel>
        get() = responseIntentionDetail

    private val responseUrlDownloadIntention = MutableLiveData<String>()
    val responseUrlDownloadIntentionP: LiveData<String>
        get() = responseUrlDownloadIntention

    private val error = MutableLiveData<EAMXMessageError>()
    val errorP: LiveData<EAMXMessageError>
        get() = error

    var locationId = 0
    var userId = 0
    var profile = ""

    init {

        userId = eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID.name,
            EAMXTypeObject.INT_OBJECT
        ) as Int

        profile = eamxcu_preferences.getData(
            EAMXEnumUser.USER_PROFILE.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String

        locationId = when (profile) {
            EAMXProfile.CommunityResponsible.rol,
            EAMXProfile.CommunityAdmin.rol -> {
                (eamxcu_preferences.getData(
                    EAMXEnumUser.USER_COMMUNITY_ID.name,
                    EAMXTypeObject.INT_OBJECT
                ) as Int)
            }
            else -> {
                val churchTemp =
                (eamxcu_preferences.getData(
                    EAMXEnumUser.CHURCH.name,
                    EAMXTypeObject.STRING_OBJECT
                ) as String)

                if(churchTemp.isNotEmpty()){
                    churchTemp.toInt()
                }else{
                    0
                }
            }
        }
    }

    fun getIntentions(date: String) {
        GlobalScope.launch {
            if (locationId != 0) {
                val response = repository.getIntentions(userId, locationId, date)
                if (response.sucess) {
                    response.data?.let {
                        responseIntentions.postValue(it.mapToAdminIntentionGeneralModel())
                    } ?: kotlin.run {
                        error.postValue(
                            EAMXMessageError(
                                message = "No tenemos registrada ninguna solicitud por el momento, consulta más tarde el módulo para dar seguimiento a las intenciones.",
                                back = true
                            )
                        )
                    }
                } else {
                    error.postValue(
                        EAMXMessageError(
                            message = response.exception?.message,
                            back = true
                        )
                    )
                }
            } else {
                error.postValue(
                    EAMXMessageError(
                        message = "No tienes una comunidad o iglesia asignada para este usuario.",
                        back = true
                    )
                )
            }
        }
    }

    fun getIntentionDetail(date: String, time: String) {
        GlobalScope.launch {
            val response = repository.getIntentionsDetail(userId, profile, date, time)

            if (response.sucess) {
                if (response.data?.intentions?.isNotEmpty() == true) {
                    response.data?.let {
                        responseIntentionDetail.postValue(it.mapToAdminIntentionDetailModel())
                    } ?: kotlin.run {
                        error.postValue(
                            EAMXMessageError(
                                message = "La intención seleccionada no cuenta con información por el momento, consulta más tarde el módulo para dar seguimiento a las intenciones.",
                                back = true
                            )
                        )
                    }
                } else {
                    error.postValue(
                        EAMXMessageError(
                            message = "No cuentas con intenciones para este horario.",
                            back = true
                        )
                    )
                }
            } else {
                error.postValue(
                    EAMXMessageError(
                        message = response.exception?.message,
                        back = true
                    )
                )
            }
        }
    }

    fun getUrlDownloadIntention(date: String, time: String) {
        GlobalScope.launch {
            val response = repository.getUrlDownloadIntention(userId, profile, date, time)

            if (response.sucess) {
                response.data?.let {
                    responseUrlDownloadIntention.postValue(it.url)
                } ?: kotlin.run {
                    error.postValue(
                        EAMXMessageError(
                            message = "El documento no puede ser descargado, intenta de nuevo.",
                            back = true
                        )
                    )
                }
            } else {
                error.postValue(
                    EAMXMessageError(
                        message = response.exception?.message,
                        back = true
                    )
                )
            }
        }
    }
}
