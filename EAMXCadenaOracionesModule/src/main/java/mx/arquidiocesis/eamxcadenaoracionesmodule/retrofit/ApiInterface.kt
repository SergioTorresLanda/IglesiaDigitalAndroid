package mx.arquidiocesis.eamxcadenaoracionesmodule.retrofit

import kotlinx.coroutines.Deferred
import mx.arquidiocesis.eamxcadenaoracionesmodule.model.EAMXPrayResponse
import mx.arquidiocesis.eamxcadenaoracionesmodule.model.EAMXPraySend
import mx.arquidiocesis.eamxcadenaoracionesmodule.model.EAMXSendPrayStatus
import mx.arquidiocesis.eamxcadenaoracionesmodule.config.WebConfig
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @Headers("accept: application/json")
    @GET(WebConfig.PRAYERS)
    fun getPrayersAsync(@Header("X-User-Id") id: Int): Deferred<Response<EAMXPrayResponse>>

    @PUT(WebConfig.PRAYERS)
    fun sendPrayAsync(@Body pray: EAMXPraySend): Deferred<Response<ResponseBody>>

    @POST(WebConfig.PRAYERREACTION)
    fun reactionPrayAsync(
        @Path("id") id: Int,
        @Body eamxSendPrayStatus: EAMXSendPrayStatus
    ): Deferred<Response<ResponseBody>>
}