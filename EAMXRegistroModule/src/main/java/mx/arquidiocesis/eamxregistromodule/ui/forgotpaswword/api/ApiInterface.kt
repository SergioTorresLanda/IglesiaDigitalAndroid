package mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.api

import kotlinx.coroutines.Deferred
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.model.ConfirmPasswordModel
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.model.RequestUserInfoModel
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.model.SendCodeModel
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.model.UserInfoModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {

    @POST(ApiConfig.SEND_CODE)
    fun sendCodeAsync(@Body request: SendCodeModel): Deferred<Response<Void>>

    @POST(ApiConfig.CONFIRM_PASSWORD)
    fun confirmPasswordAsync(@Body request: ConfirmPasswordModel): Deferred<Response<Void>>

    @POST(ApiConfig.INFO_USER_BASIC)
    fun getInfoUserAsync(@Body request: RequestUserInfoModel): Deferred<Response<UserInfoModel>>
}