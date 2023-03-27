package mx.arquidiocesis.eamxevent.retrofit

import mx.arquidiocesis.eamxcommonutils.application.ConstansApp

object WebConfig {
    val HOST : String = ConstansApp.hostEncuentro()

    //Event
    const val EVENT_DINER = "act-voluntariado/comedores"
    const val EVENT_DINER_PATH = "act-voluntariado/comedores/{dinerId}"

    //Donors
    const val EVENT_DONOR = "act-voluntariado/donadores"
    const val EVENT_DONOR_PATH = "act-voluntariado/donadores/{donadorId}"
    const val EVENT_DONOR_HISTORY_PATH = "act-voluntariado/donadores/history/{userId}"

    //Volunteers
    const val EVENT_VOLUNTEER = "act-voluntariado/voluntario"
    const val EVENT_VOLUNTEER_PATH = "act-voluntariado/voluntario/{voluntarioId}"

    //Obtener donadores por comedor
    const val GET_DONOR_BY_DINER = "act-voluntariado/comedores/:comedor_id?participantes=DONADORES"

    //Obtener voluntarios por comedor
    const val GET_VOLUNTEER_BY_DINER = "act-voluntariado/comedores/:comedor_id?participantes=VOLUNTARIOS"

}