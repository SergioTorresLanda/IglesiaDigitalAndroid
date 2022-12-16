package mx.arquidiocesis.eamxcadenaoracionesmodule.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcadenaoracionesmodule.config.WebConfig
import mx.arquidiocesis.eamxcadenaoracionesmodule.model.EAMXPrayResponse
import mx.arquidiocesis.eamxcadenaoracionesmodule.model.EAMXPraySend
import mx.arquidiocesis.eamxcadenaoracionesmodule.model.EAMXSendPrayStatus
import mx.arquidiocesis.eamxcadenaoracionesmodule.retrofit.ApiInterface
import mx.arquidiocesis.eamxcommonutils.retrofit.Validation.ValidationCommun
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxcommonutils.retrofit.model.inter.ValidationCode
import okhttp3.ResponseBody

class EAMXCadenaOracionesRepository(private val context: Context) : ManagerCall() {

    val prayListResponse = MutableLiveData<EAMXPrayResponse>()
    val sendPrayResponse = MutableLiveData<ResponseBody>()
    val prayingResponse = MutableLiveData<ResponseBody>()
    val errorResponse = MutableLiveData<String>()

    private val retrofitInstance = RetrofitApp.Build<ApiInterface>()
        .setContext(context)
        .setHost(WebConfig.HOST)
        .setClass(ApiInterface::class.java)
        .setEnvironment(true)


    suspend fun getPrayers(userId: Int) {
        managerCallApi(
            context = context,
            call = { retrofitInstance.builder().instance().getPrayersAsync(userId).await() },
            validation = ValidationCommun()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                response.data?.let {d->
                    if (response.sucess) {
                        prayListResponse.value = d
                    } else {
                        errorResponse.value = response.exception?.message
                    }
                } ?: kotlin.run {
                    errorResponse.value = response.exception?.message

                }

            }
        }
    }

    suspend fun sendPrayer(requestPray: EAMXPraySend) {
        managerCallApi(
            context = context,
            call = { retrofitInstance.builder().instance().sendPrayAsync(requestPray).await() },
            validation = ValidationCommun()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                response.data?.let {d->
                    if (response.sucess) {
                        sendPrayResponse.value = d
                    } else {
                        errorResponse.value = response.exception?.message
                    }
                } ?: kotlin.run {
                    errorResponse.value = response.exception?.message

                }

            }
        }
    }

    suspend fun prayingOration(idPray: Int, status: EAMXSendPrayStatus) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.builder().instance().reactionPrayAsync(idPray, status).await()
            },
            validation = ValidationCommun()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                response.data?.let {d->
                    if (response.sucess) {
                        prayingResponse.value = d
                    } else {
                        errorResponse.value = response.exception?.message
                    }
                } ?: kotlin.run {
                    errorResponse.value = response.exception?.message

                }

            }
        }
    }
}