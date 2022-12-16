package mx.arquidiocesis.eamxcadenaoracionesmodule.model


import com.google.gson.annotations.SerializedName

data class EAMXPraySend(
        @SerializedName("description")
        val description: String,
        @SerializedName("fiel_id")
        val fielId: Int,
        @SerializedName("fiel_name")
        val fielName: String,
        @SerializedName("reason")
        val reason: Int
)