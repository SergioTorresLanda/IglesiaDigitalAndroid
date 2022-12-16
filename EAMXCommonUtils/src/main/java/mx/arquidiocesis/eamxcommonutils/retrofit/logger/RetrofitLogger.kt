package mx.arquidiocesis.eamxcommonutils.retrofit.logger

import mx.arquidiocesis.eamxcommonutils.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitLogger {

    fun createHttpClient(): OkHttpClient {
        val okhttpClient = OkHttpClient.Builder()
        okhttpClient.addInterceptor(loggingInterceptor())
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