package mx.arquidiocesis.eamxprofilemodule.model

import com.google.gson.annotations.SerializedName

data class LocationCommunityModel(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("type")
    val type: String?
)