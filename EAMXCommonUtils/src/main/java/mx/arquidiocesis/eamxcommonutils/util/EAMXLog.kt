package mx.arquidiocesis.eamxcommonutils.util

import android.util.Log
import mx.arquidiocesis.eamxcommonutils.BuildConfig

fun Any.eamxLog(message: String,seccondMessage: String = ""){
//    if (BuildConfig.FLAVOR != "pro")
        Log.d("EAMX","${this.javaClass.simpleName} $message $seccondMessage")
}