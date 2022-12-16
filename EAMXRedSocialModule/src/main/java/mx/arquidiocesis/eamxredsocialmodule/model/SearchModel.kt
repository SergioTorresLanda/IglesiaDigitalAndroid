package mx.arquidiocesis.eamxredsocialmodule.model

data class SearchModel(
    val id: Int,
    val name: String?,
    val image:String?,
    val type:String?,
    val relationship: RelationshipModel?,
    val metadata: MetadataModel?

)