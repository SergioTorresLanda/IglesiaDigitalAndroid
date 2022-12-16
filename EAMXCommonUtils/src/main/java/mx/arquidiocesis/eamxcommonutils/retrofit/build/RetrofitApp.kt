package mx.arquidiocesis.eamxcommonutils.retrofit.build

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import mx.arquidiocesis.eamxcommonutils.retrofit.interceptor.ConnectionInterceptor
import mx.arquidiocesis.eamxcommonutils.retrofit.interceptor.HeaderInterceptor
import mx.arquidiocesis.eamxcommonutils.retrofit.interceptor.RemoveCharacterInterceptor
import mx.arquidiocesis.eamxcommonutils.retrofit.interceptor.TokenInterceptor
import mx.arquidiocesis.eamxcommonutils.retrofit.logger.RetrofitLogger
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitApp<T> private constructor(
    private val host: String,
    private val context: Context,
    private val apiInterface: Class<T>,
    private val headerInterceptor: HeaderInterceptor?,
    private val useToken : Boolean = true,
    private val isDebug: Boolean
) {

    private fun retrofitClient(): Retrofit.Builder {

        val httpClient = OkHttpClient.Builder().apply {
            addInterceptor(RemoveCharacterInterceptor())
            addInterceptor(RetrofitLogger.loggingInterceptor())
            addInterceptor(ConnectionInterceptor(context))
            connectTimeout(1000, TimeUnit.MINUTES)
            writeTimeout(1000, TimeUnit.MINUTES)
            readTimeout(1000, TimeUnit.MINUTES)
        }

        headerInterceptor?.let {
            httpClient.addInterceptor(it)
        }

        if(useToken){
            httpClient.addInterceptor(TokenInterceptor())
        }

        return Retrofit.Builder()
            .baseUrl(host)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
    }

    fun instance(): T {
        return retrofitClient()
            .build()
            .create(apiInterface)
    }

    class Build<T>() {
        private lateinit var host: String
        private lateinit var context: Context
        private lateinit var apiInterface: Class<T>
        private var headerInterceptor: HeaderInterceptor? = null
        private var isDebug: Boolean = false
        private var useToken: Boolean = true

        fun setHost(host: String): Build<T> {
            this.host = host
            return this
        }

        fun setContext(context: Context): Build<T> {
            this.context = context
            return this
        }

        fun setEnvironment(environment: Boolean): Build<T> {
            this.isDebug = environment
            return this
        }

        fun setClass(apiInterface: Class<T>): Build<T> {
            this.apiInterface = apiInterface
            return this
        }

        fun setUseToken(useToken : Boolean): Build<T> {
            this.useToken = useToken
            return this
        }

        fun setInterceptors(headerInterceptor: HeaderInterceptor?): Build<T> {
            this.headerInterceptor = headerInterceptor
            return this
        }

        fun builder(): RetrofitApp<T> {
            return RetrofitApp(
                host,
                context,
                apiInterface,
                headerInterceptor,
                useToken,
                isDebug
            )
        }
    }
}