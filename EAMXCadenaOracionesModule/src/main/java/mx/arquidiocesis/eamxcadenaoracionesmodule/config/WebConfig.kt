package mx.arquidiocesis.eamxcadenaoracionesmodule.config

import mx.arquidiocesis.eamxcadenaoracionesmodule.BuildConfig
import mx.arquidiocesis.eamxcommonutils.application.ConstansApp

object WebConfig {
    val HOST = ConstansApp.hostEncuentro()
    const val PRAYERS = "prayers"
    const val PRAYERREACTION = "prayers/{id}/reaction"
}