package mx.arquidiocesis.eamxredsocialmodule.model

data class ResponseCommentModel(
    val message: String,
    val requestId: String,
    val result: ResultCommentModel?
)