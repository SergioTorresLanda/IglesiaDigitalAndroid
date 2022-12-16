package mx.arquidiocesis.servicios.model

import com.google.gson.annotations.SerializedName

data class LocationXXX(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("name")
    val name: String?,
)
