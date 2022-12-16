package mx.arquidiocesis.eamxprofilemodule.model


import com.google.gson.annotations.SerializedName

data class ModuleModel(
    @SerializedName("category")
    val category: String?,
    @SerializedName("enable")
    val enable: Boolean?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
)