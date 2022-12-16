package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class ScheduleXX(
    @SerializedName("hour_end")
    val hourEnd: String?,
    @SerializedName("hour_start")
    val hourStart: String?
)