package mx.arquidiocesis.misiglesias.config

import mx.arquidiocesis.eamxcommonutils.application.ConstansApp
import mx.arquidiocesis.misiglesias.BuildConfig

object WebConfig {
    val HOST_NEW = ConstansApp.hostEncuentro()
    const val IGLESIASLISTADO = "locations"
    const val IGLESIASDETALLE = "locations/{location_id}"
    const val IGLESIABUSQUEDA = "locations"
    const val IGLESIASFAVORITAS = "users/{user_id}/locations"
    const val IGLESIASFAVDEL = "users/{user_id}/locations/{location_id}"
    const val MISASLISTADO = "masses"
    const val COMENTARIOS = "locations/{location_id}/reviews"
    const val CATALOGOSEVICIOS = "catalog/services"
    const val PRIESTSBUSQUEDA = "users"
    const val SUGERENCIAS = "suggestions"


    const val HOST_OLD = "https://rc1hfd6cjd.execute-api.us-east-1.amazonaws.com/dev/v3/"
    const val CATALOGOMISAS = "catalogs/masses"
}