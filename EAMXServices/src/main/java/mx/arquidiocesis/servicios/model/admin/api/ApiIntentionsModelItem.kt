package mx.arquidiocesis.servicios.model.admin.api

data class ApiIntentionsModelItem(
    val date: String,
    val end_time: Any,
    val priest: ApiPriest,
    val start_time: String
)