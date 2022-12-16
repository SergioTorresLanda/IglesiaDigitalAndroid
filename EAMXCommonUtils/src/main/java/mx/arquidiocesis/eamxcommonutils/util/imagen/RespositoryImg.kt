package mx.arquidiocesis.eamxcommonutils.util.imagen

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
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import java.io.File
import java.io.FileOutputStream


class RespositoryImg(val context: Context) : ManagerCall() {

    val locationResponse = MutableLiveData<Location>()
    val errorResponse = MutableLiveData<String>()


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
                    setFile(resource,file)
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                    // this is called when imageView is cleared on lifecycle call or for
                    // some other reason.
                    // if you are referencing the bitmap somewhere else too other than this imageView
                    // clear it here as you can no longer have the bitmap
                }
            })
    }
    fun setFile(resource: Bitmap, file: File){
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