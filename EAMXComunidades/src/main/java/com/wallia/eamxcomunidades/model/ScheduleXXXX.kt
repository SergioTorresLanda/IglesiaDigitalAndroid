package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class ScheduleXXXX(
    @SerializedName("end_hour")
    val endHour: String?,
    @SerializedName("start_hour")
    val startHour: String?
)