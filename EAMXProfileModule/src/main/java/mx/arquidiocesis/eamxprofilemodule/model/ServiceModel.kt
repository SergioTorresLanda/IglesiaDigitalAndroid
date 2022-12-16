package mx.arquidiocesis.eamxprofilemodule.model


import com.google.gson.annotations.SerializedName

data class ServiceModel(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
)