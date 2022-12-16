package mx.arquidiocesis.sos.retrofit

import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import mx.arquidiocesis.sos.config.WebConfig
import mx.arquidiocesis.sos.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @Headers("Content-Type: application/json")
    @PATCH(WebConfig.SOSHABILITAR)
    fun setSos(
        @Path("user_id") userId: Int,
        @Body json: ActivateSosModel
    ): Call<ResponseBody>
    @GET(WebConfig.SOSHABILITAR)
    fun getSosAsync(@Path("user_id") userId: Int ): Deferred<Response<ActivateSosModel>>

    @Headers("accept: application/json")
    @GET(WebConfig.SERVICESSOS)
    fun getListServicesAsync(
        @Header("X-User-Id") id: Int,
        @Query("status") status :String,
        @Query("type") type :String): Deferred<Response<List<ServicesPriestModel>>>


    @Headers("accept: application/json")
    @GET(WebConfig.SERVICESLIST)
    fun getPendiente(
        @Header("X-User-Id") id: Int,
        @Header("X-Role") role: String,
        @Query("catalog_service_id") type :Int): Deferred<Response<RespuestaModel>>


    @Headers("Content-Type: application/json")
    @POST(WebConfig.SERVICESSOS)
    fun postRegistrySOSAsync(@Header("X-User-Id") userId: Int,@Body body: JsonObject): Deferred<Response<ResponseSOSModel>>

    @GET(WebConfig.LOCATIONS)
    fun getLocationsSOSAsync(
        @Query("type") type: String,
        @Query("latitude") lat: Double,
        @Query("longitude") lnt: Double
    ): Deferred<Response<List<LocationSOSModel>>>



    @GET(WebConfig.SERVICESLISTSOS)
    fun getServicesListAsync(@Query("type") type: String): Deferred<Response<List<Service>>>

    @Headers("Content-Type: application/json")
    @PUT(WebConfig.SERVICESGET)
    fun setStatusProgress(@Header("X-User-Id") userId: Int,@Path("service_id") serviceId: Int,@Body body: JsonObject): Deferred<Response<ResponseBody>>

    @Headers("Content-Type: application/json")
    @PATCH(WebConfig.SERVICESGET)
    fun setStatusAsync(@Header("X-User-Id") userId: Int,@Path("service_id") serviceId: Int, @Body json: StatusModel): Deferred<Response<ResponseBody>>

    @GET(WebConfig.SERVICESGET)
    fun getStatusAsync(@Header("X-User-Id") userId: Int, @Path("service_id") serviceId: Int): Deferred<Response<ServiceDetalleModel>>

    @Headers("accept: application/json")
    @GET(WebConfig.PRIESTLIST)
    fun getSacerdote(
        @Header("X-User-Id") id: Int,
        @Query("service") service :String,
    ): Deferred<Response<List<ServiceBoxModel>>>


}