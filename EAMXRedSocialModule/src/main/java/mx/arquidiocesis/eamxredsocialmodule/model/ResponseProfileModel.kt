package mx.arquidiocesis.eamxredsocialmodule.model

data class ResponseProfileModel(
    val message: String,
    val requestId: String,
    val result: ResultProfileModel
)