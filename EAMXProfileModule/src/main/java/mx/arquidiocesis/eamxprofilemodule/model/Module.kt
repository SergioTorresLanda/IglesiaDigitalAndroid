package mx.arquidiocesis.eamxprofilemodule.model


import com.google.gson.annotations.SerializedName

data class Module(
    @SerializedName("category")
    val category: String?,
    @SerializedName("enabled")
    val enabled: Boolean?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
)