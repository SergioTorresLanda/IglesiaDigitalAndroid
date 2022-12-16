package mx.arquidiocesis.servicios.model.admin.api

data class ApiAddressModel(
    val description : String?,
    val zipcode : String?,
    val neighborhood : String?,
    val longitude : Double?,
    val latitude : Double?
)