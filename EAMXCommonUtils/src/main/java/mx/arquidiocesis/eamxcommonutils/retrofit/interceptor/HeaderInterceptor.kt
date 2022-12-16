package mx.arquidiocesis.eamxcommonutils.retrofit.interceptor

import mx.arquidiocesis.eamxcommonutils.retrofit.model.header.RequestHeader
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor constructor(private val headers : List<RequestHeader>?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestOriginal = chain.request()

        val header = headers?.let { items ->
            val newBuilder = requestOriginal.newBuilder()
            if(items.isNotEmpty()){
                items.map { item -> newBuilder.addHeader(item.headerName, item.value) }
            }
            newBuilder
        } ?: kotlin.run {
            requestOriginal.newBuilder()
        }
        val request = header.build()
        return chain.proceed(request)
    }
}