package mx.arquidiocesis.eamxcommonutils.repostiory

import com.google.gson.JsonParser
import mx.arquidiocesis.eamxcommonutils.BuildConfig
import mx.arquidiocesis.eamxcommonutils.application.ConstansApp
import mx.arquidiocesis.eamxcommonutils.repostiory.models.EAMXLoginRequest
import mx.arquidiocesis.eamxcommonutils.repostiory.models.EAMXRefreshTokenRequest
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RepositoryArquidiocesisApi {
    private var tokenAuth = ""
    private var xIdUser = 0

    private val api by lazy {
        Retrofit.Builder()
            .baseUrl(ConstansApp.urlHost())
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(EAMXHttpLoggerInterceptor())
                    .addInterceptor {
                        val request = it.request().newBuilder()
                        if (xIdUser > 0) request.addHeader("X-User-Id", xIdUser.toString())
                        if (tokenAuth.isNotEmpty())
                            request.addHeader(
                                "Authorization",
                                "Bearer $tokenAuth"
                            )
                        it.proceed(request.build())
                    }
                    .build()
            )
            .build()
            .create(UserApiService::class.java)
    }

    private suspend fun <T> onResponse(
        refreshToken: Boolean = true,
        onCb: suspend () -> T
    ): EAMXResponse {
        try {
            return EAMXResponse.Success(onCb())
        } catch (e: Exception) {
            if (BuildConfig.BUILD_TYPE == "debug") e.printStackTrace()
            when (e) {
                is HttpException -> {
                    if (e.code() == 401 && refreshToken) {
                        try {
                            tokenAuth =
                                api.refreshToken(EAMXRefreshTokenRequest(tokenAuth)).accessToken
                            return onResponse(false, onCb)
                        } catch (e: Exception) {
                            if (e is HttpException) return e.toResponse()
                            return e.toResponse()
                        }
                    }
                    return e.toResponse()
                }
                else -> return e.toResponse()

            }
        }
    }

    private fun Exception.toResponse() =
        EAMXResponse.Error(
            message ?: "Ocurrió un error inténtalo mas tarde",
            500,
            500,
        )

    private fun HttpException.toResponse(): EAMXResponse.Error {
        val error = response()?.errorBody()?.charStream()?.readText()
        var code = 500
        var message = "Ocurrio un error intentelo mas tarde"
        try {
            JsonParser().parse(error).asJsonObject.let {
                message = it.get("message").asString
                code = it.get("code").asInt
            }
        } catch (e: Exception) {
            message = error ?: message
        }
        return EAMXResponse.Error(message, code, code())
    }

    suspend fun login(userName: String, pas4sd: String) =
        onResponse { api.login(EAMXLoginRequest(userName, pas4sd)) }

    suspend fun getDetailsUser() =
        onResponse { api.getDetails(xIdUser) }
}