package mx.arquidiocesis.eamxevent.retrofit

import mx.arquidiocesis.eamxcommonutils.application.ConstansApp

object WebConfig {
    val HOST : String = ConstansApp.hostEncuentro()

    const val EVENT = "act-voluntariado/comedores"
    const val EVENT_PATH = "act-voluntariado/comedores/{dinerId}"
}