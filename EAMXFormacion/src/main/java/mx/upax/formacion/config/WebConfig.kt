package mx.upax.formacion.config

import com.upax.formacion.BuildConfig
import mx.arquidiocesis.eamxcommonutils.application.ConstansApp

object WebConfig {
    val HOST = ConstansApp.hostEncuentro()
    const val FORMATION_SIMPLE = "formations"
    const val FORMATION_PATCH = "formations/{id}"
    const val MENU = "catalog/library-themes"
}