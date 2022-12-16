package mx.arquidiocesis.eamxgeneric.model


import com.google.gson.annotations.SerializedName

data class LocationModuleX(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("modules")
    val modules: List<String>?,
    @SerializedName("name")
    val name: String?
)