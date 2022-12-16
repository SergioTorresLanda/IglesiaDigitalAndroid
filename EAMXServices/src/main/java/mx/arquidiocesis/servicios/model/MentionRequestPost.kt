package mx.arquidiocesis.servicios.model


import com.google.gson.annotations.SerializedName

data class MentionRequestPost(
    @SerializedName("dedicated_to")
    val dedicatedTo: String?,
    @SerializedName("intention_type")
    val intentionType: String?,
    @SerializedName("location_id")
    val locationId: Int?,
    @SerializedName("mass_date")
    val massDate: String?,
    @SerializedName("mass_schedule")
    val massSchedule: String?,
    @SerializedName("service_id")
    val serviceId: Int?,
    @SerializedName("mention_from")
    val from : String?
)