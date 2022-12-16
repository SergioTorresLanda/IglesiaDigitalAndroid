package mx.arquidiocesis.registrosacerdote.config

import mx.arquidiocesis.eamxcommonutils.application.ConstansApp
import mx.arquidiocesis.registrosacerdote.BuildConfig


object WebConfig {
    val HOST_CATALOG = "${ConstansApp.hostEncuentro()}catalog/"
    val HOST_USER : String = ConstansApp.hostUser();

    const val GET_CATALOG_ACTIVITIES = "activities"
    const val GET_CATALOG_CONGREGATION = "congregations"
    const val POST_UPDATE_USER = "user/update"

    const val GET_DETAIL_USER = "user/detail/{id}"
}