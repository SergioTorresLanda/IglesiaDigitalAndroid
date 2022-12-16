package mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.api.ApiConfig
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.api.ApiInterface
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.api.validation.ValidationConfirmPassword
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.api.validation.ValidationForgotEmail
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.model.ConfirmPasswordModel
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.model.RequestUserInfoModel
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.model.SendCodeModel
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.model.UserInfoModel

class EAMXForgotPasswordRepository(private val context : Context) : ManagerCall() {

    val sendCodeResponse = MutableLiveData<Void>()
    val confirmPasswordResponse = MutableLiveData<Void>()
    val userInfoResponse = MutableLiveData<UserInfoModel>()
    val errorConfirmPassword = MutableLiveData<String>()
    val errorResponse = MutableLiveData<String>()

    private val retrofitInstance = RetrofitApp.Build<ApiInterface>()
            .setHost(ApiConfig.HOST)
            .setContext(context)
            .setClass(ApiInterface::class.java)
            .setEnvironment(true)
            .builder().instance()

    suspend fun sendCode(request: SendCodeModel) {
        managerCallApi(
                call = {
                    retrofitInstance.sendCodeAsync(request).await()
                },
                validation = ValidationForgotEmail()
        ).let{ response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    sendCodeResponse.value = null
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun confirmPassword(request: ConfirmPasswordModel) {
        managerCallApi(
                call = {
                    retrofitInstance.confirmPasswordAsync(request).await()
                },
                validation = ValidationConfirmPassword()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if(response.sucess){
                    confirmPasswordResponse.value = null
                }else{
                    errorConfirmPassword.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getUserInfo(request: RequestUserInfoModel) : UserInfoModel?{
        var infoUserInfoModel : UserInfoModel? = null
        managerCallApi(
                call = {
                    retrofitInstance.getInfoUserAsync(request).await()
                }
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    userInfoResponse.value = response.data
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }.run {
            return infoUserInfoModel;
        }
    }
}