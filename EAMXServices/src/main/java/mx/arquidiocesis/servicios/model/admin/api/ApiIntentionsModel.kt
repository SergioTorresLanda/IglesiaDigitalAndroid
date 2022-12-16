package mx.arquidiocesis.servicios.model.admin.api

import mx.arquidiocesis.servicios.model.admin.view.AdminIntentionGeneralModel

class IntentionsApiModel : ArrayList<ApiIntentionsModelItem>()

fun IntentionsApiModel.mapToAdminIntentionGeneralModel() : List<AdminIntentionGeneralModel>{
    return this.map { item ->
        AdminIntentionGeneralModel(
            hour = item.start_time,
            date = item.date,
            nameIntention = "",//No viene
            speaker = "${item.priest.name} ${item.priest.first_surname}"
        )
    }
}