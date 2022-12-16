
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxcommonutils.util.validationrepositorygeneral.ValidationDefault

import mx.arquidiocesis.eamxmaps.retrofit.validationlayer.ValidationCodes
import mx.arquidiocesis.eamxpagos.model.BillingModel
import mx.arquidiocesis.eamxpagos.model.ZipCodeBillingModel

class RepositoryDonation(val context: Context) : ManagerCall() {
    val postResponse = MutableLiveData<Boolean?>()
    val getBillingResponse = MutableLiveData<BillingModel>()
    val zipCodeResponse = MutableLiveData<ZipCodeBillingModel>()
    val errorResponse = MutableLiveData<String>()

    private val retrofitInstance = RetrofitApp.Build<ApiInterface>()
        .setContext(context)
        .setEnvironment(true)
        .setClass(ApiInterface::class.java)

    suspend fun postBilling(json: JsonObject) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_NEW)
                    .builder().instance().postBilling(json).await()
            }, validation = ValidationDefault()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    postResponse.value = true
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun updateBilling(idBilling: Int, json: JsonObject) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_NEW)
                    .builder().instance().putBilling(idBilling, json).await()
            }, validation = ValidationDefault()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    postResponse.value = true
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun delBilling(idBilling: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_NEW)
                    .builder().instance().delBilling(idBilling).await()
            }, validation = ValidationDefault()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    postResponse.value = true
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getBilling() {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_NEW)
                    .builder().instance().getBilling().await()
            }, validation = ValidationDefault()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    if (response.data.isNullOrEmpty()) {
                        getBillingResponse.value = null
                    } else {
                        getBillingResponse.value = response.data!!.first()
                    }
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getZipCode(code: String) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_NEW)
                    .builder().instance().getZipCode(code).await()
            }, validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    response.data?.let { d ->
                        zipCodeResponse.value = d
                    } ?: run {
                        errorResponse.value = response.exception?.message
                    }
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }
}