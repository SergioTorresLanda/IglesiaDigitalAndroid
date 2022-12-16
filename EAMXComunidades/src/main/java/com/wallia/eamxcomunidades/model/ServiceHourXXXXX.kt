package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class ServiceHourXXXXX(
    @SerializedName("day")
    val day: Int?,
    @SerializedName("schedule")
    val schedule: List<ScheduleXXXXXX>?
)