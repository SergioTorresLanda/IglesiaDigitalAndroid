package mx.arquidiocesis.registrosacerdote.model.userdetailpriest

import com.google.gson.annotations.SerializedName
import mx.arquidiocesis.eamxcommonutils.common.ModuleAdminEnabled

data class DataPriest(
    @SerializedName("User")
    val UserPriest: UserPriest
)

data class UserPriest(
    val id: Int,
    val name: String,
    val first_surname: String,
    val second_surname: String?= null,
    val phone_number: String,
    val pastoral_work: String?= null,
    val location_id: Int?= null,
    val profile: String? = null,
    val image: String?= null,
    var email: String,
    var community: CommunityModel?=null,
    val life_status: LifeStatusModel?= null,
    val congregation: CongregationModel?= null,
    val location_modules: List<ModuleAdminEnabled>? = null,
    val interest_topics: List<InterestTopicModel>?= null,
    val locations: List<LocationModel>?= null,
    val services_provided: List<ServicesProvided>?= null,
    val prefixModel: PrefixModel? = null
)

data class LocationModel(
    @SerializedName("name")
    val name: String,
    @SerializedName("id")
    val id: Int
)

data class CongregationModel(
    @SerializedName("name")
    val name: String,
    @SerializedName("id")
    val id: Int
)

data class LifeStatusModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

data class InterestTopicModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

data class CommunityModel(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("status")
    val status: String?
)

data class PrefixModel(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
)

data class ServicesProvided(
    @SerializedName("location_id")
    val location_id: Int = 0,
    @SerializedName("location_name")
    val location_name: String = "",
    @SerializedName("service_id")
    val service_id: Int = 0,
    @SerializedName("service_name")
    val service_name: String = "",
    @SerializedName("service_description")
    val service_description: String? = ""
)

