package mx.arquidiocesis.eamxredsocialmodule.model

data class MetadataModel(
    val personId:Int?,
    val username:String?,
    val role:String?,
    val type:Int?,
    val communityId: Int?,
    val churchId:Int?,
    val address: String?,
    val email: String?,
    val phoneNumber: String?
)