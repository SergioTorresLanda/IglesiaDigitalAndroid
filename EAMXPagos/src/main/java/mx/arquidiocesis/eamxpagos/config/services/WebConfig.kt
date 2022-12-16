package mx.arquidiocesis.eamxpagos.config.services

import mx.arquidiocesis.eamxcommonutils.application.ConstansApp

object WebConfig {
    val HOST = ConstansApp.hostEncuentro()
    const val CATALOG_SERVICES = "catalog/services"
    const val POST_SERVICES = "services"
    const val GET_LIST_CHURCH_AND_MASS = "locations"
    const val GET_DETAIL_CHURCH_OR_MASS = "locations/{locationId}"
    const val GET_CHURCH_FAVORITE = "users/{userId}/locations"
    const val GET_ZIP_CODE= "catalog/neighborhoods"

}