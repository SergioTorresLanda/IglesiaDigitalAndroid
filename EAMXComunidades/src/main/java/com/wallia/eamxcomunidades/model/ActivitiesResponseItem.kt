package com.wallia.eamxcomunidades.model

import com.google.gson.annotations.SerializedName
import mx.arquidiocesis.eamxcommonutils.util.numberToDayNameCompleted

data class ActivitiesResponseItem(
    @SerializedName("description")
    val description: String?,
    @SerializedName("geared_toward")
    val gearedToward: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("service_hours")
    val serviceHours: List<ServiceHourX>?
)

fun ActivitiesResponseItem.getHours(): String {
    val firstItem = serviceHours?.firstOrNull()
    val hour = firstItem?.schedules?.firstOrNull()

    return hour?.let { "${hour.hourStart} - ${hour.hourEnd}" } ?: ""
}

fun ActivitiesResponseItem.getDays(): String {
    var seguidos = false
    var dia1 = 0
    var dias = ""
    serviceHours?.let { s ->
        s.forEach { ser ->
            ser.day?.let { d ->
                if (dia1 == 0) {
                    dia1 = d
                    dias = d.numberToDayNameCompleted()
                } else {
                    dias = "$dias - ${d.numberToDayNameCompleted()}"
                    if (dia1 == d - 1) {
                        dia1 = d
                        seguidos = true
                    } else {
                        seguidos = false
                    }
                }

            }
        }
        return if (seguidos) {
            val firstItem = serviceHours?.firstOrNull()
            val lastItem = serviceHours?.lastOrNull()
            "${firstItem?.day?.numberToDayNameCompleted() ?: ""} - ${lastItem?.day?.numberToDayNameCompleted() ?: ""}"
        } else {
            dias
        }
    }?: run {
        return ""
    }
}

fun ActivitiesResponseItem.getDaysInt(): List<Int> {
    return serviceHours?.let {
        it.map { item -> item?.let { itemDay -> itemDay.day } ?: 0 }.toList()
    } ?: listOf()
}

fun ActivitiesResponseItem.getScheduler(): ScheduleXXXXX {
    val firstItem = serviceHours?.firstOrNull()
    val hour = firstItem?.schedules?.firstOrNull()
    return hour?.let { ScheduleXXXXX(startHour = it.hourStart, endHour = it.hourEnd) }
        ?: ScheduleXXXXX()
}

