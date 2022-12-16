package mx.arquidiocesis.eamxredsocialmodule.model

data class UpLoadMediaModel(
    val upload_type: String,
    val resource_id: Int,
    val content: List<String>?,
)