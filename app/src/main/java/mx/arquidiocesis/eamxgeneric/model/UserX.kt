package mx.arquidiocesis.eamxgeneric.model


import com.google.gson.annotations.SerializedName

data class UserX(
    @SerializedName("email")
    val email: String?,
    @SerializedName("first_surname")
    val firstSurname: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("interest_topics")
    val interestTopics: List<InterestTopicX>?,
    @SerializedName("life_status")
    val lifeStatus: LifeStatusX?,
    @SerializedName("location_id")
    val locationId: Any?,
    @SerializedName("location_modules")
    val locationModules: List<LocationModuleX>?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("profile")
    val profile: String?,
    @SerializedName("community")
    val community: ComunityPrincipalModel?,
    @SerializedName("second_surname")
    val secondSurname: Any?,
    @SerializedName("services_provided")
    val servicesProvided: List<ServicesProvided>?
)