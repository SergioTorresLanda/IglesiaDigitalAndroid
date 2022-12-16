package mx.arquidiocesis.eamxprofilemodule.repository

import com.google.gson.Gson
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import java.text.SimpleDateFormat
import java.util.*

class RepositoryLocalCatalog {
    private val dateFormat = "yyyy-MM-dd"

    fun isDataNeedUpdate(dateSearch : String?) : Boolean {
        if (dateSearch == null) {
            return true
        }
        val format = SimpleDateFormat(dateFormat, Locale.getDefault())
        val strDate = format.parse(dateSearch)
        val calendar = Calendar.getInstance()
        val dayCurrent = calendar.time.date
        val daySave = strDate?.date ?: 0
        return daySave - dayCurrent != 0
    }

    fun <T> getData(nameCollection : String, type : T) : T? {
        val dataLocal = eamxcu_preferences.getData(nameCollection, EAMXTypeObject.STRING_OBJECT).toString()
        return if(dataLocal.isEmpty()){
            val objectRecovery = Gson().fromJson(dataLocal, type!!::class.java)
            objectRecovery
        } else{
            null
        }
    }

    fun <T> saveData(collection: String, data: T): Boolean {
        return eamxcu_preferences.saveData(collection, Gson().toJson(data).toString())
    }
}