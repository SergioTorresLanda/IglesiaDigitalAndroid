package mx.arquidiocesis.servicios.retrofit

import kotlinx.coroutines.Deferred
import mx.arquidiocesis.eamxcommonutils.retrofit.model.dataclass.ResponseData
import mx.arquidiocesis.servicios.config.WebConfigSer
import mx.arquidiocesis.servicios.model.ServicesIntentionsResponse
import mx.arquidiocesis.servicios.model.admin.api.ApiIntentionUrlModel
import mx.arquidiocesis.servicios.model.admin.api.IntentionsApiModel
import mx.arquidiocesis.servicios.model.admin.api.IntentionsDetailApiModel
import retrofit2.Response
import retrofit2.http.*

interface ApiIntentionsInterface {
    @GET(WebConfigSer.GET_INTENTIONS)
    fun  getIntentionsAsync(
        @Path("location_Id") location_Id: Int,
        @Query("type") type: String,
        @Query("date") date: String,
    ):  Deferred<Response<IntentionsApiModel>>

    @GET(WebConfigSer.GET_INTENTIONS_DETAIL)
    fun getIntentionsDetailAsync(
        @Query("type") type: String,
        @Query("date") date: String,
        @Query("time") time: String
    ):  Deferred<Response<IntentionsDetailApiModel>>

    @GET(WebConfigSer.GET_INTENTIONS_DETAIL)
    fun getIntentionsUrlPdfAsync(
        @Query("type") type: String,
        @Query("date") date: String,
        @Query("time") time: String,
        @Query("pdf") pdf: Boolean,
    ):  Deferred<Response<ApiIntentionUrlModel>>
}