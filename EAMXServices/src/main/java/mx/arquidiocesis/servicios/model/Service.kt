package mx.arquidiocesis.servicios.model


import com.google.gson.annotations.SerializedName

data class Service(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("type")
    val type: String?
)