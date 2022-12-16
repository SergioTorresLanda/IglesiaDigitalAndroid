package mx.arquidiocesis.eamxpagos.config.services

import mx.arquidiocesis.eamxcommonutils.application.ConstansApp

object WebConfigSer {
    val HOST = ConstansApp.hostEncuentro()
    const val POST_SERVICES = "services"
    const val GET_ZIP_CODE= "catalog/neighborhoods"

    //INTENTIONS
    const val GET_INTENTIONS = "locations/{location_Id}"
    const val GET_INTENTIONS_DETAIL = "services"

    //SERVICES
    const val GET_SERVICES = "services"
    const val GET_SERVICES_DETAIL = "services/{service_id}"
    const val GET_SERVICES_CATALOG = "catalog/services"
    const val EXECUTE_CHANGE_STATUS_SERVICES = "services/{service_id}"
    const val EXECUTE_ADD_COMMENT = "services/{service_id}"
    const val EXECUTE_DELETE_SERVICES = "services/{service_id}"

    const val COMMUNITY = "users/{user_id}/locations"
}