package mx.arquidiocesis.eamxevent.retrofit

import mx.arquidiocesis.eamxcommonutils.application.ConstansApp

object WebConfig {
    val HOST : String = ConstansApp.hostEncuentro()

    //Event Diner
    const val EVENT_DINER = "act-voluntariado/comedores"
    const val EVENT_DINER_PATH = "act-voluntariado/comedores/{dinerId}"

    //Event Pantry
    const val EVENT_PANTRY = "act-voluntariado/despensas"
    const val EVENT_PANTRY_PATH = "act-voluntariado/despensas/{pantryId}"
    //Event Other
    const val EVENT_OTHER = "act-voluntariado/genericos"
    const val EVENT_OTHER_UPDATE = "act-voluntariado/genericos/{eventId}"
    const val EVENT_OTHER_GET = "act-voluntariado/genericos"
    const val EVENT_OTHER_GETDET = "act-voluntariado/genericos/{eventId}"
    const val EVENT_OTHER_ACTORS = "act-voluntariado/genericos/actores"
    const val UPDATE_OTHER_ACTORS = "act-voluntariado/genericos/actores/{actorId}"


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