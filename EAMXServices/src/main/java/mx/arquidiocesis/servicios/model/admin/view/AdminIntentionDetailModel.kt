package mx.arquidiocesis.servicios.model.admin.view

import mx.arquidiocesis.servicios.model.IntentionDetail

data class AdminIntentionDetailModel(
    val churchName : String,
    val date : String,
    val hour : String,
    val list : List<IntentionDetail>
)