package mx.arquidiocesis.eamxpagos.Repository

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxpagos.Retrofit.Services.ApiInterface
import mx.arquidiocesis.eamxpagos.Retrofit.ValidationCodes
import mx.arquidiocesis.eamxpagos.config.services.WebConfigSer
import mx.arquidiocesis.eamxpagos.model.MentionRequestPost
import mx.arquidiocesis.eamxpagos.model.ServiceMenuMainModel
import mx.arquidiocesis.eamxpagos.model.ZipCodeModel

import okhttp3.ResponseBody

class RepositoryServices(val context: Context) : ManagerCall() {
    val serviciosResponse = MutableLiveData<List<ServiceMenuMainModel>>()
    val errorResponse = MutableLiveData<String>()
    val mentionResponse = MutableLiveData<ResponseBody?>()
    val zipCodeResponse = MutableLiveData<ZipCodeModel>()

    var userId = 0

    init {
        userId = eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID.name,
            EAMXTypeObject.INT_OBJECT
        ) as Int
    }


    private val retrofitInstance = RetrofitApp.Build<ApiInterface>()
        .setHost(WebConfigSer.HOST)
        .setContext(context)
        .setEnvironment(true)
        .setClass(ApiInterface::class.java)
        .builder().instance()


    suspend fun sendMention(mentionRequest: MentionRequestPost) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.sendMentionAsync(userId, mentionRequest).await()
            }, validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    mentionResponse.value = response.data
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }
}