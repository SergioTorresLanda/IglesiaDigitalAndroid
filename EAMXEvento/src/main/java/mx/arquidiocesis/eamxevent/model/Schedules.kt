package mx.arquidiocesis.eamxevent.model

import com.google.gson.annotations.SerializedName

data class Schedules(
    @SerializedName("days")
    val days: List<Day>,
    @SerializedName("hour_start")
    val hour_start: String? = null,
    @SerializedName("hour_end")
    val hour_end: String? = null
)