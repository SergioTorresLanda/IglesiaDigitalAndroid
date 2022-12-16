package mx.arquidiocesis.eamxmaps.retrofit

import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import mx.arquidiocesis.eamxmaps.config.WebConfig
import mx.arquidiocesis.eamxmaps.model.IgleciasModel

import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {


    @GET(WebConfig.IGLESIASLISTADO)
    fun getListadoMapIglesiasAsync(@Query("type_location") type: String): Deferred<Response<List<IgleciasModel>>>

    @GET(WebConfig.IGLESIASLISTADO)
    fun getListadoMapNameIglesiasAsync(@Query("name") name: String,@Query("type_location") type: String):  Deferred<Response<List<IgleciasModel>>>

}