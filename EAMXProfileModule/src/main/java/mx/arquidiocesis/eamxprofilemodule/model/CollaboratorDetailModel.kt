package mx.arquidiocesis.eamxprofilemodule.model


import com.google.gson.annotations.SerializedName

data class CollaboratorDetailModel(
    @SerializedName("email")
    val email: String?,
    @SerializedName("first_surname")
    val firstSurname: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("life_status")
    val lifeStatus: String?,
    @SerializedName("location")
    val location: LocationCommunityModel? = null,
    @SerializedName("modules")
    val modules: List<Module>?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("second_surname")
    val secondSurname: String?,
    @SerializedName("services")
    val serviceModels: List<ServiceModel>?
)