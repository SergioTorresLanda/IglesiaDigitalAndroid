package mx.arquidiocesis.eamxgeneric.retrofit

import mx.arquidiocesis.eamxcommonutils.retrofit.logger.RetrofitLogger
import mx.arquidiocesis.eamxgeneric.config.WebConfig
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.converter.gson.GsonConverterFactory
object MainRetrofitClient {

    val retrofitClient: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(WebConfig.HOST)
            .client(RetrofitLogger.createHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
    }

    val MAIN_API_INTERFACE: MainApiInterface by lazy {
        retrofitClient
            .build()
            .create(MainApiInterface::class.java)
    }

    val retrofitClientHome: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(WebConfig.HOSTHOME)
            .client(RetrofitLogger.createHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
    }

    val MAIN_API_INTERFACE_HOME: MainApiInterface by lazy {
        retrofitClientHome
            .build()
            .create(MainApiInterface::class.java)
    }
}