package mx.arquidiocesis.eamxevent.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.api.core.EAMXInitServices
import mx.arquidiocesis.eamxcommonutils.api.core.EAMXValidResponse
import mx.arquidiocesis.eamxcommonutils.api.core.response.EAMXGenericResponse
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import retrofit2.Call
import mx.arquidiocesis.eamxevent.model.Event
import mx.arquidiocesis.eamxevent.model.Day
import mx.arquidiocesis.eamxevent.model.EventResponse
import mx.arquidiocesis.eamxevent.retrofit.ApiInterface
import mx.arquidiocesis.eamxevent.retrofit.WebConfig.HOST_EVENT

class RepositoryEvent(val context: Context) : ManagerCall() {

    val zonaResponse = MutableLiveData<String>()
    val dayResponse = MutableLiveData<Day>()
    val scheduleResponse = MutableLiveData<Day>()
    val errorResponse = MutableLiveData<String>()
    val updateMessage = MutableLiveData<String>()
    val registerResponse = MutableLiveData<String>()

    private var retrofitInstance = RetrofitApp.Build<ApiInterface>()
        .setContext(context)
        .setClass(ApiInterface::class.java)
    fun callServiceEvent(
        requestModel: Event,
        observer: Observer<EAMXGenericResponse<EventResponse, String, Event>>
    ) {
        val initService: EAMXInitServices<Event, Call<EventResponse>> =
            EAMXInitServices()
        EAMXValidResponse(observer, requestModel, EventResponse::class).validationMethod(
            initService.postExecuteService(requestModel, AppMyConstants.CREATE_DINER)
        )
    }

    suspend fun saveEventDiner(event: Event) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(HOST_EVENT).builder().instance().postUpdateEventAsync(event).await()
            }
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if(response.sucess){
                    registerResponse.value = "Se ha dado de alta el comedor correctamente."
                }else{
                    errorResponse.value = "Verifica tus datos"
                }
            }
        }
    }

}