package mx.arquidiocesis.registrosacerdote.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxcommonutils.retrofit.model.dataclass.ResponseData
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.log
import mx.arquidiocesis.registrosacerdote.model.userdetailpriest.UserPriestResponse
import mx.arquidiocesis.registrosacerdote.retrofit.validation.ValidationCollaborators
import mx.arquidiocesis.registrosacerdote.model.update.userupdatepriest.UserPriest
import mx.arquidiocesis.registrosacerdote.config.WebConfig
import mx.arquidiocesis.registrosacerdote.model.ActivitiesModel
import mx.arquidiocesis.registrosacerdote.model.catalog.*
import mx.arquidiocesis.registrosacerdote.retrofit.ApiInterface
import mx.arquidiocesis.registrosacerdote.retrofit.validation.ValidationDetailUser

class Repository(val context : Context) : ManagerCall() {

    val activitiesResponse = MutableLiveData<List<ActivitiesModel>>()
    val congregationsResponse = MutableLiveData<CongregationsModel>()
    val registerResponse = MutableLiveData<String>()
    val errorResponse = MutableLiveData<String>()

    private var retrofitInstance = RetrofitApp.Build<ApiInterface>()
        .setContext(context)
        .setClass(ApiInterface::class.java)

    suspend fun getActivitiesList() {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(WebConfig.HOST_CATALOG).builder().instance().getCatalogActivitiesAsync().await()
            }
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if(response.sucess){
                    activitiesResponse.value = response.data!!
                }else{
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getCongregations() {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(WebConfig.HOST_CATALOG).builder().instance().getCongregationsAsync().await()
            }
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if(response.sucess){
                    congregationsResponse.value = response.data!!
                }else{
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun updateUsePriest(user: UserPriest) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(WebConfig.HOST_USER).builder().instance().postUpdateUserPriestAsync(user).await()
            },
            validation = ValidationCollaborators()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if(response.sucess){
                    "Save profile -> success save priest".log()
                    val responseDetail = getUserDetail(userId = user.id)
                    "Save profile -> success get detail $responseDetail".log()
                    saveDataUser(responseDetail.data)
                    registerResponse.value = "Se actualizaron tus datos correctamente."
                }else{
                    errorResponse.value = "Verifica que cancilleria tenga tus datos. En caso de duda o aclaracion, enviar correo electronico a la siguiente dirección para autorizar tu registro: cancilleria@arquidiocesismexico.org"
                }
            }
        }
    }

    private suspend fun getUserDetail(userId : Int): ResponseData<UserPriestResponse?> {
        return managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(WebConfig.HOST_USER).builder().instance()
                    .getUserDetailAsync(userId).await()
            },
            validation = ValidationDetailUser()
        )
    }

    private fun saveDataUser(user: UserPriestResponse?) {
        "Save profile -> Entro al guardado de datos".log()
        eamxcu_preferences.apply {
            user?.let { userProfile ->
                "Save profile -> $userProfile".log()

                saveData("USER", Gson().toJson(user).toString())

                userProfile.data.UserPriest.profile?.let { pro ->
                    "Save profile -> Información guardada profile -> $pro".log()
                    saveData(EAMXEnumUser.USER_PROFILE.name, pro)
                }

                userProfile.data.UserPriest.location_id?.let { location ->
                    "Save profile -> Información guardada location -> $location".log()
                    saveData(EAMXEnumUser.CHURCH.name, location)
                }
            }
        }
    }
}
