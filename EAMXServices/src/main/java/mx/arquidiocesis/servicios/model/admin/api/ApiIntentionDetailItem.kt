package mx.arquidiocesis.servicios.model.admin.api

import mx.arquidiocesis.servicios.model.DedicatedTo

data class ApiIntentionDetailItem(
    val dedicated_to: List<DedicatedTo>?=null,
    val intention: String?=null,
    val mention_from: String?=null
)