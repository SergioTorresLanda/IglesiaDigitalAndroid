package mx.arquidiocesis.registrosacerdote.retrofit

import kotlinx.coroutines.Deferred
import mx.arquidiocesis.registrosacerdote.config.WebConfig
import mx.arquidiocesis.registrosacerdote.model.ActivitiesModel
import mx.arquidiocesis.registrosacerdote.model.catalog.*
import mx.arquidiocesis.registrosacerdote.model.update.userupdatepriest.UserPriest
import mx.arquidiocesis.registrosacerdote.model.userdetailpriest.UserPriestResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @GET(WebConfig.GET_CATALOG_ACTIVITIES)
    fun getCatalogActivitiesAsync(): Deferred<Response<List<ActivitiesModel>>>

    @GET(WebConfig.GET_CATALOG_CONGREGATION)
    fun getCongregationsAsync(): Deferred<Response<CongregationsModel>>

    @POST(WebConfig.POST_UPDATE_USER)
    fun postUpdateUserPriestAsync(@Body user: UserPriest) : Deferred<Response<ResponseBody>>

    @GET(WebConfig.GET_DETAIL_USER)
    fun getUserDetailAsync(@Path("id") id: Int): Deferred<Response<UserPriestResponse>>
}

