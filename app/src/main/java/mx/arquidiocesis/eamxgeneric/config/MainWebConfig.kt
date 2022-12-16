package mx.arquidiocesis.eamxgeneric.config

import mx.arquidiocesis.eamxcommonutils.application.ConstansApp
import mx.arquidiocesis.eamxgeneric.BuildConfig

object WebConfig {
    val HOST = ConstansApp.hostUser();
    val HOSTHOME = ConstansApp.hostEncuentro()
    const val TOKEN = "user/token"
    const val TOKENDELETE = "user/token/{token}"
    const val GET_DETAIL_USER = "user/detail/{id}"
    const val ORACIONESDETALLE = "devotions/{devotion_id}"
    const val HOMEDATA = "home"
}