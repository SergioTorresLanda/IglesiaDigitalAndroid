package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

data class ScheduleXXXXX(
    @SerializedName("end_hour")
    var endHour: String? = "",
    @SerializedName("start_hour")
    var startHour: String? = ""
)

fun ScheduleXXXXX.startHourMinusEndHour() : Boolean{
    startHour?.let { start ->
        val formatStart = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeStart = formatStart.parse(start)
        endHour?.let { end ->
            val formatEnd = SimpleDateFormat("HH:mm", Locale.getDefault())
            val timeEnd = formatEnd.parse(end)

            return timeStart < timeEnd
        }
    }
    return false

}