package mx.arquidiocesis.eamxpagos.fragment.ui

import DonacionesViewModel
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.widget.Toast
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import org.json.JSONObject


class WebAppInterface internal constructor(
    c: Context,
    v: DonacionesViewModel,
    val firebaseAnalytics: EAMXFirebaseManager? = null
) {
    private var mContext: Context = c
    private var viewmodel: DonacionesViewModel = v

    // Show a toast from the web page
    @JavascriptInterface
    fun showToast(s: String?) {
        /*firebaseAnalytics?.setLogEvent("ui_interaction", Bundle().apply {
            putString("flow", "view_form_card")
            putString("subflow", "pay_complete")
            putString("section", "donation")
            putString("screen_class", "form_card")
            putString("element", s)
        })*/
        s?.let { t ->
            val json = JSONObject(t)
            when (json.get("status")) {
                "success" -> {
                    viewmodel.succesResponse.postValue("¡Muchas gracias! Tu donación ha sido procesada exitosamente.")
                }
                "error" -> {
                    viewmodel.errorResponse.postValue(json.get("description").toString())
                }
            }

        }

    }

    @get:JavascriptInterface
    val androidVersion: Int
        get() = Build.VERSION.SDK_INT

    @JavascriptInterface
    fun showAndroidVersion(versionName: String?) {
        Toast.makeText(mContext, versionName, Toast.LENGTH_SHORT).show()
    }

}