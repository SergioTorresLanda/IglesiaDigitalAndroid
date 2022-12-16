package mx.arquidiocesis.eamxcommonutils.api.core.request

import mx.arquidiocesis.eamxcommonutils.BuildConfig
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.retrofit.logger.RetrofitLogger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EAMXRestEngine {
    companion object {
        fun getRestEngine(baseUrl : String ? = null): Retrofit {
            return if(baseUrl != null){
                Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(RetrofitLogger.createHttpClient())
                    .build()
            }else{
                Retrofit.Builder()
                    .baseUrl(AppMyConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(RetrofitLogger.createHttpClient())
                    .build()
            }

        }
    }
}