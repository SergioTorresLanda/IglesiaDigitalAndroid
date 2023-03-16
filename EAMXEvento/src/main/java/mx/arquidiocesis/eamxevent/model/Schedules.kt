package mx.arquidiocesis.eamxevent.model

import com.google.gson.annotations.SerializedName
import mx.arquidiocesis.eamxevent.model.Day
data class Schedules(
    @SerializedName("days")
    val days: MutableList<Day>,
    @SerializedName("hour_start")
    val hour_start: String? = null,
    @SerializedName("hour_end")
    val hour_end: String? = null
)