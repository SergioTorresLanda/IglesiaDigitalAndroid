package mx.arquidiocesis.servicios.model


import com.google.gson.annotations.SerializedName

data class MentionRequest(
    @SerializedName("date")
    val date: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("devotee")
    val devotee: DevoteeX?,
    @SerializedName("hour_start")
    val hourStart: String?,
    @SerializedName("intention_type")
    val intentionType: String?,
    @SerializedName("location")
    val location: LocationXX?,
    @SerializedName("service")
    val service: Service?
)