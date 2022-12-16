package mx.arquidiocesis.servicios.model


import com.google.gson.annotations.SerializedName

data class Intention(
    @SerializedName("action")
    val action: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
)