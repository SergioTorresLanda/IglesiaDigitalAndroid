package mx.arquidiocesis.servicios.retrofit

import kotlinx.coroutines.Deferred
import mx.arquidiocesis.servicios.config.WebConfigSer
import mx.arquidiocesis.servicios.model.admin.api.*
import retrofit2.Response
import retrofit2.http.*

interface ApiServicesInterface {
    @GET(WebConfigSer.GET_SERVICES)
    fun getServicesAsync():  Deferred<Response<ApiServiceListModel>>

    @GET(WebConfigSer.GET_SERVICES_DETAIL)
    fun getServicesDetailAsync(@Path("service_id") service_id: Int,
    ):  Deferred<Response<ApiServiceDetailModel>>

    @DELETE(WebConfigSer.EXECUTE_DELETE_SERVICES)
    fun deleteServiceAsync(@Path("service_id") service_id: Int,
    ):  Deferred<Response<Void>>

    @GET(WebConfigSer.GET_SERVICES)
    fun getServicesHistoryAsync(@Query("record") record: Boolean
    ):  Deferred<Response<ApiServiceListModel>>

    @GET(WebConfigSer.GET_SERVICES)
    fun getServicesHistoryAsync(@Query("record") record: Boolean, @Query("status") status: String):
            Deferred<Response<ApiServiceListModel>>

    @GET(WebConfigSer.GET_SERVICES_CATALOG)
    fun getServicesCatalogAsync(@Query("type") type: String
    ):  Deferred<Response<ServicesTypeCatalog>>

    @PATCH(WebConfigSer.EXECUTE_CHANGE_STATUS_SERVICES)
    fun pathChangeStatusServicesAsync(@Path("service_id") service_id: Int, @Body statusRequest: RequestChangeStatusModel): Deferred<Response<Void>>

    @PATCH(WebConfigSer.EXECUTE_ADD_COMMENT)
    fun pathAddCommentAsync(@Path("service_id") service_id: Int, @Body comments: RequestCommentModel): Deferred<Response<Void>>
}