package mx.arquidiocesis.eamxredsocialmodule.model

data class ResultFollowModel(
    val Pagination: PaginationModel?,
    val Follows: List<FollowModel>
)