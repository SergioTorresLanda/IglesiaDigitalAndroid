package mx.arquidiocesis.eamxbiblioteca.retrofit

import mx.arquidiocesis.eamxcommonutils.retrofit.logger.RetrofitLogger
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClientLibrary(host: String) {

    val retrofitClient: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(host)
            .client(RetrofitLogger.createHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
    }

    val apiInterface: ApiInterfaceLibrary by lazy {
        retrofitClient
            .build()
            .create(ApiInterfaceLibrary::class.java)
    }
}