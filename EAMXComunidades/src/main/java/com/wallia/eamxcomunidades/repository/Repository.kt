package com.wallia.eamxcomunidades.repository

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.wallia.eamxcomunidades.config.WebConfig
import com.wallia.eamxcomunidades.database.instance.MeetRoomDataBaseCommunity
import com.wallia.eamxcomunidades.database.model.CommunityLocal
import com.wallia.eamxcomunidades.model.*
import com.wallia.eamxcomunidades.retrofit.ApiInterface
import com.wallia.eamxcomunidades.retrofit.validationlayer.ValidationDefault
import com.wallia.eamxcomunidades.utils.PublicFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.interceptor.HeaderInterceptor
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxcommonutils.retrofit.model.header.RequestHeader
import mx.arquidiocesis.eamxcommonutils.retrofit.model.inter.ValidationCode
import mx.arquidiocesis.eamxcommonutils.util.downloadImageUrlToBitArray
import okhttp3.ResponseBody

class Repository(val context: Context, val database : MeetRoomDataBaseCommunity) : ManagerCall() {

    val userDetailResponse = MutableLiveData<UserDetailModel>()
    val errorResponse = MutableLiveData<String>()
    val genericResponse = MutableLiveData<ResponseBody?>()
    val completeCommunityResponse = MutableLiveData<Boolean>()

    val communityModuleResponse = MutableLiveData<CommunityModuleResponse>()
    val mainCommunityResponse = MutableLiveData<MainCommunityResponse>()
    val getCommunityTypesResponse = MutableLiveData<CommunityType>()
    val getAllCommunitiesResponse = MutableLiveData<AllCommunitiesResponse>()
    val getCommunitiesByLocationsResponse = MutableLiveData<CommunitiesByLocationsResponse>()
    val getCommunitiesByNameResponse = MutableLiveData<CommunitiesByNameResponse>()
    val getCommunitiesSearchByNameResponse = MutableLiveData<CommunitiesByNameResponse>()
    val getCommunityDetailResponse = MutableLiveData<CommunityDetailResponse>()
    val getActivitiesResponse = MutableLiveData<ActivitiesResponse>()
    val getPartnerCommunitiesResponse = MutableLiveData<PartnerCommunitiesResponse>()
    val reviewsResponse = MutableLiveData<ReviewsResponse>()
    val reviewResponse = MutableLiveData<ReviewItem>()
    val locationResponse = MutableLiveData<Location>()
    private var userIdHeader: Int
    private val headerUser = "X-User-Id"
    private val publicFunctions = PublicFunctions()

    init {
        userIdHeader = publicFunctions.getUserId()
    }

    private val retrofitInstance = RetrofitApp.Build<ApiInterface>()
        .setHost(WebConfig.HOSTAPI)
        .setContext(context)
        .setEnvironment(true)
        .setInterceptors(
            HeaderInterceptor(
                listOf(
                    RequestHeader(
                        headerName = headerUser,
                        value = userIdHeader.toString()
                    )
                )
            )
        )
        .setClass(ApiInterface::class.java)
        .builder()
        .instance()

    private val retrofitInstanceOauth = RetrofitApp.Build<ApiInterface>()
        .setHost(WebConfig.HOST_USER)
        .setContext(context)
        .setEnvironment(true)
        .setClass(ApiInterface::class.java)
        .builder()
        .instance()

    suspend fun getUserDetail(id: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstanceOauth.getUserDetail(id).await()
            },
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    userDetailResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getCommunityModules(location_id: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getCommunityModules(location_id)?.await()!!
            },
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    communityModuleResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getMainCommunity(category: String) {
        val communityList = database.communityDao().getAll(userIdHeader)
        //if(communityList.isEmpty()) {
            managerCallApi(
                context = context,
                call = {
                    retrofitInstance.getMainCommunity(userIdHeader, category)?.await()!!
                },
                validation = ValidationDefault()
            ).let { response ->
                GlobalScope.launch(Dispatchers.Main) {
                    if (response.sucess) {
                        response.data?.let {d->
                            saveCommunityLocal(d)
                            mainCommunityResponse.value = d
                        }?: run {
                            errorResponse.value = response.exception?.message
                        }

                    } else {
                        errorResponse.value = response.exception?.message
                    }
                }
            }
        /*}else{
            GlobalScope.launch(Dispatchers.Main) {
                mainCommunityResponse.value = communityList.convertToLocationCommunity()
            }
        }*/
    }

    private fun saveCommunityLocal(communityData: MainCommunityResponse) {
        communityData.assigned?.let { assigned ->
            downloadAssigned(assigned){
                communityData.locations?.let { locations ->
                    downloadLocation(locations){
                        saveInDatabase(communityData)
                    }
                } ?: kotlin.run {
                    saveInDatabase(communityData)
                }
            }
        } ?: kotlin.run {
            communityData.locations?.let {
                downloadLocation(it){
                    saveInDatabase(communityData)
                }
            }
        }
    }

    private fun downloadAssigned(assigned : com.wallia.eamxcomunidades.model.Location , listener : () -> Unit){
        assigned.imageUrl?.downloadImageUrlToBitArray(context){
            assigned.arrayImage = it
            listener()
        }
    }

    private fun downloadLocation(locations : List<com.wallia.eamxcomunidades.model.Location>, listener : () -> Unit){
        if(locations.isEmpty()){
            listener()
        }

        locations.forEachIndexed{ index, item ->
            item.imageUrl?.downloadImageUrlToBitArray(context){
                item.arrayImage = it
                if(index == (locations.size - 1)){
                    listener()
                }
            }
        }
    }

    private fun saveInDatabase(communityData: MainCommunityResponse) {
        database.communityDao().addCommunities(communityData.convertToCommunityLocal(userIdHeader))
    }

    suspend fun postFavoriteCommunity(jsonObject: JsonObject, communityDetail : CommunityDetailResponse?=null, isMain: Boolean?=null) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.postFavoriteCommunity(userIdHeader, jsonObject)?.await()!!
            }, validation = ValidationDefault()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    communityDetail?.let {
                        saveCommunityFavoriteLocal(it, isMain)
                    }
                    genericResponse.value = response.data!!
                } else {
                    errorResponse.value =
                        "Este usuario ya tiene el maximo de sedes favoritas registradas"
                }
            }
        }
    }

    private fun saveCommunityFavoriteLocal(location: CommunityDetailResponse, isMain: Boolean?) {
        location.imageUrl?.let {
            it.downloadImageUrlToBitArray(context) { buffer ->
                database.communityDao().addCommunity(
                    CommunityLocal(
                        idCommunity = location.id ?: 0,
                        userId = userIdHeader,
                        isCommunityMain = isMain ?: false,
                        name = location.name ?: "",
                        image_url = location.imageUrl,
                        arrayImage = buffer
                    )
                )
            }
        }
    }


    suspend fun deleteCommunity(location_id: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.deleteCommunity(userIdHeader, location_id)?.await()!!
            }, validation = ValidationDefault()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    database.communityDao().deleteCommunity(userIdHeader, location_id)
                    genericResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getCommunityTypes() {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getCommunityTypes()?.await()!!
            },
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    getCommunityTypesResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getAllCommunities(type_location: String) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getAllCommunities(type_location)?.await()!!
            },
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    getAllCommunitiesResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getCommunitiesByLocations(
        type_location: String,
        latitude: String,
        longitude: String
    ) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getCommunitiesByLocations(type_location, latitude, longitude)
                    ?.await()!!
            },validation = ValidationDefault()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    getCommunitiesByLocationsResponse.value = response.data!!
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
                retrofitInstance.getCommunitiesByNameAsync(type_location, name)?.await()!!
            },validation = ValidationDefault()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    getCommunitiesByNameResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getCommunitiesSearchByName(name: String) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getCommunitiesSearchByNameAsync("COMMUNITY", name)?.await()!!
            },
            validation = ValidationDefault()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    getCommunitiesByNameResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getCommunityDetail(location_id: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .getCommunityDetailAsync(location_id)?.await()!!
            },
            validation = ValidationDefault()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                 if (response.sucess) {
                    getCommunityDetailResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getActivities(location_id: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getActivitiesAsync(location_id)?.await()!!
            }, validation = ValidationDefault()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    getActivitiesResponse.value = response.data!!
                } else {
                    // errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getPartnerCommunities(location_id: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getPartnerCommunitiesAsync(location_id, true)?.await()!!
            },validation = ValidationDefault()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    getPartnerCommunitiesResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun createCommunity(createCommunityRequest: CreateCommunityRequest) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.createCommunity(userIdHeader, createCommunityRequest)?.await()!!
            },
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    if (response.data != null) {
                        genericResponse.value = response.data
                    } else {
                        genericResponse.value = null
                        /*errorResponse.value =
                            "Tu registro no pudo ser completado, intenta de nuevo"*/
                    }
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun editCommunity(editCommunityRequest: EditCommunityRequest) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.editCommunity(editCommunityRequest)?.await()!!
            },
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    genericResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun completeCommunity(church: Int, completeCommunityRequest: CompleteCommunityRequest) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.completeCommunityAsync(church, completeCommunityRequest)?.await()!!
            },
            validation = ValidationDefault()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    completeCommunityResponse.value = true
                } else {
                    errorResponse.value = "Tu registro no pudo ser actualizado, intenta de nuevo"
                }
            }
        }
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