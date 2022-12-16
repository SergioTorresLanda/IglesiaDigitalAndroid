package mx.arquidiocesis.servicios.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.interceptor.HeaderInterceptor
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxcommonutils.retrofit.model.dataclass.ResponseData
import mx.arquidiocesis.eamxcommonutils.retrofit.model.header.RequestHeader
import mx.arquidiocesis.servicios.config.WebConfigSer
import mx.arquidiocesis.servicios.model.*
import mx.arquidiocesis.servicios.model.admin.api.*
import mx.arquidiocesis.servicios.model.admin.view.AdminDetailModel
import mx.arquidiocesis.servicios.model.admin.view.StatusService.CANCELLED
import mx.arquidiocesis.servicios.model.admin.view.StatusService.COMPLETED
import mx.arquidiocesis.servicios.model.admin.view.StatusService.NOTHING
import mx.arquidiocesis.servicios.model.admin.view.StatusService.PENDING_CONFIRMATION
import mx.arquidiocesis.servicios.model.admin.view.StatusService.REJECTED
import mx.arquidiocesis.servicios.model.admin.view.StatusService.TODAY
import mx.arquidiocesis.servicios.retrofit.ApiServicesInterface
import mx.arquidiocesis.servicios.retrofit.validationlayer.ValidationGeneral
import mx.arquidiocesis.servicios.retrofit.validationlayer.ValidationRejected
import okhttp3.Response

class AdminServicesRepository(val context: Context) : ManagerCall() {
    private val serviceCatalog = "INTENTION"
    private val headerUser = "X-User-Id"
    private val headerRol = "X-Role"

    private val retrofitInstance = RetrofitApp.Build<ApiServicesInterface>()
        .setHost(WebConfigSer.HOST)
        .setContext(context)
        .setEnvironment(true)
        .setClass(ApiServicesInterface::class.java)

    suspend fun getServices(userId : Int, rol : String): ResponseData<ApiServiceListModel?> {
        return managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setInterceptors(
                        HeaderInterceptor(
                        listOf(
                            RequestHeader(headerName = headerUser, value = userId.toString()),
                            RequestHeader(headerName = headerRol, value = rol)
                        ))
                    )
                    .builder().instance()
                    .getServicesAsync().await()
            },
            validation = ValidationGeneral()
        )
    }

    suspend fun getHistory(userId : Int, rol : String, status : String? = null): ResponseData<ApiServiceListModel?> {
        val instanceRetrofit =  retrofitInstance
            .setInterceptors(
                HeaderInterceptor(
                    listOf(
                        RequestHeader(headerName = headerUser, value = userId.toString()),
                        RequestHeader(headerName = headerRol, value = rol)
                    ))
            )
            .builder().instance()

        return managerCallApi(
            context = context,
            call = {
                status?.let { instanceRetrofit.getServicesHistoryAsync(record = true, status = it).await() } ?:
                kotlin.run { instanceRetrofit.getServicesHistoryAsync(record = true).await() }
            },
            validation = ValidationGeneral()
        )
    }

    suspend fun getDetailServiceById(userId: Int, serviceId: Int): ResponseData<ApiServiceDetailModel?> {
        return managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setInterceptors(
                        HeaderInterceptor(
                            listOf(
                                RequestHeader(headerName = headerUser, value = userId.toString())
                            ))
                    )
                    .builder().instance()
                    .getServicesDetailAsync(serviceId).await()
            },
            validation = ValidationGeneral()
        )
    }

    fun getStatusServices(): ResponseData<ServicesTypeCatalog?> {

        val data = arrayListOf(
            ServicesTypeCatalogItem(id = NOTHING,name = "Selecciona"),
            ServicesTypeCatalogItem(id = TODAY,name = "Solicitudes del dia"),
            ServicesTypeCatalogItem(id = PENDING_CONFIRMATION,name = "Solicitudes anteriores"),
            ServicesTypeCatalogItem(id = COMPLETED,name = "Solicitudes atendidas"),
            ServicesTypeCatalogItem(id = REJECTED,name = "Solicitudes rechazadas"),
            ServicesTypeCatalogItem(id = CANCELLED,name = "Solicitudes canceladas"),
        )

        val resp = ServicesTypeCatalog()
        resp.addAll(data)

        return ResponseData(true, resp, null)
    }

    suspend fun executeChangeStatusService(userId : Int, serviceId : Int, request: RequestChangeStatusModel): ResponseData<Void?> {
        return managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setInterceptors(
                        HeaderInterceptor(
                            listOf(
                                RequestHeader(headerName = headerUser, value = userId.toString()),
                            ))
                    )
                    .builder().instance()
                    .pathChangeStatusServicesAsync(serviceId, request).await()
            },
            validation = ValidationGeneral()
        )
    }

    suspend fun executeChangeStatusServiceRejected(userId : Int, serviceId : Int, request: RequestChangeStatusModel): ResponseData<Void?> {
        return managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setInterceptors(
                        HeaderInterceptor(
                            listOf(
                                RequestHeader(headerName = headerUser, value = userId.toString()),
                            ))
                    )
                    .builder().instance()
                    .pathChangeStatusServicesAsync(serviceId, request).await()
            },
            validation = ValidationRejected()
        )
    }

    suspend fun executeAddComment(userId : Int, serviceId : Int, request: RequestCommentModel) : ResponseData<Void?> {
        return managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setInterceptors(
                        HeaderInterceptor(
                            listOf(
                                RequestHeader(headerName = headerUser, value = userId.toString()),
                            ))
                    )
                    .builder().instance()
                    .pathAddCommentAsync(serviceId, request).await()
            },
            validation = ValidationGeneral()
        )
    }

    suspend fun executeDeleteService(userId : Int, serviceId : Int) : ResponseData<Void?> {
        return managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setInterceptors(
                        HeaderInterceptor(
                            listOf(
                                RequestHeader(headerName = headerUser, value = userId.toString()),
                            ))
                    )
                    .builder().instance()
                    .deleteServiceAsync(serviceId).await()
            },
            validation = ValidationGeneral()
        )
    }
}