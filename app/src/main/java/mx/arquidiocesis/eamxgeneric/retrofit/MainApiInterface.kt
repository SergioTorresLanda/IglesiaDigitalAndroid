package mx.arquidiocesis.eamxgeneric.retrofit

import mx.arquidiocesis.eamxgeneric.config.WebConfig
import mx.arquidiocesis.eamxgeneric.model.*
import mx.arquidiocesis.eamxprofilemodule.model.userdetail.UserResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface MainApiInterface {
    /*@POST(WebConfi.TOKEN)
    fun sendToken(@Body tokenObj: TokenObj): Call<ResponseBody>*/

    @POST(WebConfig.TOKEN)
    fun sendToken(@Header("X-User-Id") id: Int, @Body tokenObj: TokenObj): Call<ResponseBody>

    @DELETE(WebConfig.TOKENDELETE)
    fun deleteToken(@Path("token") token: String): Call<ResponseBody>

    @GET(WebConfig.HOMEDATA)
    fun getHomeRelease(
        @Header("X-User-Id") id: Int,
        @Query("type") type: String,
        @Query("starting_date") starting_date: String
    ): Call<List<DataHomeReleaseResponse>>

    @GET(WebConfig.HOMEDATA)
    fun getHomeSaint(
        @Header("X-User-Id") id: Int,
        @Query("type") type: String,
        @Query("starting_date") starting_date: String
    ): Call<List<DataHomeSaintResponse>>

    @GET(WebConfig.HOMEDATA)
    fun getHomeSuggestion(
        @Header("X-User-Id") id: Int,
        @Query("type") type: String,
    ): Call<List<SuggestionModel>>

    @GET(WebConfig.GET_DETAIL_USER)
    fun getUserInfo(@Path("id")userId: Int): Call<UserResponse>

    @GET(WebConfig.ORACIONESDETALLE)
    fun getDetalleOracionesAsync(@Path("devotion_id") id: Int): Call<OracionDetalleModel>
}