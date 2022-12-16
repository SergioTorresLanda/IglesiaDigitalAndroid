package mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.model

data class UserAttributes(
    val email: String,
    val id: Int,
    val last_name: String,
    val middle_name: String,
    val name: String,
    val phone_number: String,
    val role: String
)