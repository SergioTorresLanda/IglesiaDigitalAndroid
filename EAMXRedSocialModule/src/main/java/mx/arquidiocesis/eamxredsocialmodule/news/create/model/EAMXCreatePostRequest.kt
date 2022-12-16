package mx.arquidiocesis.eamxredsocialmodule.news.create.model

import com.google.gson.annotations.SerializedName

data class EAMXCreatePostRequest(

    val asParam: String?,

    @SerializedName("organization_id")
    val organizationId: Int?,

    val content: String?,

    val feeling: String?,

    val multimedia: List<EAMXMultimedia>?,

    val location: String?,

    val status: String?,

    val FIIDEMPLEADO: Int?,

    @SerializedName("as")
    val `as`: String?,
)