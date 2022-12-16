package mx.arquidiocesis.servicios.model.admin.view

data class AdminServiceModel(
    val id : Int,
    val service : String,
    val applicant : String,
    val name : String,
    val stateTitle : String,
    val state : String,
    val date : String? = ""
)