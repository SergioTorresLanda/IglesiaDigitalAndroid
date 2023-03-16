package mx.arquidiocesis.eamxevent.api

import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.retrofit.logger.RetrofitLogger
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RequestApi {

    companion object {

        fun getRequestApi(baseUrl : String ? = null): Retrofit {
            return if(baseUrl != null){
                Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(RetrofitLogger.createHttpClient())
                    .build()
            }else{
                Retrofit.Builder()
                    .baseUrl(AppMyConstants.BASE_URLC)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(RetrofitLogger.createHttpClient())
                    .build()
            }

        }
    }
}