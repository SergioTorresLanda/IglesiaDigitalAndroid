package mx.arquidiocesis.eamxgeneric.config

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object FirebaseRemoteConfig {
    fun validateForceVersion(context: AppCompatActivity){
        val remoteConfig = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        context.lifecycleScope.launch(Dispatchers.IO){
            val json = remoteConfig.getBoolean("force_update")
            val djson = remoteConfig.getString("version")
            val dsdjson = remoteConfig.getBoolean("version")

        }
    }
}