package mx.arquidiocesis.eamxredsocialmodule.model

data class ResultSearchModel(
    val pagination: PaginationModel?,
    val results: List<SearchModel>
)