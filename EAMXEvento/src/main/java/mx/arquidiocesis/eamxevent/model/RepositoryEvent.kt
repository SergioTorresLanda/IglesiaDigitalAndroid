package mx.arquidiocesis.eamxevent.model

import android.R
import android.content.Context
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.arquidiocesis.eamxcommonutils.retrofit.interceptor.HeaderInterceptor
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxcommonutils.retrofit.model.header.RequestHeader
import mx.arquidiocesis.eamxevent.model.catalog.Zone

class RepositoryEvent(val context: Context) : ManagerCall() {

    val zonaResponse = MutableLiveData<Zone>()


    suspend fun getZone() {

    }

}