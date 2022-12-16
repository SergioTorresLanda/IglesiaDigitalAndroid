package mx.arquidiocesis.eamxredsocialmodule.model

data class ResponseSearchModel(
    val message: String,
    val requestId: String,
    val result: ResultSearchModel?
)