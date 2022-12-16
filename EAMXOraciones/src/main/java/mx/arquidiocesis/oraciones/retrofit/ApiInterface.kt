package mx.arquidiocesis.oraciones.retrofit


import kotlinx.coroutines.Deferred
import mx.arquidiocesis.oraciones.config.WebConfig
import mx.arquidiocesis.oraciones.model.OracionesDetalleModel
import mx.arquidiocesis.oraciones.model.OracionesTipoModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    @GET(WebConfig.ORACIONESLISTADO)
    fun getListadoOracionesAsync(@Query("name") type: String): Deferred<Response<List<OracionesTipoModel>>>

    @GET(WebConfig.ORACIONESDETALLE)
    fun getDetalleOracionesAsync(@Path("devotion_id") id: Int): Deferred<Response<OracionesDetalleModel>>

    @GET(WebConfig.ORACIONESTIPO)
    fun getTipoOracionesAsync(): Deferred<Response<List<OracionesTipoModel>>>
}