package mx.arquidiocesis.eamxcommonutils.base

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object LocationPermissionHelper {
    private const val PERMISSION_CODE = 0
    private const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION

    /** Check to see we have the necessary permissions for this app.  */
    fun hasPermission(activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(activity, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    /** Check to see we have the necessary permissions for this app, and ask for them if we don't.  */
    fun requestPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity, arrayOf(FINE_LOCATION), PERMISSION_CODE
        )
    }

    /** Check to see if we need to show the rationale for this permission.  */
    fun shouldShowRequestPermissionRationale(activity: Activity): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, FINE_LOCATION)
    }

    /** Launch Application Setting to grant permission.  */
    fun launchPermissionSettings(activity: Activity) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", activity.packageName, null)
        activity.startActivity(intent)
    }
}