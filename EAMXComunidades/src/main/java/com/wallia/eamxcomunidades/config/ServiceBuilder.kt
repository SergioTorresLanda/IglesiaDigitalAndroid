package com.wallia.eamxcomunidades.config

import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.retrofit.logger.RetrofitLogger
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceBuilder {
    val okhttpClient = OkHttpClient.Builder().apply {
        addInterceptor(
            Interceptor { chain ->
                val builder = chain.request().newBuilder()
                builder.header("X-User-Id", getUserId())
                return@Interceptor chain.proceed(builder.build())
            }
        )
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(WebConfig.HOSTAPI)
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            okhttpClient
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addNetworkInterceptor(RetrofitLogger.loggingInterceptor())
                .build()
        ).build()

    fun getUserId(): String {
        val userId = eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID.name,
            EAMXTypeObject.INT_OBJECT
        ) as Int
        return userId.toString()
    }
}