package mx.arquidiocesis.servicios.model


import com.google.gson.annotations.SerializedName

data class Devotee(
    @SerializedName("devotee_id")
    val devotee_id: Int
)