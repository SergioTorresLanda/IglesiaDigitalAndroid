

package mx.arquidiocesis.eamxmaps.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxmaps.config.WebConfig.HOST_NEW
import mx.arquidiocesis.eamxmaps.model.IgleciasModel
import mx.arquidiocesis.eamxmaps.retrofit.ApiInterface
import mx.arquidiocesis.eamxmaps.retrofit.validationlayer.ValidationCodes
import java.io.File
import java.io.FileOutputStream

class Repository(val context: Context) : ManagerCall() {

    val iglesiasMapResponse = MutableLiveData<List<IgleciasModel>>()
    val locationResponse = MutableLiveData<Location>()
    val errorResponse = MutableLiveData<String>()

    private val retrofitInstance = RetrofitApp.Build<ApiInterface>()
        .setContext(context)
        .setEnvironment(true)
        .setClass(ApiInterface::class.java)



    suspend fun getiglesiasList() {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(HOST_NEW)
                    .builder().instance().getListadoMapIglesiasAsync("CHURCH").await()
            },validation = ValidationCodes()
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

    suspend fun getiglesiasList(name: String) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(HOST_NEW)
                    .builder().instance().getListadoMapNameIglesiasAsync(name,"CHURCH").await()
            },validation = ValidationCodes()
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


    fun imagenInterna(nombre: String): File {
        val folder = File(context.applicationContext.filesDir.toString() + "/images/")
        folder.mkdir()
        return File(folder, "$nombre.jpg")
    }

    fun descargarImagen(ruta: String, file: File) {

        Glide.with(context)
            .asBitmap()
            .load(Uri.parse(ruta))
            .override(300, 200)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    var dest = file
                    try {
                        val out = FileOutputStream(dest)
                        resource.compress(Bitmap.CompressFormat.JPEG, 90, out)
                        out.flush()
                        out.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // this is called when imageView is cleared on lifecycle call or for
                    // some other reason.
                    // if you are referencing the bitmap somewhere else too other than this imageView
                    // clear it here as you can no longer have the bitmap
                }
            })
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
    fun updateImage(ruta: String, file: File, listener : (Boolean) -> Unit) {

        Glide.with(context)
            .asBitmap()
            .load(Uri.parse(ruta))
            .override(300, 200)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    val dest = file
                    try {
                        val out = FileOutputStream(dest)
                        resource.compress(Bitmap.CompressFormat.JPEG, 90, out)
                        out.flush()
                        out.close()
                        listener(true)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        listener(false)
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // this is called when imageView is cleared on lifecycle call or for
                    // some other reason.
                    // if you are referencing the bitmap somewhere else too other than this imageView
                    // clear it here as you can no longer have the bitmap
                }
            })
    }
}