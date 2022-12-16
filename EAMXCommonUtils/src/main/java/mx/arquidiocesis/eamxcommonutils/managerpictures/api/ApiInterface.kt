package mx.arquidiocesis.eamxcommonutils.managerpictures.api

import kotlinx.coroutines.Deferred
import mx.arquidiocesis.eamxcommonutils.managerpictures.config.WebConfig
import mx.arquidiocesis.eamxcommonutils.managerpictures.model.ImageRequestModel
import mx.arquidiocesis.eamxcommonutils.managerpictures.model.UrlImageModel
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @POST(WebConfig.GET_URL_IMAGE)
    fun getUpdateImageAsync(@Body request: ImageRequestModel): Deferred<Response<UrlImageModel>>
}

