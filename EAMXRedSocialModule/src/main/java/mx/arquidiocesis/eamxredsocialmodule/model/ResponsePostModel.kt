package mx.arquidiocesis.eamxredsocialmodule.model


data class ResponsePostModel(
    val message: String,
    val requestId: String,
    val result: PostIdModel
)