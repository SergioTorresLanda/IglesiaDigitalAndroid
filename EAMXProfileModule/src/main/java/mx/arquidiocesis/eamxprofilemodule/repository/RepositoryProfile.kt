package mx.arquidiocesis.eamxprofilemodule.repository

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.retrofit.Validation.ValidationCommun
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.interceptor.HeaderInterceptor
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxcommonutils.retrofit.model.dataclass.ResponseData
import mx.arquidiocesis.eamxcommonutils.retrofit.model.exception.BackendException
import mx.arquidiocesis.eamxcommonutils.retrofit.model.header.RequestHeader
import mx.arquidiocesis.eamxcommonutils.retrofit.model.inter.ValidationCode
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.log
import mx.arquidiocesis.eamxcommonutils.util.validationrepositorygeneral.ValidationDefault
import mx.arquidiocesis.eamxprofilemodule.config.WebConfig
import mx.arquidiocesis.eamxprofilemodule.model.*
import mx.arquidiocesis.eamxprofilemodule.model.local.DataModelSharedPreferences
import mx.arquidiocesis.eamxprofilemodule.model.local.DataModelSharedPreferences2
import mx.arquidiocesis.eamxprofilemodule.model.update.userupdatecaselaicconsecratedreligious.UserLaic
import mx.arquidiocesis.eamxprofilemodule.model.update.userupdatecaselaicconsecratedreligious.UserLaicConsecratedReligious
import mx.arquidiocesis.eamxprofilemodule.model.update.userupdatecasesinglemarriedwidower.UserSingleMarriedWidower
import mx.arquidiocesis.eamxprofilemodule.model.update.userupdatediacono.UserDiacono
import mx.arquidiocesis.eamxprofilemodule.model.userdetail.UserResponse
import mx.arquidiocesis.eamxprofilemodule.retrofit.ApiInterface
import mx.arquidiocesis.eamxprofilemodule.viewmodel.SELECT_ITEM
import mx.arquidiocesis.registrosacerdote.retrofit.validation.ValidationCollaborators
import mx.arquidiocesis.registrosacerdote.retrofit.validation.ValidationDetailUser
import mx.arquidiocesis.registrosacerdote.retrofit.validation.ValidationPrefileGeneral
import okhttp3.ResponseBody
import retrofit2.Response
import java.util.*

const val CATALOG_LIFE_STATE = "LIFE_STATE"
const val CATALOG_PREFIX = "PREFIX"
const val CATALOG_PROVIDED_SERVICES = "PROVIDED_SERVICES"
const val CATALOG_TOPIC = "TOPIC"
const val USER = "USER"

class RepositoryProfile(val context: Context) : ManagerCall() {

    val lifeStateResponse = MutableLiveData<LifeStateModel>()
    val prefixResponse = MutableLiveData<PrefixModel>()
    val providedServicesResponse = MutableLiveData<ProvidedServicesModel>()
    val topicResponse = MutableLiveData<TopicsModel>()
    val congregationsResponse = MutableLiveData<CongregationsModel>()
    val iglesiasMapResponse = MutableLiveData<List<ChurchModel>>()
    val locationResponse = MutableLiveData<Location>()
    val userDetailResponse = MutableLiveData<UserResponse>()
    val updateMessage = MutableLiveData<String>()
    val errorResponse = MutableLiveData<String>()
    val errorResponseExit = MutableLiveData<String>()
    val getModulesResponse = MutableLiveData<List<ModuleModel>>()
    val updateModulesResponse = MutableLiveData<ResponseBody>()
    val getCollaboratorsResponse = MutableLiveData<List<CollaboratorModel>>()
    val collaboratorDetailResponse = MutableLiveData<CollaboratorDetailModel>()
    val updateModulesOfCollaboratorResponse = MutableLiveData<Response<Void>>()
    val responseUpdateImage = MutableLiveData<String?>()
    val successRemoveUser = MutableLiveData<Boolean>()

    private val repositoryLocal = LocalRepository()

    private val retrofitInstance = RetrofitApp.Build<ApiInterface>()
            .setContext(context)
            .setClass(ApiInterface::class.java)
            .setEnvironment(true)

    suspend fun getLifeSateList() {
        val dataLocal = Gson().fromJson(repositoryLocal.getData(CATALOG_LIFE_STATE), DataModelSharedPreferences2::class.java)
        if(!repositoryLocal.isDataNeedUpdate(dataLocal.time)){
            withContext(Dispatchers.Main) {
                lifeStateResponse.value = LifeStateModel(data = dataLocal.data)
            }
            return
        }
        managerCallApi(
                call = {
                    retrofitInstance
                            .setHost(WebConfig.HOST_INFO)
                            .setUseToken(false)
                            .builder().instance()
                            .getCatalogLifeStateAsync().await()
                }
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    saveLocalLifeStyle(response)
                    lifeStateResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    private fun saveLocalLifeStyle(response: ResponseData<LifeStateModel?>) {
        val dataResponse = response.data
        dataResponse?.let { response ->
            response.data.let { list ->
                if(list.isNotEmpty()){
                    val data = setDataDefaultCatalogs(list[0])
                    data?.let {
                        list.add(0, it)
                    }
                }
            }
        }
        repositoryLocal.saveData(
                CATALOG_LIFE_STATE,
                DataModelSharedPreferences2(
                        time = repositoryLocal.getDateCurrent(),
                        data = dataResponse!!.data
                ))
    }

    suspend fun getCatalogPrefix(lifestate: String,user: Int) {
        val dataLocal = Gson().fromJson(repositoryLocal.getData(CATALOG_PREFIX), DataModelSharedPreferences::class.java)
        if(!repositoryLocal.isDataNeedUpdate(dataLocal.time)){
            withContext(Dispatchers.Main) {
                prefixResponse.value = PrefixModel(data = dataLocal.data.map {
                    DataWithDescription(
                        id = it.id,
                        description = it.name
                    )
                }.toMutableList())
            }
            return
        }
        managerCallApi(
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_INFO)
                    .setUseToken(false)
                    .setInterceptors(
                        HeaderInterceptor(
                            listOf(
                                RequestHeader(headerName = "X-User-Id", value = user.toString())
                            ))
                    )
                    .builder().instance()
                    .getCatalogPrefixAsync(lifestate).await()
            },validation = ValidationPrefileGeneral()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    response.data?.let {
                        saveLocalPrefix(response)
                        prefixResponse.value = it
                    }
                }
            }
        }
    }

    private fun saveLocalPrefix(response: ResponseData<PrefixModel?>) {
        val dataResponse = response.data
        dataResponse?.let { response ->
            response.data.let { list ->
                if(list.size > 0) {
                    val data = setDataDefaultCatalogs(list[0])
                    data?.let {
                        list.add(0, it)
                    }
                }
            }
        }
    }

    suspend fun getProvidedServices() {
        val dataLocal = Gson().fromJson(repositoryLocal.getData(CATALOG_PROVIDED_SERVICES), DataModelSharedPreferences::class.java)
        if(!repositoryLocal.isDataNeedUpdate(dataLocal.time)){
            withContext(Dispatchers.Main) {
                providedServicesResponse.value = ProvidedServicesModel(data = dataLocal.data.map {
                    DataWithDescription(
                            id = it.id,
                            description = it.name
                    )
                }.toMutableList())
            }
            return
        }
        managerCallApi(
                call = {
                    retrofitInstance
                            .setHost(WebConfig.HOST_INFO)
                            .setUseToken(false)
                            .builder().instance()
                            .getProvidedServicesAsync().await()
                }
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    saveLocalProvidedServices(response)
                    providedServicesResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    private fun saveLocalProvidedServices(response: ResponseData<ProvidedServicesModel?>) {
        val dataResponse = response.data
        dataResponse?.let { response ->
            response.data.let { list ->
                val data = setDataDefaultCatalogs(list[0])
                data?.let {
                    list.add(0, it)
                }
            }
        }
        repositoryLocal.saveData(
                CATALOG_PROVIDED_SERVICES,
                DataModelSharedPreferences(
                        time = repositoryLocal.getDateCurrent(),
                        data = dataResponse!!.data.map {
                            DataWithName(
                                    id = it.id,
                                    name = it.description
                            )
                        }.toMutableList()
                )
        )
    }

    suspend fun getTopics() {
        val dataLocal = Gson().fromJson(repositoryLocal.getData(CATALOG_TOPIC), DataModelSharedPreferences::class.java)
        if(!repositoryLocal.isDataNeedUpdate(dataLocal.time)){
            withContext(Dispatchers.Main) {
                topicResponse.value = TopicsModel(data = dataLocal.data.map {
                    DataWithDescription(
                            id = it.id,
                            description = it.name
                    )
                }.toMutableList())
            }
            return
        }
        managerCallApi(
                call = {
                    retrofitInstance
                            .setHost(WebConfig.HOST_INFO)
                            .setUseToken(false)
                            .builder().instance()
                            .getTopicsAsync().await()
                }
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    saveLocalTopics(response)
                    topicResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    private fun saveLocalTopics(response: ResponseData<TopicsModel?>) {
        val dataResponse = response.data
        dataResponse?.let { response ->
            response.data.let { list ->
                val data = setDataDefaultCatalogs(list[0])
                data?.let {
                    list.add(0, it)
                }
            }
        }
        repositoryLocal.saveData(
                CATALOG_TOPIC,
                DataModelSharedPreferences(
                        time = repositoryLocal.getDateCurrent(),
                        data = dataResponse!!.data.map {
                            DataWithName(
                                    id = it.id,
                                    name = it.description
                            )
                        }.toMutableList()
                )
        )
    }

    suspend fun getCongregations() {
        managerCallApi(
                context = context,
                call = {
                    retrofitInstance
                            .setHost(WebConfig.HOST_INFO)
                            .builder().instance()
                            .getCongregationsAsync().await()
                }
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    congregationsResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    private suspend fun getChurchUser(userId: Int) {
        managerCallApi(
                call = {
                    retrofitInstance.setHost(WebConfig.HOST_INFO).builder().instance()
                            .getFavoriteChurchAsync(userId).await()
                },
                validation = ValidationDetailUser()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    response.data?.let { item ->
                        item.assigned?.let { assign ->
                            eamxcu_preferences.saveData(EAMXEnumUser.CHURCH.name, assign.id)
                        }
                    }
                }
            }
        }
    }

    suspend fun getUserDetail(userId: Int) {
        val dataLocal = Gson().fromJson(repositoryLocal.getData(USER), UserResponse::class.java)
        dataLocal?.let {
            "User local -> $it".log()
        }
        if(dataLocal?.data == null || dataLocal.data.User.id != userId) {
            managerCallApi(
                context = context,
                call = {
                    retrofitInstance.setHost(WebConfig.HOST_USER)
                        .setUseToken(true)
                        .builder().instance()
                        .getUserDetailAsync(userId).await()
                },
                validation = ValidationDetailUser()
            ).let { response ->
                GlobalScope.launch(Dispatchers.Main) {
                    if (response.sucess) {
                        try {
                            repositoryLocal.saveDataUser(response.data)
                            userDetailResponse.value = response.data!!
                        } catch (e:Exception) {
                            errorResponse.value = "Ha ocurrido un error al descargar la información del usuario"
                        }

                    } else {
                        errorResponse.value = response.exception?.message
                    }
                }
            }
       }else{
            GlobalScope.launch(Dispatchers.Main) {
                val imageNew = eamxcu_preferences.getData(EAMXEnumUser.URL_PICTURE_PROFILE_USER.name, EAMXTypeObject.STRING_OBJECT) as String
                "Profile imageNew -> $imageNew".log()
                dataLocal.data.User.image = imageNew
                repositoryLocal.saveDataUser(dataLocal)
                "Profile dataLocal -> $dataLocal".log()
                userDetailResponse.value = dataLocal
            }
        }
    }

    suspend fun getUserDetailAndSaveProfile(userId: Int, useResponse: Boolean = false) {
            managerCallApi(
                context = context,
                call = {
                    retrofitInstance.setHost(WebConfig.HOST_USER).builder().instance()
                        .getUserDetailAsync(userId).await()
                },
                validation = ValidationDetailUser()
            ).let { response ->
                GlobalScope.launch(Dispatchers.Main) {
                    if (response.sucess) {
                        try {
                            repositoryLocal.saveDataUser(response.data)
                            if(useResponse){
                                response.data?.let {
                                    userDetailResponse.value = it
                                }
                            }
                        } catch (e:Exception) {
                            "Ha ocurrido un error al descargar la información del usuario".log()
                        }
                    } else {
                        "Error download profile -> ${response.exception?.message}".log()
                    }
                }
            }
    }

    suspend fun updateUserSingleMarriedWidower(user: UserSingleMarriedWidower) {
        managerCallApi(
                context = context,
                call = {
                    retrofitInstance.setHost(WebConfig.HOST_USER)
                        .setUseToken(true).builder().instance()
                            .postUpdateUserSingleMarriedWidowerAsync(user).await()
                },
            validation = ValidationDefault()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    updateMessage.value = "Actualización exitosa"
                    repositoryLocal.saveDataUser(user)
                } else {
                    errorResponse.value = response.exception?.message ?: "La actualización del usuario no se pudo realizar"
                }
            }
        }
    }

    suspend fun updateUserLaicConsecratedReligious(user: UserLaicConsecratedReligious) {
        managerCallApi(
                context = context,
                call = {
                    retrofitInstance.setHost(WebConfig.HOST_USER)
                        .setUseToken(true).builder().instance()
                            .postUpdateUserLaicConsecratedReligiousAsync(user).await()
                },
            validation = ValidationDefault()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    updateMessage.value = "Actualización exitosa"
                    repositoryLocal.saveDataUser(user)
                } else {
                    errorResponse.value = response.exception?.message ?: "La actualización del usuario no se pudo realizar"
                }
            }
        }
    }

    suspend fun updateUserLaic(user: UserLaic) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(WebConfig.HOST_USER)
                    .setUseToken(true).builder().instance()
                    .postUpdateUserLaicAsync(user).await()
            },
            validation = ValidationDefault()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    updateMessage.value = "Actualización exitosa"
                    repositoryLocal.saveDataUser(user)
                } else {
                    errorResponse.value = response.exception?.message ?: "La actualización del usuario no se pudo realizar"
                }
            }
        }
    }

    suspend fun updateUserDiacono(user: UserDiacono) {
        managerCallApi(
                context = context,
                call = {
                    retrofitInstance.setHost(WebConfig.HOST_USER).setUseToken(true)
                        .builder().instance()
                            .postUpdateUserDiaconoAsync(user).await()
                },
            validation = ValidationDefault()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    updateMessage.value = "Actualización exitosa"
                    repositoryLocal.saveDataUser(user)
                } else {
                    errorResponse.value = response.exception?.message ?: "La actualización del usuario no se pudo realizar"
                }
            }
        }
    }

    suspend fun getChurch(name : String? = null) {
        managerCallApi(
                context = context,
                call = {
                    if(name == null) {
                        retrofitInstance.setHost(WebConfig.HOST_INFO).builder().instance()
                                .getChurchAsync().await()
                    }else{
                        retrofitInstance.setHost(WebConfig.HOST_INFO).builder().instance()
                                .getChurchAsync(name).await()
                    }
                },validation = ValidationPrefileGeneral()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    iglesiasMapResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getCommunitiesByName(type_location: String, name: String) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(WebConfig.HOST_INFO).builder().instance().getCommunitiesByNameAsync(type_location, name)?.await()!!
            },validation = ValidationPrefileGeneral()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    iglesiasMapResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getModules(location_id: Int) {
        managerCallApi(
                context = context,
                call = {
                    retrofitInstance
                            .setHost(WebConfig.HOST_INFO)
                            .builder().instance()
                            .getModulesAsync(location_id).await()
                }
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    getModulesResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun updateModules(location_id: Int, modules: List<RequestModuleUpdate>) {
        managerCallApi(
                context = context,
                call = {
                    retrofitInstance
                            .setHost(WebConfig.HOST_INFO)
                            .builder().instance()
                            .updateModulesAsync(location_id, modules).await()
                }
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    updateModulesResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getCollaborators(location_id: Int, name: String) {
        managerCallApi(
                context = context,
                call = {
                    retrofitInstance
                            .setHost(WebConfig.HOST_INFO)
                            .builder().instance()
                            .getCollaboratorsAsync(location_id, name).await()
                },validation = ValidationCommun()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    response.data?.let {
                        getCollaboratorsResponse.value = it
                    }?: run {
                        errorResponse.value = response.exception?.message
                    }
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getCollaboratorDetail(location_id: Int, user_id: Int, user : Int) {
        managerCallApi(
                call = {
                    retrofitInstance
                            .setHost(WebConfig.HOST_INFO)
                            .setInterceptors(
                                HeaderInterceptor(
                                    listOf(
                                        RequestHeader(headerName = "X-User-Id", value = user.toString())
                                    ))
                            )
                            .builder().instance()
                            .getCollaboratorDetailAsync(location_id, user_id).await()
                },
            validation = ValidationCollaborators()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    if (response.data != null) {
                        collaboratorDetailResponse.value = response.data!!
                    }else {
                        errorResponse.value = response.toString()
                    }
                } else {
                    errorResponseExit.value = response.exception?.message
                }
            }
        }
    }

    suspend fun updateModulesOfCollaborator(
            location_id: Int,
            user_id: Int,
            modules: List<RequestModuleUpdate>,
            user: Int,
    ) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_INFO)
                    .setInterceptors(
                        HeaderInterceptor(
                            listOf(
                                RequestHeader(headerName = "X-User-Id", value = user.toString())
                            ))
                    )
                    .builder().instance()
                    .updateModulesOfCollaboratorAsync(location_id, user_id, modules).await()
            },
            validation = ValidationCollaborators()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    successRemoveUser.value = response.sucess
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getUploadImage(requestUrl: ImageRequestModel) {
        val response = managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(WebConfig.HOST_INFO).builder().instance()
                    .getUpdateImageAsync(requestUrl).await()
            },
        )
        if (response.sucess) {
            response.data?.let { url ->
                val name = "picture_profile_encuentro_${Calendar.getInstance().timeInMillis}"
//                    FunImagen().descargaImagen(context,url.url,name)
                eamxcu_preferences.apply {
                    saveData(EAMXEnumUser.URL_PHOTO_UPDATE.name, true)
                    saveData(EAMXEnumUser.URL_PICTURE_PROFILE_USER.name, url.url)
                    saveData(EAMXEnumUser.URL_PICTURE_PROFILE_USER_LOCAL.name, name)
                }
                responseUpdateImage.postValue(url.url)
            }
        } else {
            errorResponse.value = "No fue posible subir la imagen de perfil"
        }
    }

    suspend fun deleteAccount(onSuccess: (Boolean, String?) -> Unit) {
        val response = managerCallApi(context = context, call = {
            retrofitInstance.setHost(WebConfig.HOST_USER).builder().instance()
                .deleteAccount(
                    JsonObject().apply {
                        addProperty("email",eamxcu_preferences.email ?: "")
                    }
                ).await()
        }, validation = object : ValidationCode<Response<*>> {
            override fun executeValidation(response: Response<*>) {
                if (!response.isSuccessful)
                    throw BackendException("No fue posible eliminar tu cuenta, intente más tarde.")
            }

        })
        onSuccess(
            response.sucess,
            if (!response.sucess) response.exception?.message else null
        )
    }

    private fun <T> setDataDefaultCatalogs(dataItem: T): T? {
        var isDescription = false
        var isCode = false
        dataItem?.let {
            isDescription = dataItem is DataWithDescription
            isCode = dataItem is DataWithNameCode
            return if (isDescription) {
                DataWithDescription(id = 0, description = SELECT_ITEM) as T
            } else if (isCode) {
                DataWithNameCode(id = 0, name = SELECT_ITEM, code = "") as T
            } else {
                DataWithName(id = 0, name = SELECT_ITEM) as T
            }
        }
        return null
    }
    fun getLocation(): MutableLiveData<Location> {
        var locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationResponse.value = location
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        try {
            // Request location updates
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0L,
                0f,
                locationListener
            )
        } catch (ex: SecurityException) {
            errorResponse.value = "No fue posible obtener sus datos de geolocalización."
        }

        return locationResponse
    }
}
