package com.wallia.eamxcomunidades.retrofit

import com.google.gson.JsonObject
import com.wallia.eamxcomunidades.config.WebConfig
import com.wallia.eamxcomunidades.model.*
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @GET(WebConfig.GET_DETAIL_USER)
    fun getUserDetail(
        //@Header("X-User-Id") userId: String,
        @Path("id_user") id: Int
    ): Deferred<Response<UserDetailModel>>

    @GET(WebConfig.GETCOMMUNITYMODULES)//Obtener Modulos por comunidad
    fun getCommunityModules(
        @Path("location_id") location_id: Int
    ): Deferred<Response<CommunityModuleResponse>>?

    @GET(WebConfig.COMMUNITY)//Obtener comunidad principal
    fun getMainCommunity(
        @Path("user_id") user_id: Int,
        @Query("category") category: String
    ): Deferred<Response<MainCommunityResponse>>?

    @POST(WebConfig.COMMUNITY)//Coloca comunidad princupal o favorita
    fun postFavoriteCommunity(
        @Path("user_id") user_id: Int,
        @Body json: JsonObject
    ): Deferred<Response<ResponseBody>>?

    @DELETE(WebConfig.DELETECOMMUNITY)
    fun deleteCommunity(
        @Path("user_id") user_id: Int,
        @Path("location_id") location_id: Int
    ): Deferred<Response<ResponseBody>>?

    @GET(WebConfig.GETCOMMUNITYTYPES)//OBTENER TIPOS DE COMUNIDAD
    fun getCommunityTypes(): Deferred<Response<CommunityType>>?

    @GET(WebConfig.LOCATIONS)
    fun getAllCommunities(
        @Query("type_location") type_location: String
    ): Deferred<Response<AllCommunitiesResponse>>?

    @GET(WebConfig.LOCATIONS)
    fun getCommunitiesByLocations(
        @Query("type_location") type_location: String,
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String
    ): Deferred<Response<CommunitiesByLocationsResponse>>?

    @GET(WebConfig.LOCATIONS)
    fun getCommunitiesByNameAsync(
        @Query("type_location") type_location: String,
        @Query("name") name: String
    ): Deferred<Response<CommunitiesByNameResponse>>?

    @GET(WebConfig.LOCATIONS)
    fun getCommunitiesSearchByNameAsync(
        @Query("type_location") type_location: String,
        @Query("name") name: String
    ): Deferred<Response<CommunitiesByNameResponse>>?

    @GET(WebConfig.GETLOCATIONS)
    fun getCommunityDetailAsync(
        @Path("location_id") location_id: Int
    ): Deferred<Response<CommunityDetailResponse>>?

    @GET(WebConfig.GETACTIVITIES)
    fun getActivitiesAsync(
        @Path("location_id") location_id: Int
    ): Deferred<Response<ActivitiesResponse>>?

    @GET(WebConfig.GETLOCATIONS)
    fun getPartnerCommunitiesAsync(
        @Path("location_id") location_id: Int,
        @Query("linked") linked: Boolean
    ): Deferred<Response<PartnerCommunitiesResponse>>?

    @POST(WebConfig.LOCATIONS)
    fun createCommunity(
        @Header("X-User-Id") userIdHeader: Int,
        @Body createCommunityRequest: CreateCommunityRequest
    ): Deferred<Response<ResponseBody>>?

    @PUT(WebConfig.PUTLOCATIONS)
    fun editCommunity(
        @Body editCommunityRequest: EditCommunityRequest
    ): Deferred<Response<ResponseBody>>?

    @PUT(WebConfig.PUTLOCATIONS)
    fun completeCommunityAsync(
        @Path("location_id") location_id: Int,
        @Body completeCommunityRequest: CompleteCommunityRequest
    ): Deferred<Response<ResponseBody>>?

    @POST(WebConfig.REVIEW)
    fun sendReview(
        @Header("X-User-Id") userIdHeader: Int,
        @Path("location_id") location_id: Int,
        @Body json: JsonObject
    ): Deferred<Response<ReviewItem>>?

    @GET(WebConfig.REVIEW)
    fun getReviews(
        @Header("X-User-Id") userIdHeader: Int,
        @Path("location_id") location_id: Int
    ): Deferred<Response<ReviewsResponse>>?
}