package mx.arquidiocesis.eamxredsocialmodule.model

data class ResultProfileModel(
    val id: Int,
    val name: String,
    val lastName: String,
    val secondLastName: String,
    val email: String,
    val fullName: String,
    var personId: Int,
    val phoneNumber: String,
    var role: String,
    val image: String,
    val thumbnail: String
)