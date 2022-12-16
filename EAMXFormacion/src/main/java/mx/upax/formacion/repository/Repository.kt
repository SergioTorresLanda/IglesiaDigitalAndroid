package mx.upax.formacion.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxcommonutils.retrofit.model.dataclass.ResponseData
import mx.arquidiocesis.eamxcommonutils.util.live.SingleLiveEvent
import mx.upax.formacion.config.WebConfig
import mx.upax.formacion.model.*
import mx.upax.formacion.retrofit.ApiInterface
import mx.upax.formacion.retrofit.validationlayer.ValidationFormationCodes

class Repository(val context: Context) : ManagerCall() {

    val searchTabNewsResponse = SingleLiveEvent<SearchTabNewsModel>()
    val tabNavigationResponse = SingleLiveEvent<MenuModel>()
    val errorResponse = SingleLiveEvent<String>()
    private val retrofitInstance = RetrofitApp.Build<ApiInterface>()
        .setHost(WebConfig.HOST)
        .setContext(context)
        .setEnvironment(true)
        .setClass(ApiInterface::class.java)
        .builder().instance()

    suspend fun getTabNavigation() {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getTabNavigationAsync().await()
            },
            validation = ValidationFormationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if(response.sucess){
                    response.data?.let {
                        tabNavigationResponse.value = it
                    }
                }else{
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun searchInformation(filter: String? = null, tabName: String? = null): ResponseData<List<FeaturedModel>?> {
        return managerCallApi(
            context = context,
            call = {
                retrofitInstance.getDataListAsync(filter, tabName).await()
            },
            validation = ValidationFormationCodes()
        )
    }

    suspend fun updateViewInItem(itemIdentifier: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.updateViewInItemAsync(itemIdentifier).await()
            },
            validation = ValidationFormationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if(response.sucess){
                    print(response.data)
                }else{
                    print(response.exception?.message)
                }
            }
        }
    }
}