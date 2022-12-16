package mx.arquidiocesis.eamxcommonutils.util.permission

import android.app.Activity
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

const val MULTIPLE_PERMISSIONS : Int = 834

class UtilValidPermission {

    fun validListPermissionsAndBuildRequest(
        fragment: Fragment,
        arrayPermissions: ArrayList<String>,
        CODE_PERMISSION_CUSTOM : Int? = null
    ) : Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val listPermissionsNeeded: MutableList<String> = mutableListOf()
            for (itemPermission in arrayPermissions) {
                val result = ContextCompat.checkSelfPermission(fragment.requireContext(), itemPermission)
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(itemPermission)
                }
            }

            if(listPermissionsNeeded.isNotEmpty()){
                fragment.requestPermissions(
                    listPermissionsNeeded.toTypedArray(),
                        CODE_PERMISSION_CUSTOM ?: MULTIPLE_PERMISSIONS
                )
                false
            }else {
                true
            }
        }else{
            true
        }
    }

    fun validListPermissionsAndBuildRequestActivity(
            activity: AppCompatActivity,
            arrayPermissions: ArrayList<String>,
            CODE_PERMISSION_CUSTOM : Int? = null
    ) : Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val listPermissionsNeeded: MutableList<String> = mutableListOf()
            for (itemPermission in arrayPermissions) {
                val result = ContextCompat.checkSelfPermission(activity, itemPermission)
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(itemPermission)
                }
            }

            if(listPermissionsNeeded.isNotEmpty()){
                activity.requestPermissions(
                        listPermissionsNeeded.toTypedArray(),
                        CODE_PERMISSION_CUSTOM ?: MULTIPLE_PERMISSIONS
                )
                false
            }else {
                true
            }
        }else{
            true
        }
    }


    fun allPermissionsAreAgree(results :  IntArray) = (results.isNotEmpty() &&
                results.filter { it == PackageManager.PERMISSION_GRANTED }.size == results.size)


    fun isGpsAvailableInDevice(activity: Activity, locationServices: String) : Boolean{
        val locationManager =  activity.getSystemService(locationServices) as LocationManager?
        return locationManager?.isLocationEnabled ?: false
    }
}