package mx.arquidiocesis.eamxcommonutils.util.repositoryrefreshtoken

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.application.ConstansApp
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxcommonutils.util.Repository.ValidationReview
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.log
import mx.arquidiocesis.eamxcommonutils.util.repositoryrefreshtoken.model.EAMXUserLoginRequest
import mx.arquidiocesis.eamxcommonutils.util.repositoryrefreshtoken.model.RefreshTokenRequest

class RefreshTokenRepository(val context: Context) : ManagerCall() {
    val TAG = "RefreshTokenRepository"
    private val retrofitInstance = RetrofitApp.Build<ApiInterfaceRefreshToken>()
        .setContext(context)
        .setEnvironment(true)
        .setClass(ApiInterfaceRefreshToken::class.java)

    suspend fun getRefreshToken() {
        val tokenLocal  =  eamxcu_preferences.TOKEN_REFRESH_CUSTOMER
        val request = RefreshTokenRequest(tokenLocal)
        "LOG-$TAG TOKEN REFRESH -> $tokenLocal".log()
        managerCallApi(
            call = { retrofitInstance.setHost(ConstansApp.hostUser()).setUseToken(false).builder().instance().getRefreshTokenAsync(request).await()
            },
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if(response.sucess) {
                    eamxcu_preferences.apply {
                        val token = response.data?.IdToken ?: ""
                        "LOG-$TAG SAVE TOKEN $token".log()
                        saveData(EAMXEnumUser.TOKEN_CUSTOMER.name, token)
                    }
                }
            }
        }
    }

    suspend fun getTokenLogin(user: String, password : String) {
        val request = EAMXUserLoginRequest(user, password)
        managerCallApi(
            call = { retrofitInstance.setHost(ConstansApp.hostUser()).setUseToken(false).builder().instance().getLoginTokenAsync(request).await()
            }, validation = ValidationReview()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                eamxcu_preferences.apply {
                    response.data?.UserAttributes?.let {
                        "SAVE USER ID ${it.id}".log()
                        "SAVE USER EMAIL ${it.email}".log()
                        saveData(EAMXEnumUser.USER_ID.name, it.id)
                        saveData(EAMXEnumUser.USER_EMAIL.name, it.email)
                    }

                    response.data?.AuthenticationResult?.let {
                        "SAVE ID TOKEN ${it.IdToken}".log()
                        "SAVE REFRESH TOKEN ${it.RefreshToken}".log()
                        saveData(EAMXEnumUser.TOKEN_CUSTOMER.name, it.IdToken)
                        saveData(EAMXEnumUser.TOKEN_REFRESH_CUSTOMER.name, it.RefreshToken)
                    }
                }
            }
        }
    }

}