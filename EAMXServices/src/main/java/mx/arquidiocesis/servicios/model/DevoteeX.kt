package mx.arquidiocesis.servicios.model


import com.google.gson.annotations.SerializedName

data class DevoteeX(
    @SerializedName("devotee_id")
    val devoteeId: Int?
)