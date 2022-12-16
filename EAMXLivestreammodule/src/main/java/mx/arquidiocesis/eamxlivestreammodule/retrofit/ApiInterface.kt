package mx.arquidiocesis.eamxlivestreammodule.retrofit


import kotlinx.coroutines.Deferred
import mx.arquidiocesis.eamxlivestreammodule.config.WebConfig
import mx.arquidiocesis.eamxlivestreammodule.model.VideoResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @GET(WebConfig.STREAMING)
    fun getDataVideoListAsync(): Deferred<Response<VideoResponse>>

}