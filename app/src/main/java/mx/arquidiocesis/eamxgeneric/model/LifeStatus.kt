package mx.arquidiocesis.eamxgeneric.model


import com.google.gson.annotations.SerializedName

data class LifeStatus(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
)