package mx.arquidiocesis.eamxcommonutils.util

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject

class EAMXCUMySharedPreferences(context: Context) {
    companion object{
        @JvmStatic
        var singleton: EAMXCUMySharedPreferences? = null
    }
    private val oldPref = context.getSharedPreferences("mis_prefrerencias", Context.MODE_PRIVATE)

    private val shared = EncryptedSharedPreferences.create(
        "data_user_shared",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    init {
        singleton = this
        shared.edit {
            oldPref.all.forEach {
                when (it.value) {
                    is String -> putString(it.key, it.value as? String)
                    is Int -> putInt(it.key, (it.value as? Int) ?: 0)
                    is Long -> putLong(it.key, (it.value as? Long) ?: 0)
                    is Boolean -> putBoolean(it.key, (it.value as? Boolean) ?: false)
                    is Float -> putFloat(it.key, (it.value as? Float) ?: 0.0f)
                    is Set<*> -> putStringSet(it.key, (it.value as? Set<String>) ?: setOf<String>())
                }
            }
        }
        oldPref.edit { clear() }
    }

    fun saveData(key: String, value: Any): Boolean {
        return when (value) {
            is String -> {
                shared.edit().putString(key, value).apply();return true
            }
            is Int -> {
                shared.edit().putInt(key, value).apply(); return true
            }
            is Boolean -> {
                shared.edit().putBoolean(key, value).apply(); return true
            }
            is Float -> {
                shared.edit().putFloat(key, value).apply(); return true
            }
            is Long -> {
                shared.edit().putLong(key, value).apply(); return true
            }
            else -> false
        }
    }

    fun getData(key: String, type:EAMXTypeObject): Any {
        return when (type) {
            EAMXTypeObject.STRING_OBJECT -> {
                shared.getString(key, "").toString()
            }
            EAMXTypeObject.INT_OBJECT -> {
                shared.getInt(key, 0)
            }
            EAMXTypeObject.BOOLEAN_OBJECT -> {
                shared.getBoolean(key, false)
            }
            EAMXTypeObject.FLOAT_OBJECT -> {
                shared.getFloat(key, 0f)
            }
            EAMXTypeObject.LONG_OBJECT -> {
                shared.getLong(key, 0)
            }
            else -> false
        }
    }

    fun removeFile() {
        oldPref.edit().clear().apply()
        shared.edit().clear().apply()
    }

    var email: String?
        get() = shared.getString(EAMXEnumUser.USER_EMAIL.name, "")
        set(value) = shared.edit().putString(EAMXEnumUser.USER_EMAIL.name, value).apply()

    var pas4d: String?
        get() = shared.getString("password", null)
        set(value) = shared.edit().putString("password", value).apply()

    var USER_COMMUNITY_ID: Int
        get() = shared.getInt(EAMXEnumUser.USER_COMMUNITY_ID.name, 0)
        set(value) = shared.edit().putInt(EAMXEnumUser.USER_COMMUNITY_ID.name, value).apply()

    var TOKEN_CUSTOMER: String
        get() = shared.getString(EAMXEnumUser.TOKEN_CUSTOMER.name, "") ?: ""
        set(value) = shared.edit().putString(EAMXEnumUser.TOKEN_CUSTOMER.name, value).apply()

    var token: String
        get() = shared.getString("Token_bearer", "") ?: ""
        set(value) = shared.edit().putString("Token_bearer", value).apply()

    var TOKEN_REFRESH_CUSTOMER: String
        get() = shared.getString(EAMXEnumUser.TOKEN_REFRESH_CUSTOMER.name, "") ?: ""
        set(value) = shared.edit().putString(EAMXEnumUser.TOKEN_REFRESH_CUSTOMER.name, value)
            .apply()

}