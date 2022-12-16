package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class ServiceHourX(
    @SerializedName("day")
    val day: Int?,
    @SerializedName("schedules")
    val schedules: List<ScheduleXX>?
)