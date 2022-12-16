package mx.arquidiocesis.eamxgeneric.model

import com.google.gson.annotations.SerializedName

data class ComunityPrincipalModel(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("status")
    val status: String?
)