package mx.arquidiocesis.eamxprofilemodule.repository

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.interceptor.HeaderInterceptor
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxcommonutils.retrofit.model.dataclass.ResponseData
import mx.arquidiocesis.eamxcommonutils.retrofit.model.header.RequestHeader
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.log
import mx.arquidiocesis.eamxprofilemodule.config.WebConfig
import mx.arquidiocesis.eamxprofilemodule.model.*
import mx.arquidiocesis.eamxprofilemodule.model.local.DataModelSharedPreferences
import mx.arquidiocesis.eamxprofilemodule.model.update.userupdatecaselaicconsecratedreligious.UserLaicConsecratedReligious
import mx.arquidiocesis.eamxprofilemodule.model.update.userupdatecasesinglemarriedwidower.UserSingleMarriedWidower
import mx.arquidiocesis.eamxprofilemodule.model.update.userupdatediacono.UserDiacono
import mx.arquidiocesis.registrosacerdote.model.userdetailpriest.LifeStatusModel
import mx.arquidiocesis.eamxprofilemodule.retrofit.ApiInterface
import mx.arquidiocesis.registrosacerdote.retrofit.validation.ValidationCollaborators
import mx.arquidiocesis.registrosacerdote.retrofit.validation.ValidationDetailUser
import mx.arquidiocesis.eamxprofilemodule.viewmodel.SELECT_ITEM
import mx.arquidiocesis.registrosacerdote.retrofit.validation.ValidationPrefileGeneral
import java.util.*
/*
const val CATALOG_LIFE_STATE = "LIFE_STATE"
const val CATALOG_PROVIDED_SERVICES = "PROVIDED_SERVICES"
const val CATALOG_TOPIC = "TOPIC"
const val USER = "USER"*/

class RepositoryCatalog(val context: Context) : ManagerCall() {
/*
    private val repositoryCatalog = RepositoryLocalCatalog();

    private val retrofitInstance = RetrofitApp.Build<ApiInterface>()
            .setContext(context)
            .setClass(ApiInterface::class.java)
            .setEnvironment(true)

    suspend fun getLifeSateList() {

        val uno = repositoryCatalog.getData(CATALOG_LIFE_STATE, LifeStatusModel::class.java)


        val dataLocal = Gson().fromJson(repositoryCatalog.getData(CATALOG_LIFE_STATE), DataModelSharedPreferences::class.java)
        if(!repositoryCatalog.isDataNeedUpdate(dataLocal.time)){
            withContext(Dispatchers.Main) {
                lifeStateResponse.value = LifeStateModel(data = dataLocal.data)
            }
            return
        }
        managerCallApi(
                call = {
                    retrofitInstance
                            .setHost(WebConfig.HOST_INFO)
                            .builder().instance()
                            .getCatalogLifeStateAsync().await()
                }
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    saveLocalLifeStyle(response)
                    lifeStateResponse.value = response.data
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
                val data = setDataDefaultCatalogs(list[0])
                data?.let {
                    list.add(0, it)
                }
            }
        }
        repositoryCatalog.saveData(
                CATALOG_LIFE_STATE,
                DataModelSharedPreferences(
                        time = repositoryCatalog.getDateCurrent(),
                        data = dataResponse!!.data
                ))
    }

    suspend fun getProvidedServices() {
        val dataLocal = Gson().fromJson(repositoryCatalog.getData(CATALOG_PROVIDED_SERVICES), DataModelSharedPreferences::class.java)
        if(!repositoryCatalog.isDataNeedUpdate(dataLocal.time)){
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
                            .builder().instance()
                            .getProvidedServicesAsync().await()
                }
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    saveLocalProvidedServices(response)
                    providedServicesResponse.value = response.data
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
        repositoryCatalog.saveData(
                CATALOG_PROVIDED_SERVICES,
                DataModelSharedPreferences(
                        time = repositoryCatalog.getDateCurrent(),
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
        val dataLocal = Gson().fromJson(repositoryCatalog.getData(CATALOG_TOPIC), DataModelSharedPreferences::class.java)
        if(!repositoryCatalog.isDataNeedUpdate(dataLocal.time)){
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
                            .builder().instance()
                            .getTopicsAsync().await()
                }
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    saveLocalTopics(response)
                    topicResponse.value = response.data
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
        repositoryCatalog.saveData(
                CATALOG_TOPIC,
                DataModelSharedPreferences(
                        time = repositoryCatalog.getDateCurrent(),
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
                call = {
                    retrofitInstance
                            .setHost(WebConfig.HOST_INFO)
                            .builder().instance()
                            .getCongregationsAsync().await()
                }
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    congregationsResponse.value = response.data
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
        //val dataLocal = Gson().fromJson(repositoryLocal.getData(USER), UserDetailModel::class.java)
        /*dataLocal?.let {
            "User local -> $it".log()
        }*/
        //if(dataLocal?.data == null || dataLocal.data.User.id != userId) {
            managerCallApi(
                call = {
                    retrofitInstance.setHost(WebConfig.HOST_USER).builder().instance()
                        .getUserDetailAsync(userId).await()
                },
                validation = ValidationDetailUser()
            ).let { response ->
                GlobalScope.launch(Dispatchers.Main) {
                    if (response.sucess) {
                        try {
                            repositoryCatalog.saveDataUser(response.data)
                            userDetailResponse.value = response.data!!
                        } catch (e:Exception) {
                            errorResponse.value = "Ha ocurrido un error al descargar la información del usuario"
                        }

                    } else {
                        errorResponse.value = response.exception?.message
                    }
                }
            }
       /*}else{
            GlobalScope.launch(Dispatchers.Main) {
                val imageNew = eamxcu_preferences.getData(EAMXEnumUser.URL_PICTURE_PROFILE_USER.name, EAMXTypeObject.STRING_OBJECT) as String
                "Profile imageNew -> $imageNew".log()
                dataLocal.data.User.image = imageNew
                repositoryLocal.saveDataUser(dataLocal)
                "Profile dataLocal -> $dataLocal".log()
                userDetailResponse.value = dataLocal
            }
        }*/
    }

    suspend fun getUserDetailAndSaveProfile(userId: Int) {
            managerCallApi(
                call = {
                    retrofitInstance.setHost(WebConfig.HOST_USER).builder().instance()
                        .getUserDetailAsync(userId).await()
                },
                validation = ValidationDetailUser()
            ).let { response ->
                GlobalScope.launch(Dispatchers.Main) {
                    if (response.sucess) {
                        try {
                            repositoryCatalog.saveDataUser(response.data)
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
                call = {
                    retrofitInstance.setHost(WebConfig.HOST_USER).builder().instance()
                            .postUpdateUserSingleMarriedWidowerAsync(user).await()
                }
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    updateMessage.value = "Actualización exitosa"
                    repositoryCatalog.saveDataUser(user)
                } else {
                    errorResponse.value = response.exception?.message ?: "La actualización del usuario no se pudo realizar"
                }
            }
        }
    }

    suspend fun updateUserLaicConsecratedReligious(user: UserLaicConsecratedReligious) {
        managerCallApi(
                call = {
                    retrofitInstance.setHost(WebConfig.HOST_USER).builder().instance()
                            .postUpdateUserLaicConsecratedReligiousAsync(user).await()
                }
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    updateMessage.value = "Actualización exitosa"
                    repositoryCatalog.saveDataUser(user)
                } else {
                    errorResponse.value = response.exception?.message ?: "La actualización del usuario no se pudo realizar"
                }
            }
        }
    }

    suspend fun updateUserDiacono(user: UserDiacono) {
        managerCallApi(
                call = {
                    retrofitInstance.setHost(WebConfig.HOST_USER).builder().instance()
                            .postUpdateUserDiaconoAsync(user).await()
                }
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    updateMessage.value = "Actualización exitosa"
                    repositoryCatalog.saveDataUser(user)
                } else {
                    errorResponse.value = response.exception?.message ?: "La actualización del usuario no se pudo realizar"
                }
            }
        }
    }

    suspend fun getChurch(name : String? = null) {
        managerCallApi(
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
                call = {
                    retrofitInstance
                            .setHost(WebConfig.HOST_INFO)
                            .builder().instance()
                            .getModulesAsync(location_id).await()
                }
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    getModulesResponse.value = response.data
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun updateModules(location_id: Int, modules: List<RequestModuleUpdate>) {
        managerCallApi(
                call = {
                    retrofitInstance
                            .setHost(WebConfig.HOST_INFO)
                            .builder().instance()
                            .updateModulesAsync(location_id, modules).await()
                }
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    updateModulesResponse.value = response.data
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getCollaborators(location_id: Int, name: String) {
        managerCallApi(
                call = {
                    retrofitInstance
                            .setHost(WebConfig.HOST_INFO)
                            .builder().instance()
                            .getCollaboratorsAsync(location_id, name).await()
                }
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    getCollaboratorsResponse.value = response.data
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
                    updateModulesOfCollaboratorResponse.value = null
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getUploadImage(requestUrl: ImageRequestModel) {
        managerCallApi(
            call = {
                retrofitInstance.setHost(WebConfig.HOST_INFO).builder().instance().getUpdateImageAsync(requestUrl).await()
            },
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    response.data?.let { url ->
                        eamxcu_preferences.apply {
                            saveData(EAMXEnumUser.URL_PHOTO_UPDATE.name, true)
                            saveData(EAMXEnumUser.URL_PICTURE_PROFILE_USER.name, url.url)
                            saveData(EAMXEnumUser.URL_PICTURE_PROFILE_USER_LOCAL.name, "picture_profile_encuentro_${Calendar.getInstance().timeInMillis}")
                        }
                        responseUpdateImage.value = true
                    }
                } else {
                    errorResponse.value = "No fue posible subir la imagen de perfil"
                }
            }
        }
    }

    private fun <T> setDataDefaultCatalogs(dataItem: T): T? {
        var isDescription = false
        dataItem?.let {
            isDescription = dataItem is DataWithDescription
            return if (isDescription) {
                DataWithDescription(id = 0, description = SELECT_ITEM) as T
            } else {
                DataWithName(id = 0, name = SELECT_ITEM) as T
            }
        }
        return null
    }*/
}
