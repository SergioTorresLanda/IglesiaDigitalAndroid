package mx.arquidiocesis.eamxcommonutils.repostiory

import mx.arquidiocesis.eamxcommonutils.repostiory.models.EAMXAuthenticationResult
import mx.arquidiocesis.eamxcommonutils.repostiory.models.EAMXLoginRequest
import mx.arquidiocesis.eamxcommonutils.repostiory.models.EAMXRefreshTokenRequest
import mx.arquidiocesis.eamxcommonutils.repostiory.models.EAMXResponseLogin
import retrofit2.http.*

interface UserApiService {
    private companion object{
        const val urlUser = "/arquidiocesis/gestion-usuarios/v1/"
    }

    @POST("${urlUser}user/login")
    suspend fun login(@Body body: EAMXLoginRequest): EAMXResponseLogin

    //
//
    @POST("${urlUser}user/refresh_tokens")
    suspend fun refreshToken(@Body body: EAMXRefreshTokenRequest): EAMXAuthenticationResult


    @GET("${urlUser}user/detail/{userId}")
    suspend fun getDetails(
        @Path("userId") userId: Int,
    ): String

}