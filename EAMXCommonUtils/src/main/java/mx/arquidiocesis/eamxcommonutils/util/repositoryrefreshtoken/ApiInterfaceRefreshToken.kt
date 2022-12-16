package mx.arquidiocesis.eamxcommonutils.util.repositoryrefreshtoken

import kotlinx.coroutines.Deferred
import mx.arquidiocesis.eamxcommonutils.util.repositoryrefreshtoken.model.AuthenticationResult
import mx.arquidiocesis.eamxcommonutils.util.repositoryrefreshtoken.model.EAMXUserLoginRequest
import mx.arquidiocesis.eamxcommonutils.util.repositoryrefreshtoken.model.RefreshTokenRequest
import mx.arquidiocesis.eamxcommonutils.util.repositoryrefreshtoken.model.UserResponse

import retrofit2.Response
import retrofit2.http.*

interface ApiInterfaceRefreshToken{

    @POST(WebConfigRefreshToken.REFRESH_TOKEN)
    fun getRefreshTokenAsync(@Body request: RefreshTokenRequest): Deferred<Response<AuthenticationResult>>

    @POST(WebConfigRefreshToken.LOGIN_TOKEN)
    fun getLoginTokenAsync(@Body request: EAMXUserLoginRequest): Deferred<Response<UserResponse>>
}