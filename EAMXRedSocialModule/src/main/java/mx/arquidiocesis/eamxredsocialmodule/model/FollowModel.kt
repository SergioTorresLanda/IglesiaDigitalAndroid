package mx.arquidiocesis.eamxredsocialmodule.model

data class FollowModel(
    val id: Int,
    val name: String,
    val image:String?,
    val type: String,
    val relationshipId: Int,
    val relationshipStatus: Int?
)