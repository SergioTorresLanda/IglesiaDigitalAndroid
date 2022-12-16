package mx.arquidiocesis.eamxgeneric.model


import com.google.gson.annotations.SerializedName

data class ServicesProvided(
    @SerializedName("location_id")
    val locationId: Int?,
    @SerializedName("location_name")
    val locationName: String?,
    @SerializedName("service_description")
    val serviceDescription: Any?,
    @SerializedName("service_id")
    val serviceId: Int?,
    @SerializedName("service_name")
    val serviceName: String?
)