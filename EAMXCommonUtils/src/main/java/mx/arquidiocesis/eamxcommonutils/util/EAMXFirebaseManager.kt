package mx.arquidiocesis.eamxcommonutils.util

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject

class EAMXFirebaseManager(context: Context) {
    private val sharedPreferences by lazy {
        EAMXCUMySharedPreferences(context)
    }

    private val firebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(context)
    }

    init {
        firebaseAnalytics.setAnalyticsCollectionEnabled(true)
    }

    private fun addProperty(label: EAMXEnumUser) {
        firebaseAnalytics.setUserProperty(
            label.name,
            (sharedPreferences.getData(
                label.name,
                EAMXTypeObject.STRING_OBJECT
            ) as? String).toString()
        )
    }

    fun setLogEvent(event: String, params: Bundle) {
        firebaseAnalytics.setUserId(
            (sharedPreferences.getData(
                EAMXEnumUser.USER_ID.name,
                EAMXTypeObject.INT_OBJECT
            ) as? Int).toString()
        )
        firebaseAnalytics.logEvent(event, params)
    }

    fun setLogEvent(event: String) {
        firebaseAnalytics.setUserId(
            (sharedPreferences.getData(
                EAMXEnumUser.USER_ID.name,
                EAMXTypeObject.INT_OBJECT
            ) as? Int).toString()
        )
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, event)
                putString(FirebaseAnalytics.Param.SCREEN_CLASS, event)
            }
        }
    }
}