package mx.arquidiocesis.eamxredsocialmodule.model

data class EAMXPublicationsAllResponse(
    val publications: List<Publication>,
    val pages: Int,
    val total: Int,
    val totalPost: Int
)