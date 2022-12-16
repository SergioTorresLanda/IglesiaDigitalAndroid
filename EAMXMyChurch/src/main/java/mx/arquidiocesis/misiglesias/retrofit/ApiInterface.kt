package mx.arquidiocesis.misiglesias.retrofit

import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import mx.arquidiocesis.misiglesias.config.WebConfig
import mx.arquidiocesis.misiglesias.model.CatServiceModel
import mx.arquidiocesis.misiglesias.model.ChurchDetaillModel
import mx.arquidiocesis.misiglesias.model.MasseModel
import mx.arquidiocesis.misiglesias.model.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @GET(WebConfig.IGLESIASFAVORITAS)
    fun getFavoritasAsync(@Path("user_id") id: Int,@Query("category") category: String): Deferred<Response<MisIgleciasModel>>

    @Headers("Content-Type: application/json")
    @POST(WebConfig.IGLESIASFAVORITAS)
    fun setFavoritasAsync(
        @Path("user_id") id: Int,
        @Body json: JsonObject
    ): Deferred<Response<ResponseBody>>

    @Headers("Content-Type: application/json")
    @DELETE(WebConfig.IGLESIASFAVDEL)
    fun delFavoritasAsync(
        @Path("user_id") id: Int,
        @Path("location_id") locationId: Int
    ): Deferred<Response<ResponseBody>>

    @GET(WebConfig.IGLESIASLISTADO)
    fun getListadoMapIglesiasAsync(@Query("type_location") type: String): Deferred<Response<List<IgleciasModel>>>

    @GET(WebConfig.SUGERENCIAS)
    fun getListadoSuggestion(): Deferred<Response<List<Location>>>

    @GET(WebConfig.IGLESIASLISTADO)
    fun getListadoMapNameIglesiasAsync(@Query("name") name: String): Deferred<Response<List<IgleciasModel>>>

    @GET(WebConfig.IGLESIASDETALLE)
    fun getDetalleIglesiasAsync(@Header("X-User-Id") userId: Int,@Path("location_id") id: Int): Deferred<Response<ChurchDetaillModel>>

    @Headers("Content-Type: application/json")
    @PUT(WebConfig.IGLESIASDETALLE)
    fun addIglesiasRegisterAsync(@Path("location_id") id: Int, @Body json: JsonObject): Deferred<Response<ResponseBody>>

    @GET(WebConfig.CATALOGOMISAS)
    fun getCatalogMisas(): Call<List<MasseModel>>

    @GET(WebConfig.CATALOGOSEVICIOS)
    fun getCatalogServiciosAsync(): Deferred<Response<List<CatServiceModel>>>

    @GET(WebConfig.IGLESIABUSQUEDA)
    fun getBuscarIglesiasAsync(@Query("name") name: String): Deferred<Response<List<IglesiaBusquedaModel>>>

    @GET(WebConfig.PRIESTSBUSQUEDA)
    fun getBuscarPriestsAsync(@Query("name") name: String): Deferred<Response<List<PriestModel>>>

    @GET(WebConfig.MISASLISTADO)
    fun getTypePrayersAsync(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String
    ): Deferred<Response<List<MassesModelM>>>

    @GET(WebConfig.MISASLISTADO)
    fun getMassesAsync(
        @Query("name") name: String,
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String
    ): Deferred<Response<List<MassesModelM>>>

    @Headers("Content-Type: application/json")
    @POST(WebConfig.COMENTARIOS)
    fun putComentario(
        @Header("X-User-Id") id: Int,
        @Path("location_id") location: Int,
        @Body json: JsonObject
    ): Deferred<Response<RespondeOpinionModel>>

    @GET(WebConfig.COMENTARIOS)
    fun getComentariosList(
        @Header("X-User-Id") id: Int,
        @Path("location_id") location: Int,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int): Deferred<Response<List<OpinioModel>>>
}