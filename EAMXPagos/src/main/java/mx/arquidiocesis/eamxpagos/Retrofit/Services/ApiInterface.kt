package mx.arquidiocesis.eamxpagos.Retrofit.Services

import kotlinx.coroutines.Deferred
import mx.arquidiocesis.eamxpagos.config.services.WebConfigSer
import mx.arquidiocesis.eamxpagos.model.MentionRequestPost
import mx.arquidiocesis.eamxpagos.model.ZipCodeModel
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @POST(WebConfigSer.POST_SERVICES)
    fun sendMentionAsync(
        @Header("X-User-Id") userId: Int,
        @Body mentionRequest: MentionRequestPost
    ): Deferred<Response<ResponseBody>>


    @GET(WebConfigSer.GET_ZIP_CODE)
    fun getZipCode(@Query("zip_code") zip_code: String): Deferred<Response<ZipCodeModel>>

}