package mx.arquidiocesis.servicios.retrofit

import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import mx.arquidiocesis.servicios.config.WebConfigSer
import mx.arquidiocesis.servicios.model.*
import mx.arquidiocesis.servicios.model.IglesiasModel
import mx.arquidiocesis.servicios.model.SacramentsModel
import mx.arquidiocesis.servicios.model.ServicesModel
import mx.arquidiocesis.servicios.model.ServiceMenuMainModel
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @GET(WebConfigSer.CATALOG_SERVICES)
    fun getServiciosAsync(): Deferred<Response<List<ServiceMenuMainModel>>>

    @GET(WebConfigSer.CATALOG_SERVICES)
    fun getSacramentsAsync(@Query("type") type: String): Deferred<Response<List<SacramentsModel>>>

    @GET(WebConfigSer.CATALOG_SERVICES)
    fun getServicesTypeAsync(@Query("type") type: String): Deferred<Response<List<ServicesModel>>>

    @GET(WebConfigSer.CATALOG_SERVICES)
    fun getServiciosTypeAsync(@Query("type") type: String): Deferred<Response<List<ServicesModel>>>

    @GET(WebConfigSer.GET_CHURCH_FAVORITE)
    fun getFavoriteChurchAsync(
        @Path("userId") id: Int,
        @Query("category") category: String
    ): Deferred<Response<MisIgleciasModel>>

    @GET(WebConfigSer.GET_LIST_CHURCH_AND_MASS)
    fun getListadoMapIglesiasAsync(
        @Query("type_location") type_location: String
    ): Deferred<Response<List<IgleciasModel>>>

    @GET(WebConfigSer.GET_LIST_CHURCH_AND_MASS)
    fun getBuscarIglesiasAsync(@Query("name") name: String): Deferred<Response<List<IgleciasModel>>>

    @GET(WebConfigSer.GET_DETAIL_CHURCH_OR_MASS)
    fun getChurchDetailAsync(
        @Header("X-User-Id") userId: Int,
        @Path("locationId") id: Int
    ): Deferred<Response<ChurchDetaillModel>>

    @POST(WebConfigSer.POST_SERVICES)
    fun postServiceAsync(
        @Header("X-User-Id") userId: Int,
        @Body service: JsonObject
    ): Deferred<Response<ResponseBody>>

    @GET(WebConfigSer.GET_LIST_CHURCH_AND_MASS)
    fun getMassesAsync(
        @Query("type") type: String,
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String
    ): Deferred<Response<List<IglesiasModel>>>

    @GET(WebConfigSer.GET_DETAIL_CHURCH_OR_MASS)
    fun getMassesScheduleAsync(
        @Header("X-User-Id") userId: Int,
        @Path("locationId") locationId: Int,
        @Query("type") type: String
    ): Deferred<Response<ScheduleModel>>

    @POST(WebConfigSer.POST_SERVICES)
    fun sendMentionAsync(
        @Header("X-User-Id") userId: Int,
        @Body mentionRequest: MentionRequestPost
    ): Deferred<Response<ResponseBody>>

    @GET(WebConfigSer.CATALOG_SERVICES)
    fun getIntentions(@Query("type") type: String): Deferred<Response<List<Intention>>>

    @GET(WebConfigSer.GET_ZIP_CODE)
    fun getZipCode(@Query("zip_code") zip_code: String): Deferred<Response<ZipCodeModel>>

    @GET(WebConfigSer.COMMUNITY)//Obtener comunidad principal
    fun getMainCommunity(
        @Path("user_id") user_id: Int,
        @Query("category") category: String
    ): Deferred<Response<MainCommunityResponse>>?

    @GET(WebConfigSer.GET_LIST_CHURCH_AND_MASS)
    fun getAllCommunities(
        @Query("type_location") type_location: String
    ): Deferred<Response<List<CommunityResponse>>>?
}