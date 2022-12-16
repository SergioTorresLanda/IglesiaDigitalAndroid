package mx.arquidiocesis.misiglesias.repository

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.misiglesias.config.WebConfig
import mx.arquidiocesis.misiglesias.model.MassesModelM
import mx.arquidiocesis.misiglesias.retrofit.ApiInterface

class RepositoryMeasses(val context: Context) : ManagerCall(){

    val misasResponse = MutableLiveData<List<MassesModelM>>()
    val buscarResponse = MutableLiveData<List<MassesModelM>>()
    val errorResponse = MutableLiveData<String>()
    val locationResponse = MutableLiveData<Location>()

    private val retrofitInstance = RetrofitApp.Build<ApiInterface>()
        .setHost(WebConfig.HOST_NEW)
        .setContext(context)
        .setClass(ApiInterface::class.java)
        .builder().instance()

    suspend fun getPrayersType(location: Location) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getTypePrayersAsync(
                    location.latitude.toString(),
                    location.longitude.toString()).await()
            },
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

    suspend fun getMasses(name:String, location: Location?){
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.getMassesAsync(
                    name,location?.latitude.toString(),location?.longitude.toString()).await()
            },
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    buscarResponse.value = response.data
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    fun getLocation(context: Context): MutableLiveData<Location> {
        var locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        val locationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationResponse.value=location
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

}