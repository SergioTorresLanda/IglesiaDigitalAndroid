package com.wallia.eamxcomunidades.model

data class ServiceActivityModel(
    val name : String,
    val addressedTo : String,
    val description : String,
    val scheduleDays : String,
    val scheduleHour : String,
    val daysList : List<Int>,
    val schedule : ScheduleXXXXX? = null
)