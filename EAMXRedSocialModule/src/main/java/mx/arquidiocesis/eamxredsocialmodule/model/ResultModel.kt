package mx.arquidiocesis.eamxredsocialmodule.model

data class ResultModel(
    val pagination: PaginationModel?,
    val posts: List<PostModel>
)