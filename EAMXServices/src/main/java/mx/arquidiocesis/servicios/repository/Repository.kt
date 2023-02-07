package mx.arquidiocesis.servicios.repository

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.servicios.config.WebConfigSer
import mx.arquidiocesis.servicios.model.*
import mx.arquidiocesis.servicios.retrofit.ApiInterface
import mx.arquidiocesis.servicios.retrofit.validationlayer.ValidationCodes

import okhttp3.ResponseBody

class Repository(val context: Context) : ManagerCall() {
    private val MASSES = "MASSES"
    private val SACRAMENT = "SACRAMENT"
    private val OTHER = "OTHERS"

    val serviciosResponse = MutableLiveData<List<ServiceMenuMainModel>>()
    val sacramentsResponse = MutableLiveData<List<SacramentsModel>>()
    val celebrationResponse = MutableLiveData<List<ServicesModel>>()
    val blessigResponse = MutableLiveData<List<ServicesModel>>()
    val otherServicesResponse = MutableLiveData<List<ServicesModel>>()
    val postServicesResponse = MutableLiveData<ResponseBody>()
    val iglesiasResponse = MutableLiveData<MisIgleciasModel>()
    val churchesResponse = MutableLiveData<List<IgleciasModel>>()
    val findChurchesResponse = MutableLiveData<List<IgleciasModel>>()
    val churchDetailResponse = MutableLiveData<ChurchDetaillModel>()
    val errorResponse = MutableLiveData<String>()
    val errorResponseExit = MutableLiveData<String>()
    val misasResponse = MutableLiveData<List<IglesiasModel>>()
    val locationResponse = MutableLiveData<Location>()
    val massesScheduleResponse = MutableLiveData<ScheduleModel>()
    val mentionResponse = MutableLiveData<ResponseBody>()
    val intentionsResponse = MutableLiveData<List<Intention>>()
    val zipCodeResponse = MutableLiveData<ZipCodeModel>()
    val mainCommunityResponse = MutableLiveData<MainCommunityResponse>()
    val communitiesResponse = MutableLiveData<List<CommunityResponse>>()

    var userId = 0

    init {
        userId = eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID.name,
            EAMXTypeObject.INT_OBJECT
        ) as Int
    }


    private val retrofitInstance = RetrofitApp.Build<ApiInterface>()
        .setHost(WebConfigSer.HOST)
        .setContext(context)
        .setEnvironment(true)
        .setClass(ApiInterface::class.java)
        .builder().instance()


    suspend fun getSacramentsList() {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getSacramentsAsync(
                    SACRAMENT
                ).await()
            },validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    sacramentsResponse.value = response?.data?.sortedBy { item -> item.id }
                } else {
                    errorResponse.value = response!!.exception?.message
                }
            }
        }
    }

    suspend fun getOtherServicesList() {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getServicesTypeAsync(
                    OTHER
                ).await()
            },validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    otherServicesResponse.value = response?.data?.sortedBy { item -> item.id }
                } else {
                    errorResponse.value = response!!.exception?.message
                }
            }
        }
    }

    suspend fun serviciosList() {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getServiciosAsync().await()
            },validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    serviciosResponse.value = response.data
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getCelebrationList() {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getServiciosTypeAsync("CELEBRATION").await()
            },validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    celebrationResponse.value = response.data
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getBlessigList() {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getServiciosTypeAsync("BLESSING").await()
            },validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    blessigResponse.value = response.data
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun setService(services: JsonObject,userId: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.postServiceAsync(userId,services).await()
            },validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    postServicesResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun misasList(location: Location) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getMassesAsync(
                    MASSES,
                    location.latitude.toString(),
                    location.longitude.toString()
                ).await()
            },validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    misasResponse.value = response.data
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    fun getLocation(): MutableLiveData<Location> {
        var locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        val locationListener: LocationListener = object : LocationListener {
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
            errorResponse.value = "Error : $ex"
        }

        return locationResponse
    }

    suspend fun iglesiasList(id: Int, esSacerdote: String) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getFavoriteChurchAsync(id, "CHURCH").await()
            },validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    iglesiasResponse.value = response.data
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getAllChurches(type_location: String) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getListadoMapIglesiasAsync(type_location).await()
            },validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    churchesResponse.value = response.data
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getFindChurches(churh: String) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getBuscarIglesiasAsync(churh).await()
            },validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    churchesResponse.value = response.data
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getChurchDetail(idChurch: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getChurchDetailAsync(userId, idChurch).await()
            },validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    churchDetailResponse.value = response.data
                } else {
                    errorResponseExit.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getMassesSchedule(locationId: Int, type: String) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getMassesScheduleAsync(userId, locationId, type).await()
            },validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    massesScheduleResponse.value = response.data
                } else {
                    errorResponseExit.value = response.exception?.message
                }
            }
        }
    }

    suspend fun sendMention(mentionRequest: MentionRequestPost) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.sendMentionAsync(userId, mentionRequest).await()
            },validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    mentionResponse.value = response.data
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getIntentions() {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getIntentions("INTENTION").await()
            },validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    intentionsResponse.value = response.data
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
                retrofitInstance.getZipCode(code).await()
            },validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    response.data?.let {d->
                        zipCodeResponse.value = d
                    }?:run {
                        errorResponse.value = response.exception?.message
                    }
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getMainCommunity(category: String) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getMainCommunity(userId, category)?.await()!!
            },validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    mainCommunityResponse.value = response.data!!
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
            },validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    communitiesResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }
}