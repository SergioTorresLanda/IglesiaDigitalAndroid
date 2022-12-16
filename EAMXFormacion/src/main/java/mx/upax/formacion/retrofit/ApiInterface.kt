package mx.upax.formacion.retrofit
import kotlinx.coroutines.Deferred
import mx.upax.formacion.config.WebConfig
import mx.upax.formacion.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @GET(WebConfig.MENU)
    fun getTabNavigationAsync():
            Deferred<Response<MenuModel>>

    @GET(WebConfig.FORMATION_SIMPLE)
    fun getDataListAsync(@Query("tag") filter: String? = null,
                         @Query("type") module: String? = null):
            Deferred<Response<List<FeaturedModel>>>

    @PATCH(WebConfig.FORMATION_PATCH)
    fun updateViewInItemAsync(@Path("id") itemIdentifier: Int) : Deferred<Response<Any>>
}