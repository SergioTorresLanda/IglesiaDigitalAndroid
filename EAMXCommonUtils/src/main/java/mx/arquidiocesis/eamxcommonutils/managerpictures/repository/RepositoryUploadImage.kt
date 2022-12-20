package mx.arquidiocesis.eamxcommonutils.managerpictures.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.application.ConstansApp
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.managerpictures.viewmodel.ManagerUploadImageViewModel.Companion.SOURCE_COMMUNITY
import mx.arquidiocesis.eamxcommonutils.managerpictures.viewmodel.ManagerUploadImageViewModel.Companion.SOURCE_PROFILE
import mx.arquidiocesis.eamxcommonutils.managerpictures.api.ApiInterface
import mx.arquidiocesis.eamxcommonutils.managerpictures.config.ValidationCodeImage
import mx.arquidiocesis.eamxcommonutils.managerpictures.model.ImageRequestModel
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences

class RepositoryUploadImage(val context: Context) : ManagerCall() {

    val responseUpdateImage = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<String>()

    private val retrofitInstance = RetrofitApp.Build<ApiInterface>()
            .setContext(context)
            .setClass(ApiInterface::class.java)
            .setEnvironment(true)

    suspend fun getUploadImageProfile(requestUrl: ImageRequestModel) {
        managerCallApi(
            call = {
                retrofitInstance.setHost(ConstansApp.hostApi())
                    .builder().instance().getUpdateImageAsync(requestUrl).await()
            },
            validation = ValidationCodeImage()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    response.data?.let { responseUrl ->
                        if(saveLocalUrlImage(requestUrl.source, responseUrl.url)) {
                            responseUpdateImage.value = true
                        }
                    }
                } else {
                    errorResponse.value = "No fue posible subir la imagen"
                }
            }
        }
    }

    private fun saveLocalUrlImage(sourceImage: String, url : String) : Boolean {
        return when(sourceImage) {
            SOURCE_PROFILE -> saveImageProfile(url)
            SOURCE_COMMUNITY -> saveImageCommunity(url)
            else -> true
        }
    }

    private fun saveImageCommunity(url: String) : Boolean{
        eamxcu_preferences.apply {
            saveData(EAMXEnumUser.URL_PICTURE_COMMUNITY.name, url)
        }
        return true
    }

    private fun saveImageProfile(url: String) : Boolean{
        eamxcu_preferences.apply {
            saveData(EAMXEnumUser.URL_PHOTO_UPDATE.name, true)
            saveData(EAMXEnumUser.URL_PICTURE_PROFILE_USER.name, url)
        }
        return true
    }
}
