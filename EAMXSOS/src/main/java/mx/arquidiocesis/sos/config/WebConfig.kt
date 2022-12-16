package mx.arquidiocesis.sos.config

import com.upax.eamxsos.BuildConfig
import mx.arquidiocesis.eamxcommonutils.application.ConstansApp

object WebConfig {
    val HOST : String = ConstansApp.hostUser();
    const val SOSHABILITAR= "user/{user_id}/sos"

    val HOSTAPI = ConstansApp.hostEncuentro()
    const val SERVICESLIST = "services"
    const val SERVICESSOS = "sos-services"
    const val SERVICESGET= "sos-services/{service_id}"

    const val LOCATIONS ="locations"
    const val SERVICESLISTSOS = "catalog/services"
    const val PRIESTLIST= "users"

}