package mx.arquidiocesis.servicios.repository

import android.content.Context
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.interceptor.HeaderInterceptor
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxcommonutils.retrofit.model.dataclass.ResponseData
import mx.arquidiocesis.eamxcommonutils.retrofit.model.header.RequestHeader
import mx.arquidiocesis.servicios.config.WebConfigSer
import mx.arquidiocesis.servicios.model.ServicesIntentionsResponse
import mx.arquidiocesis.servicios.model.admin.api.ApiIntentionUrlModel
import mx.arquidiocesis.servicios.model.admin.api.IntentionsApiModel
import mx.arquidiocesis.servicios.model.admin.api.IntentionsDetailApiModel
import mx.arquidiocesis.servicios.retrofit.ApiIntentionsInterface
import mx.arquidiocesis.servicios.retrofit.validationlayer.ValidationGeneral

class AdminServicesIntentionsRepository(val context: Context) : ManagerCall() {

    private val headerUser = "X-User-Id"
    private val headerRol = "X-Role"
    private val searchMasses = "MASSES"
    private val intention = "INTENTION"

    private val retrofitInstance = RetrofitApp.Build<ApiIntentionsInterface>()
        .setHost(WebConfigSer.HOST)
        .setContext(context)
        .setEnvironment(true)
        .setClass(ApiIntentionsInterface::class.java)

    suspend fun getIntentions(
        userId: Int,
        churchId: Int,
        date: String
    ): ResponseData<IntentionsApiModel?> {
        return (managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setInterceptors(HeaderInterceptor(
                         listOf(RequestHeader(headerName = headerUser,value = userId.toString())))
                    )
                    .builder().instance()
                    .getIntentionsAsync(churchId, searchMasses, date).await()
            },
            validation = ValidationGeneral()
        ))
    }

    suspend fun getIntentionsDetail(
        userId: Int,
        rol: String,
        date: String,
        time: String
    ): ResponseData<IntentionsDetailApiModel?> {
        return managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setInterceptors(HeaderInterceptor(
                        listOf(RequestHeader(headerName = headerUser, value = userId.toString()),
                            RequestHeader(headerName = headerRol, value = rol)))
                    )
                    .builder().instance()
                    .getIntentionsDetailAsync(intention, date, time).await()
            },
            validation = ValidationGeneral()
        )
    }

    suspend fun getUrlDownloadIntention(
        userId: Int,
        rol: String,
        date: String,
        time: String
    ): ResponseData<ApiIntentionUrlModel?> {
        return managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setInterceptors(HeaderInterceptor(
                        listOf(RequestHeader(headerName = headerUser, value = userId.toString()),
                            RequestHeader(headerName = headerRol, value = rol)))
                    )
                    .builder().instance()
                    .getIntentionsUrlPdfAsync(intention, date, time, true).await()
            },
            validation = ValidationGeneral()
        )
    }


}