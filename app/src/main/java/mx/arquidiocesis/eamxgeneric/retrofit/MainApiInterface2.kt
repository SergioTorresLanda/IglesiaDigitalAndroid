package mx.arquidiocesis.eamxgeneric.retrofit

import kotlinx.coroutines.Deferred
import mx.arquidiocesis.eamxgeneric.config.WebConfig
import mx.arquidiocesis.eamxgeneric.model.*
import mx.arquidiocesis.eamxprofilemodule.model.userdetail.UserResponse
import mx.arquidiocesis.misiglesias.model.MisIgleciasModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface MainApiInterface2 {

    @POST(WebConfig.TOKEN)
    fun sendTokenAsync(@Header("X-User-Id") id: Int, @Body tokenObj: TokenObj): Deferred<Response<ResponseBody>>

    @DELETE(WebConfig.TOKENDELETE)
    fun deleteTokenAsync(@Path("token") token: String): Deferred<Response<ResponseBody>>

    @GET(WebConfig.HOMEDATA)
    fun getHomeReleaseAsync(
        @Header("X-User-Id") id: Int,
        @Query("type") type: String,
        @Query("starting_date") starting_date: String
    ):  Deferred<Response<List<DataHomeReleaseResponse>>>

    @GET(WebConfig.HOMEDATA)
    fun getHomeSaintAsync(
        @Header("X-User-Id") id: Int,
        @Query("type") type: String,
        @Query("starting_date") starting_date: String
    ):  Deferred<Response<List<DataHomeSaintResponse>>>

    @GET(WebConfig.HOMEDATA)
    fun getHomeSuggestionAsync(
        @Header("X-User-Id") id: Int,
        @Query("type") type: String,
    ): Deferred<Response<List<SuggestionModel>>>

    @GET(WebConfig.ORACIONESDETALLE)
    fun getDetailPrayAsync(@Path("devotion_id") id: Int): Deferred<Response<OracionDetalleModel>>
}