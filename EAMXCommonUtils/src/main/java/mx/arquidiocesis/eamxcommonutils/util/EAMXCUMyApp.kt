package mx.arquidiocesis.eamxcommonutils.util

import android.app.Application

val eamxcu_preferences: EAMXCUMySharedPreferences by lazy { EAMXMyApp.prefer!!}
class EAMXMyApp: Application() {

    companion object {
        var prefer: EAMXCUMySharedPreferences? = null
    }

    override fun onCreate() {
        super.onCreate()
        prefer = EAMXCUMySharedPreferences(applicationContext)
    }
}