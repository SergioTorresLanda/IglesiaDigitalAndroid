package mx.arquidiocesis.eamxcadenaoracionesmodule.model


import com.google.gson.annotations.SerializedName

data class EAMXSendPrayStatus(
    @SerializedName("fiel_id")
    val fielId: Int,
    @SerializedName("fiel_name")
    val fielName: String,
    @SerializedName("reaction")
    val reaction: Boolean
)