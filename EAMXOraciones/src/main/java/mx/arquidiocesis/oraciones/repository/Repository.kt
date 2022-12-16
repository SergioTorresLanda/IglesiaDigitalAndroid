package mx.arquidiocesis.oraciones.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxcommonutils.retrofit.model.inter.ValidationCode
import mx.arquidiocesis.eamxmaps.retrofit.validationlayer.ValidationCodes
import mx.arquidiocesis.oraciones.config.WebConfig
import mx.arquidiocesis.oraciones.model.OracionesDetalleModel
import mx.arquidiocesis.oraciones.model.OracionesTipoModel
import mx.arquidiocesis.oraciones.retrofit.ApiInterface

class Repository(val context: Context) : ManagerCall() {

    val detalleResponse = MutableLiveData<OracionesDetalleModel>()
    val tipoOracionResponse = MutableLiveData<List<OracionesTipoModel>>()
    val buscarOracionResponse = MutableLiveData<List<OracionesTipoModel>>()
    val errorResponse = MutableLiveData<String>()

    private val retrofitInstance = RetrofitApp.Build<ApiInterface>()
        .setContext(context)
        .setHost(WebConfig.HOST)
        .setEnvironment(true)
        .setClass(ApiInterface::class.java)
        .builder().instance()

    suspend fun oracionesBuscar(type: String) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getListadoOracionesAsync(type).await()
            }, validation = ValidationCodes()

        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                response.data?.let { d ->
                    if (response.sucess) {
                        buscarOracionResponse.value = d
                    } else {
                        errorResponse.value = response.exception?.message
                    }
                } ?: kotlin.run {
                    errorResponse.value = response.exception?.message
                }

            }
        }
    }

    suspend fun oracionesTipo() {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getTipoOracionesAsync().await()
            },
            validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                response.data?.let { d ->
                    if (response.sucess) {
                        tipoOracionResponse.value = d
                    } else {
                        errorResponse.value = response.exception?.message
                    }
                } ?: kotlin.run {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun obtenerDetalle(id: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getDetalleOracionesAsync(id).await()
            },
            validation = ValidationCodes()

        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                response.data?.let { d ->
                    if (response.sucess) {
                        detalleResponse.value = d
                    } else {
                        errorResponse.value = response.exception?.message
                    }
                } ?: kotlin.run {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }
}