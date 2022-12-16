package mx.arquidiocesis.eamxgeneric.model


import com.google.gson.annotations.SerializedName

data class LifeStatusX(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
)