package mx.arquidiocesis.eamxevent.model

import com.google.gson.annotations.SerializedName
import mx.arquidiocesis.eamxevent.model.Day
data class Schedules(
    @SerializedName("days")
    val days: MutableList<Day>? = null,
    @SerializedName("hour_start")
    val hour_start: String? = null,
    @SerializedName("hour_end")
    val hour_end: String? = null
)