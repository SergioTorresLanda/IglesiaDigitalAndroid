package mx.arquidiocesis.eamxlivestreammodule.repository

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxcommonutils.retrofit.model.dataclass.ResponseData
import mx.arquidiocesis.eamxlivestreammodule.config.WebConfig
import mx.arquidiocesis.eamxlivestreammodule.model.VideoNewsModel
import mx.arquidiocesis.eamxlivestreammodule.model.VideoResponse
import mx.arquidiocesis.eamxlivestreammodule.retrofit.ApiInterface
import mx.arquidiocesis.eamxlivestreammodule.retrofit.validationlayer.ValidationLiveCodes


class LiveStreamRepository(val context: Context) : ManagerCall() {

    val errorResponse = MutableLiveData<String>()
    val locationResponse = MutableLiveData<Location>()
    val videoResponse = MutableLiveData<List<VideoNewsModel>>()

    private val retrofitInstance = RetrofitApp.Build<ApiInterface>()
        .setHost(WebConfig.HOST)
        .setContext(context)
        .setClass(ApiInterface::class.java)
        .builder().instance()

    suspend fun getLiveVideos() {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getDataVideoListAsync().await()
            }
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    videoResponse.value = response.data
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }
    suspend fun getExistsLiveVideos() : ResponseData<VideoResponse?> {
        return managerCallApi(
            context = context,
            call = {
                retrofitInstance.getDataVideoListAsync().await()
            }
        )
    }
}