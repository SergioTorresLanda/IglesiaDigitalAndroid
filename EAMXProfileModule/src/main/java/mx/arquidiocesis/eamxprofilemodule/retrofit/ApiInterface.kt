package mx.arquidiocesis.eamxprofilemodule.retrofit

import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import mx.arquidiocesis.eamxprofilemodule.config.WebConfig
import mx.arquidiocesis.eamxprofilemodule.model.*
import mx.arquidiocesis.eamxprofilemodule.model.update.userupdatecaselaicconsecratedreligious.UserLaic
import mx.arquidiocesis.eamxprofilemodule.model.update.userupdatecaselaicconsecratedreligious.UserLaicConsecratedReligious
import mx.arquidiocesis.eamxprofilemodule.model.update.userupdatecasesinglemarriedwidower.UserSingleMarriedWidower
import mx.arquidiocesis.eamxprofilemodule.model.update.userupdatediacono.UserDiacono
import mx.arquidiocesis.eamxprofilemodule.model.userdetail.UserResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @GET(WebConfig.GET_CATALOG_LIFE_STATE)
    fun getCatalogLifeStateAsync(): Deferred<Response<LifeStateModel>>

    @GET(WebConfig.GET_CATALOG_PREFIX)
    fun getCatalogPrefixAsync(@Query("life-state") lifeState: String): Deferred<Response<PrefixModel>>

    @GET(WebConfig.GET_CATALOG_CONGREGATION)
    fun getCongregationsAsync(): Deferred<Response<CongregationsModel>>

    @GET(WebConfig.GET_CATALOG_PROVIDED_SERVICES)
    fun getProvidedServicesAsync(): Deferred<Response<ProvidedServicesModel>>

    @GET(WebConfig.GET_CATALOG_TOPICS)
    fun getTopicsAsync(): Deferred<Response<TopicsModel>>

    @GET(WebConfig.GET_DETAIL_USER)
    fun getUserDetailAsync(@Path("id") id: Int): Deferred<Response<UserResponse>>

    @GET(WebConfig.FAVORITE_CHURCH)
    fun getFavoriteChurchAsync(@Path("user_id") id: Int): Deferred<Response<MyFavoriteChurch>>

    @POST(WebConfig.POST_UPDATE_USER)
    fun postUpdateUserSingleMarriedWidowerAsync(@Body user: UserSingleMarriedWidower): Deferred<Response<Void>>

    @POST(WebConfig.POST_UPDATE_USER)
    fun postUpdateUserLaicConsecratedReligiousAsync(@Body user: UserLaicConsecratedReligious): Deferred<Response<Void>>

    @POST(WebConfig.POST_UPDATE_USER)
    fun postUpdateUserLaicAsync(@Body user: UserLaic): Deferred<Response<Void>>

    @POST(WebConfig.POST_UPDATE_USER)
    fun postUpdateUserDiaconoAsync(@Body user: UserDiacono): Deferred<Response<Void>>

    @HTTP(method = "DELETE", path = WebConfig.DELETE_ACCOUNT, hasBody = true)
    fun deleteAccount(@Body email: JsonObject): Deferred<Response<Void>>

    @GET(WebConfig.GET_CHURCH)
    fun getChurchAsync(): Deferred<Response<List<ChurchModel>>>//

    @GET(WebConfig.GET_CHURCH)
    fun getChurchAsync(@Query("name") name: String): Deferred<Response<List<ChurchModel>>>

    @GET(WebConfig.GET_CHURCH)
    fun getCommunitiesByNameAsync(
        @Query("type_location") type_location: String,
        @Query("name") name: String
    ):  Deferred<Response<List<ChurchModel>>>

    @GET(WebConfig.GET_MODULES)
    fun getModulesAsync(@Path("location_id") location_id: Int): Deferred<Response<List<ModuleModel>>>

    @PUT(WebConfig.UPDATE_MODULES)
    fun updateModulesAsync(
        @Path("location_id") location_id: Int,
        @Body modules: List<RequestModuleUpdate>
    ): Deferred<Response<ResponseBody>>

    @GET(WebConfig.GET_COLLABORATORS)
    fun getCollaboratorsAsync(
        @Path("location_id") location_id: Int,
        @Query("name") name: String
    ): Deferred<Response<List<CollaboratorModel>>>

    @GET(WebConfig.GET_COLLABORATOR_DETAIL)
    fun getCollaboratorDetailAsync(
        @Path("location_id") location_id: Int,
        @Path("user_id") user_id: Int
    ): Deferred<Response<CollaboratorDetailModel>>

    @PUT(WebConfig.UPDATE_MODULES_OF_COLLABORATOR)
    fun updateModulesOfCollaboratorAsync(
            @Path("location_id") location_id: Int,
            @Path("user_id") user_id: Int,
            @Body modules: List<RequestModuleUpdate>
    ): Deferred<Response<Void>>

    @POST(WebConfig.GET_URL_IMAGE)
    fun getUpdateImageAsync(@Body request: ImageRequestModel): Deferred<Response<UrlImageModel>>
}

