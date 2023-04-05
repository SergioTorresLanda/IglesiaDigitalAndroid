package mx.arquidiocesis.eamxevent.model

import com.google.gson.annotations.SerializedName

data class Process(
    @SerializedName("fecha_inicio")
    val date_start: String? = null,
    @SerializedName("fecha_final")
    val date_end: String? = null,
    @SerializedName("hora_inicio")
    val hour_start: String? = null,
    @SerializedName("hora_final")
    val hour_end: String? = null
)