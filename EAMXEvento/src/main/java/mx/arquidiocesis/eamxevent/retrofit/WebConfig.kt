package mx.arquidiocesis.eamxevent.retrofit

import mx.arquidiocesis.eamxcommonutils.application.ConstansApp

object WebConfig {
    val HOST : String = ConstansApp.hostEncuentro()

    //Event Diner
    const val EVENT_DINER = "act-voluntariado/comedores"
    const val EVENT_DINER_PATH = "act-voluntariado/comedores/{dinerId}"

    //Event Pantry
    const val EVENT_PANTRY = "act-voluntariado/despensas"
    const val EVENT_PANTRY_PATH = "act-voluntariado/comedores/{pantryId}"

    //Donors
    const val EVENT_DONOR = "act-voluntariado/donadores"
    const val EVENT_DONOR_PATH = "act-voluntariado/donadores/{donadorId}"
    const val EVENT_DONOR_HISTORY_PATH = "act-voluntariado/donadores/history/{userId}"

    //Volunteers
    const val EVENT_VOLUNTEER = "act-voluntariado/voluntario"
    const val EVENT_VOLUNTEER_PATH = "act-voluntariado/voluntario/{voluntarioId}"

    //Obtener donadores y voluntarios por comedor
    const val GET_TYPE_PARTICIPATION_BY_DINER = "act-voluntariado/comedores/{dinerId}"


}