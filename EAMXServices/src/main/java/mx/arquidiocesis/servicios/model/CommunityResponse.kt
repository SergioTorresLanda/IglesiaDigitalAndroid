package mx.arquidiocesis.servicios.model


import com.google.gson.annotations.SerializedName

data class CommunityResponse(
    @SerializedName("address")
    val address: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("img_url")
    val imgUrl: String?,
    @SerializedName("institute_or_association")
    val instituteOrAssociation: String?,
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("longitude")
    val longitude: Double?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("phone")
    val phone: String?
)