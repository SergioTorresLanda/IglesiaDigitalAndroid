package mx.arquidiocesis.eamxgeneric.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.application.ConstansApp
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxgeneric.model.*
import mx.arquidiocesis.eamxgeneric.retrofit.MainApiInterface2
import mx.arquidiocesis.eamxmaps.retrofit.validationlayer.ValidationCodes
import okhttp3.ResponseBody

class MainRepository2(val context : Context): ManagerCall(){
    val sendTokenResponse = MutableLiveData<ResponseBody>()
    val deleteTokenResponse = MutableLiveData<ResponseBody>()
    val dataHomeSaintResponse = MutableLiveData<List<DataHomeSaintResponse>>()
    val dataHomeReleaseResponse = MutableLiveData<List<DataHomeReleaseResponse>>()
    val dataHomeSuggestionResponse = MutableLiveData<List<SuggestionModel>>()
    val prayResponse = MutableLiveData<OracionDetalleModel>()
    val errorResponse = MutableLiveData<String>()

    private val retrofitInstance = RetrofitApp.Build<MainApiInterface2>()
        .setContext(context)
        .setEnvironment(true)
        .setClass(MainApiInterface2::class.java)


    suspend fun sendToken(id: Int, tokenObj: TokenObj) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(mx.arquidiocesis.eamxgeneric.config.WebConfig.HOST)
                    .builder().instance().sendTokenAsync(id, tokenObj).await()
            },
            validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    response.data?.let {
                        sendTokenResponse.value = it
                    }
                } else {
                    errorResponse.value = "Error Appointments : ${response.exception?.message}"
                }
            }
        }
    }

    suspend fun deleteToken(tokenObj: String) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(mx.arquidiocesis.eamxgeneric.config.WebConfig.HOST)
                    .builder().instance().deleteTokenAsync(tokenObj).await()
            },
            validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    response.data?.let {
                        deleteTokenResponse.value = it
                    }
                } else {
                    errorResponse.value = "Error Appointments : ${response.exception?.message}"
                }
            }
        }
    }

    suspend fun getHomeSaint(id: Int, type: String, startingDate: String) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(mx.arquidiocesis.eamxgeneric.config.WebConfig.HOSTHOME)
                    .builder().instance().getHomeSaintAsync(id, type, startingDate).await()
            },
            validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    response.data?.let {
                        dataHomeSaintResponse.value = it
                    }
                } else {
                    errorResponse.value = "Error Appointments : ${response.exception?.message}"
                }
            }
        }
    }

    suspend fun getHomeRelease(id: Int, type: String, starting_date: String) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost( ConstansApp.hostEncuentro())
                    .builder().instance().getHomeReleaseAsync(id, type, starting_date).await()
            },
            validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    response.data?.let {
                        dataHomeReleaseResponse.value = it
                    }
                } else {
                    errorResponse.value = "Error Appointments : ${response.exception?.message}"
                }
            }
        }
    }

    suspend fun getHomeSuggestion(id: Int, type: String) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(mx.arquidiocesis.eamxgeneric.config.WebConfig.HOSTHOME)
                    .builder().instance().getHomeSuggestionAsync(id, type).await()
            },
            validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    response.data?.let {
                        dataHomeSuggestionResponse.value = it
                    }
                } else {
                    errorResponse.value = "Error Appointments : ${response.exception?.message}"
                }
            }
        }
    }

    suspend fun getPrayDetail(id: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(mx.arquidiocesis.eamxgeneric.config.WebConfig.HOSTHOME)
                    .builder().instance().getDetailPrayAsync(id).await()
            },
            validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    response.data?.let {
                        prayResponse.value = it
                    }
                } else {
                    errorResponse.value = "Error Appointments : ${response.exception?.message}"
                }
            }
        }
    }
}