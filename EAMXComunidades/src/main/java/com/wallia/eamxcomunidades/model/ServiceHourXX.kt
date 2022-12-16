package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class ServiceHourXX(
    @SerializedName("day")
    val day: Int?,
    @SerializedName("schedule")
    val schedule: List<ScheduleXXX>?
)