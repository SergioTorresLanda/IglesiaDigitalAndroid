package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class ServiceHourXXX(
    @SerializedName("day")
    val day: Int?,
    @SerializedName("schedule")
    val schedule: List<ScheduleXXXX>?
)