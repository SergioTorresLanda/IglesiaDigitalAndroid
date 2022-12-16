package mx.arquidiocesis.eamxprofilemodule.model.userdetail

import com.google.gson.annotations.SerializedName
import mx.arquidiocesis.eamxcommonutils.common.ModuleAdminEnabled
import mx.arquidiocesis.eamxprofilemodule.model.ChurchAndDescriptionModel
import mx.arquidiocesis.eamxprofilemodule.model.ChurchModel
import mx.arquidiocesis.eamxprofilemodule.model.DataWithDescription
import mx.arquidiocesis.eamxprofilemodule.model.update.base.ActivityChurchModel
import java.net.URL

data class Data(
    val User: User
)

data class User(
    val id: Int,
    val name: String,
    val first_surname: String,
    val second_surname: String? = null,
    val phone_number: String,
    val pastoral_work: String? = null,
    val location_id: Int? = null,
    val profile: String? = null,
    var image: String? = null,
    var email: String,
    var community: CommunityModel? = null,
    val life_status: LifeStatusModel? = null,
    val congregation: CongregationModel? = null,
    val location_modules: List<ModuleAdminEnabled>? = null,
    val interest_topics: List<InterestTopicModel>? = null,
    val locations: List<LocationModel>? = null,
    val services_provided: List<ServicesProvided>? = null,
    val prefix: PrefixModel? = null,
    val is_provider: String? = null
)

data class LocationModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("service_url")
    val service_url: String? = null
)

fun LocationModel.toChurchModel(): ChurchModel {
    return ChurchModel(
        id = this.id,
        name = this.name,
        address = "",
        imageUrl = this.service_url,
        latitude = "0.0",
        longitude = "0.0"
    )
}

data class CongregationModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("service_url")
    val service_url: String? = null
)

fun CongregationModel.toChurchModel(): ChurchModel {
    return ChurchModel(
        id = this.id,
        name = this.name ?: "",
        address = "",
        imageUrl = this.service_url,
        latitude = "0.0",
        longitude = "0.0"
    )
}

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
    @SerializedName("description")
    val description: String?,
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
    @SerializedName("service_url")
    val service_url: String? = "",
    @SerializedName("service_description")
    val service_description: String? = ""
)

fun ServicesProvided.toChurchAndDescriptionModel(): ChurchAndDescriptionModel {
    return ChurchAndDescriptionModel(
        church = ChurchModel(
            id = this.location_id,
            name = this.location_name,
            address = "",
            imageUrl = this.service_url,
            latitude = "0.0",
            longitude = "0.0"
        ),
        activity = ActivityChurchModel(
            id = this.service_id,
            description = this.service_name,
            otherDescription = this.service_description
        )
    )
}
