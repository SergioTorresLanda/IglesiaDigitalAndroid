package mx.arquidiocesis.eamxredsocialmodule.model

data class ResultCommentModel(
    val Pagination: PaginationModel?,
    val Comments: List<CommentModel>
)