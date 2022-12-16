package mx.arquidiocesis.eamxgeneric.config

import kotlinx.coroutines.CompletableDeferred
import mx.arquidiocesis.eamxgeneric.BuildConfig
import org.json.JSONObject


object RemoteConfigFirebase {


    suspend fun validateForceVersion(): Boolean {

        val remoteConfig =
            com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance().apply {
                setConfigSettingsAsync(
                    com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings.Builder()
                        .setMinimumFetchIntervalInSeconds(0)
                        .build()
                )
            }

        val completableDeferred = CompletableDeferred<Boolean>()
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                try {
                    val json = JSONObject(remoteConfig.getString("update_app_android"))
                    val version = json.getInt("version")
                    val forceVersion = json.getBoolean("force")
                    completableDeferred.complete(task.isSuccessful && version > BuildConfig.VERSION_CODE && forceVersion)
                } catch (e: Exception) {
                    e.printStackTrace()
                    completableDeferred.complete(false)
                }

            }
            .addOnFailureListener {
                completableDeferred.complete(false)
            }
        return completableDeferred.await()
    }
}