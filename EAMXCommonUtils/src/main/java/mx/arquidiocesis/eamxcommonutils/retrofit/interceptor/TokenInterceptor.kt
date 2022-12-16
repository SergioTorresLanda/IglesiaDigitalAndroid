package mx.arquidiocesis.eamxcommonutils.retrofit.interceptor

import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.retrofit.model.header.RequestHeader
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = eamxcu_preferences.TOKEN_CUSTOMER
        val requestOriginal = chain.request()
        val newBuilder = requestOriginal.newBuilder()
        newBuilder.addHeader("Authorization" ,"Bearer $token")
        val request =  newBuilder.build()
        return chain.proceed(request)
    }
}