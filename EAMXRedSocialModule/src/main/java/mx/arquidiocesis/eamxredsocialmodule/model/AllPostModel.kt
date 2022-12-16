package mx.arquidiocesis.eamxredsocialmodule.model

data class AllPostModel(
    val message: String,
    val requestId: String,
    val result: ResultModel?
)