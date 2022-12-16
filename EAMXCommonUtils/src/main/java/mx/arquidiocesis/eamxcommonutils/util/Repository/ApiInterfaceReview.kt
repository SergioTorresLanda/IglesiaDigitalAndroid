package mx.arquidiocesis.eamxcommonutils.util.Repository

import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import mx.arquidiocesis.eamxcommonutils.model.ReviewListModel
import mx.arquidiocesis.eamxcommonutils.model.ReviewModel
import mx.arquidiocesis.eamxcommonutils.model.ReviewRespondeModel
import okhttp3.ResponseBody

import retrofit2.Response
import retrofit2.http.*

interface ApiInterfaceReview{

    @Headers("Content-Type: application/json")
    @POST(WebConfigReview.COMENTARIOS)
    fun putComentario(
        @Header("X-User-Id") id: Int,
        @Path("location_id") location: Int,
        @Body json: JsonObject
    ): Deferred<Response<ReviewRespondeModel>>

    @GET(WebConfigReview.COMENTARIOS)
    fun getComentariosList(
        @Header("X-User-Id") id: Int,
        @Path("location_id") location: Int,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int): Deferred<Response<ReviewListModel>>

    @DELETE(WebConfigReview.REVIEW)
    fun deleteComentario(
        @Header("X-User-Id") id: Int,
        @Path("location_id") location: Int,
        @Path("review_id") review: Int
    ): Deferred<Response<ReviewRespondeModel>>

    @Headers("Content-Type: application/json")
    @PUT(WebConfigReview.REVIEW)
    fun updateComentario(
        @Header("X-User-Id") id: Int,
        @Path("location_id") location: Int,
        @Path("review_id") review: Int,
        @Body json: JsonObject
    ): Deferred<Response<ReviewRespondeModel>>
}