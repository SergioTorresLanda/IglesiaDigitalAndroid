package mx.arquidiocesis.eamxcommonutils.util.repositoryrefreshtoken.model

data class UserAttributes(
    val id: Int,
    val name: String,
    val last_name: String,
    val middle_name: String?=null,
    val phone_number: String,
    val email: String,
    val role: String,
    val profile: String
)