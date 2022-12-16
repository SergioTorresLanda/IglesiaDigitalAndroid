package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class ServiceHour(
    @SerializedName("day")
    val day: Int?,
    @SerializedName("schedules")
    val schedules: List<ScheduleX>?
)