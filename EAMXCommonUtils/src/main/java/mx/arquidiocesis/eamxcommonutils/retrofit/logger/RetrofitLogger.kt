package mx.arquidiocesis.eamxcommonutils.retrofit.logger

import mx.arquidiocesis.eamxcommonutils.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object RetrofitLogger {

    fun createHttpClient(): OkHttpClient {
        val okhttpClient = OkHttpClient.Builder()
        okhttpClient.addInterceptor(loggingInterceptor())
        okhttpClient.connectTimeout(40000, TimeUnit.MILLISECONDS)
        okhttpClient.readTimeout(40000, TimeUnit.MILLISECONDS)
        return okhttpClient.build()
    }

    fun loggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        if (!BuildConfig.FLAVOR.equals("pro")) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        return logging;
    }

}