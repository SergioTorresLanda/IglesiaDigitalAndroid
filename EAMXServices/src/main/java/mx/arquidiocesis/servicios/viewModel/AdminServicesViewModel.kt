package mx.arquidiocesis.servicios.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXMessageError
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.servicios.model.*
import mx.arquidiocesis.servicios.model.admin.api.*
import mx.arquidiocesis.servicios.model.admin.view.*
import mx.arquidiocesis.servicios.model.admin.view.StatusService.NOTHING
import mx.arquidiocesis.servicios.model.admin.view.StatusService.REJECTED
import mx.arquidiocesis.servicios.model.admin.view.StatusService.TODAY
import mx.arquidiocesis.servicios.repository.AdminServicesRepository

class AdminServicesViewModel(private val repository: AdminServicesRepository) : ViewModel() {

    private val responseServices = MutableLiveData<List<AdminServiceModel>>()
    val responseServicesP : LiveData<List<AdminServiceModel>>
        get() = responseServices

    private val responseHistory = MutableLiveData<List<AdminHistoryModel>>()
    val responseHistoryP : LiveData<List<AdminHistoryModel>>
        get() = responseHistory

    private val responseServicesDetail = MutableLiveData<AdminDetailModel>()
    val responseServicesDetailP : LiveData<AdminDetailModel>
        get() = responseServicesDetail

    private val responseStatus = MutableLiveData<List<StatusServices>>()
    val responseStatusP : LiveData<List<StatusServices>>
        get() = responseStatus

    private val responseExecuteChangeStatus = MutableLiveData<Boolean>()
    val responseExecuteChangeStatusP : LiveData<Boolean>
        get() = responseExecuteChangeStatus

    private val responseDeleteService = MutableLiveData<Boolean>()
    val responseDeleteServiceP : LiveData<Boolean>
        get() = responseDeleteService

    private val responseAddComment = MutableLiveData<Boolean>()
    val responseAddCommentP : LiveData<Boolean>
        get() = responseAddComment

    private val error = MutableLiveData<EAMXMessageError>()
    val errorP : LiveData<EAMXMessageError>
        get() = error

    lateinit var changeStatus : String

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
    }

    fun getServices(){
        GlobalScope.launch {
            val response = repository.getServices(userId, profile)
            if(response.sucess){
                response.data?.let {
                    if(it.isNotEmpty()) {
                        responseServices.postValue(it.mapToAdminServiceModel())
                    }else{
                        error.postValue(EAMXMessageError(message = "No tenemos registrado ningún servicio por el momento, consulta más tarde el módulo para dar seguimiento a los servicios" , back = true))
                    }
                }?: kotlin.run {
                    error.postValue(EAMXMessageError(message = "No tenemos registrado ningún servicio por el momento, consulta más tarde el módulo para dar seguimiento a los servicios" , back = true))
                }
            }else{
                error.postValue(EAMXMessageError(message = response.exception?.message, back = true))
            }
        }
    }

    fun getHistory(status: String? = null){
        GlobalScope.launch {

            val response =
                status?.let{
                    when(status){
                        NOTHING -> {
                            return@launch
                        }
                        TODAY -> {
                            repository.getServices(userId, profile)
                        }
                        else ->   repository.getHistory(userId, profile, status)
                    }} ?:
                    kotlin.run {
                        repository.getHistory(userId, profile)
                    }


            if(response.sucess) {
                response.data?.let {
                    if(it.isNotEmpty()){
                    val itemMap = it.mapToAdminServiceModel()
                    val listGroup = itemMap.groupBy { data -> data.date }
                    val listHistory = listGroup.map { itemGroup ->
                        AdminHistoryModel(
                            date = itemGroup.key ?: "",
                            list = itemGroup.value
                        )
                    }.toList()
                    responseHistory.postValue(listHistory.reversed())
                    }else {
                        error.postValue(
                            EAMXMessageError(
                                message = "No tenemos registrada ningún servicio en el historico por el momento, consulta más tarde el módulo para dar seguimiento a los servicios",
                                back = false
                            )
                        )
                    }
                } ?: kotlin.run {
                    error.postValue(EAMXMessageError(message = "No tenemos registrada ningún servicio en el historico por el momento, consulta más tarde el módulo para dar seguimiento a los servicios", back = true))
                }
            }else{
                error.postValue(EAMXMessageError(message = response.exception?.message, back = true))
            }
        }
    }

    fun getDetailServiceById(serviceId: Int) {
        GlobalScope.launch {
            val response = repository.getDetailServiceById(userId, serviceId)

            if(response.sucess) {
                response.data?.let {
                    responseServicesDetail.postValue(it.mapToAdminDetailModel())
                } ?: kotlin.run {
                    error.postValue(EAMXMessageError(message = "El servicio seleccionado no cuenta con información por el momento, consulta más tarde el módulo para dar seguimiento al servicio", back = true))
                }
            }else{
                error.postValue(EAMXMessageError(message = response.exception?.message, back = true))
            }
        }
    }

    fun getStatusServices() {
        val response = repository.getStatusServices()
        if(response.sucess) {
            response.data?.let {
                responseStatus.postValue(it.mapToStatusServices())
            }
        }
    }

    fun executeChangeStatusService(serviceId: Int, status: String, comment : String = "") {
        changeStatus = status
        GlobalScope.launch {

            val commendAddSuccess = if(comment.isNotEmpty()){
                val requestCommend = RequestCommentModel(comment)
                val responseComment = repository.executeAddComment(userId, serviceId, requestCommend)
                if(responseComment.sucess.not()){
                    error.postValue(EAMXMessageError(message = responseComment.exception?.message))
                }
                responseComment.sucess
            }else
                true

            if(commendAddSuccess){
                val request = RequestChangeStatusModel(status)

                val response = when(status){
                    "REJECTED" ->  repository.executeChangeStatusServiceRejected(userId, serviceId, request)
                    else ->  repository.executeChangeStatusService(userId, serviceId, request)
                }

                if (response.sucess) {
                    responseExecuteChangeStatus.postValue(true)
                } else {
                    error.postValue(EAMXMessageError(message = response.exception?.message))
                }
            }
        }
    }

    fun executeDeleteService(serviceId: Int, position: Int) {
        GlobalScope.launch {
            val response = repository.executeDeleteService(userId, serviceId)
            responseDeleteService.postValue(response.sucess.not())
            if(response.sucess.not()){
                error.postValue(EAMXMessageError(message = response.exception?.message))
            }
        }
    }
}