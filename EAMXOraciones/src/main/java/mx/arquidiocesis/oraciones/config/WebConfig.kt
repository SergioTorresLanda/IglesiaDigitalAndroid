package mx.arquidiocesis.oraciones.config

import com.arquidiocesis.oraciones.BuildConfig
import mx.arquidiocesis.eamxcommonutils.application.ConstansApp

object WebConfig {
    val HOST = ConstansApp.hostEncuentro()
    const val ORACIONESLISTADO = "devotions"
    const val ORACIONESDETALLE = "devotions/{devotion_id}"
    const val ORACIONESTIPO = "devotions"
}