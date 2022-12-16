package mx.arquidiocesis.servicios.model

import com.google.gson.annotations.SerializedName

data class MainCommunityResponse (
    @SerializedName("assigned")
    val assigned: Location?,
    @SerializedName("locations")
    val locations: List<LocationXXX>?
)