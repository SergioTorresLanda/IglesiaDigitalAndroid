package mx.arquidiocesis.eamxevent.retrofit

import mx.arquidiocesis.eamxcommonutils.application.ConstansApp

object WebConfig {
    val HOST : String = ConstansApp.hostEncuentro()

    const val EVENT_DINER = "act-voluntariado/comedores"
    const val EVENT_DINER_PATH = "act-voluntariado/comedores/{dinerId}"

    const val EVENT_DONOR = "act-voluntariado/donadores"
    const val EVENT_DONOR_PATH = "act-voluntariado/donadores/{donadorId}"
    const val EVENT_DONOR_HISTORY_PATH = "act-voluntariado/donadores/history/{userId}"

    const val EVENT_VOLUNTEER = "act-voluntariado/voluntario"
    const val EVENT_VOLUNTEER_PATH = "act-voluntariado/voluntario/{voluntarioId}"

}