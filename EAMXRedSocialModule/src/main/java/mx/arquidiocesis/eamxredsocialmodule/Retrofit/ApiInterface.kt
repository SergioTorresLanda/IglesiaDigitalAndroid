package mx.arquidiocesis.eamxredsocialmodule.Retrofit

import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import mx.arquidiocesis.eamxredsocialmodule.config.WebConfig
import mx.arquidiocesis.eamxredsocialmodule.model.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @GET(WebConfig.USERSPOST)
    fun getAllPost(
        @Path("userId") userId: String,
        @Query("nextPage") page: String
    ): Deferred<Response<AllPostModel>>

    @GET(WebConfig.USERSPOST)
    fun getAllPost(@Path("userId") userId: String): Deferred<Response<AllPostModel>>

    @POST(WebConfig.POST)
    fun setPost(@Body body: JsonObject): Deferred<Response<ResponsePostModel>>

    @DELETE(WebConfig.POSTUPDATE)
    fun deletePost(
        @Path("id") id: Int,
        @Query("userId") userId: Int
    ): Deferred<Response<ResponsePostModel>>

    @PUT(WebConfig.POSTUPDATE)
    fun updatePost(
        @Path("id") id: Int,
        @Body body: JsonObject
    ): Deferred<Response<ResponsePostModel>>

    @DELETE(WebConfig.REACTD)
    fun reactDel(
        @Path("id") id: Int,
        @Query("userId") userId: Int,
    ): Deferred<Response<ResponsePostModel>>

    @POST(WebConfig.POSTREACT)
    fun reactPost(
        @Path("id") id: Int,
        @Body body: JsonObject
    ): Deferred<Response<ResponsePostModel>>
    @POST(WebConfig.FOLLOW)
    fun followPost(
        @Body body: JsonObject
    ): Deferred<Response<ResponsePostModel>>

    @Headers("Content-Type: application/json")
    @DELETE(WebConfig.UNFOLLOW)
    fun unfollowPost(
        @Path("id") id: Int,
        @Query("userId") userId: Int,
        @Query("entityType") entityType: Int
    ): Deferred<Response<ResponsePostModel>>

    @GET(WebConfig.FOLLOWGET)
    fun followGet(
        @Path("id") id: Int,
        @Query("type")type:Int,
        @Query("nextPage") page: String?=null
    ): Deferred<Response<ResponseFollowModel>>
    @GET(WebConfig.SEARCH)
    fun searchGet(
        @Query("q") q: String,
        @Query("userId") userId: Int
    ): Deferred<Response<ResponseSearchModel>>

    @GET(WebConfig.SEARCH)
    fun searchGet(
        @Query("q") q: String,
        @Query("userId") userId: Int,
        @Query("nextPage") page: String
    ): Deferred<Response<ResponseSearchModel>>

    @GET(WebConfig.COMMENT)
    fun getComment(@Path("id") id: Int): Deferred<Response<ResponseCommentModel>>

    @GET(WebConfig.COMMENT)
    fun getComment(
        @Path("id") id: Int,
        @Query("nextPage") page: String
    ): Deferred<Response<ResponseCommentModel>>

    @POST(WebConfig.COMMENTPOST)
    fun commentPost(@Body body: JsonObject): Deferred<Response<ResponsePostModel>>

    @PUT(WebConfig.COMMENTUPDATE)
    fun commentUpdate(
        @Path("id") id: Int,
        @Body body: JsonObject
    ): Deferred<Response<ResponsePostModel>>

    @DELETE(WebConfig.COMMENTUPDATE)
    fun deleteComment(
        @Path("id") id: Int,
        @Query("userId") userId: Int
    ): Deferred<Response<ResponsePostModel>>

    @GET(WebConfig.PROFILE)
    fun getProfile(@Path("id") id: Int): Deferred<Response<ResponseProfileModel>>

    @GET(WebConfig.MULTIPROFILE)
    fun getMultiProfile(@Path("id") id: Int): Deferred<Response<ResponseMultiProfileModel>>

    @POST(WebConfig.UPLOAPMULTIMEDIA)
    fun uploadMultimedia(@Body body: JsonObject): Deferred<Response<List<ResponseMultimediaModel>>>

    @PUT("posts/{data}")
    fun putMutimedia(
        @Path("data", encoded = true) data: String,
        @Body body: RequestBody
    ): Deferred<Response<ResponseBody>>
}