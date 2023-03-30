package mx.arquidiocesis.eamxevent.model


import com.google.gson.annotations.SerializedName

data class Type_Participation(
    @SerializedName("participantes")
    val typeParticipation: String
)