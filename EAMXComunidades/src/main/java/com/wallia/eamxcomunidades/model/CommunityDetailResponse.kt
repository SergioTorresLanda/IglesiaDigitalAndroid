package com.wallia.eamxcomunidades.model

import android.view.View
import com.google.gson.annotations.SerializedName
import mx.arquidiocesis.eamxcommonutils.util.numberToDayFirstLetter
import mx.arquidiocesis.eamxcommonutils.util.numberToDayNameCompleted

class CommunityDetailResponse(
    @SerializedName("address")
    var address: String?,
    @SerializedName("charisma")
    var charisma: String?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("email")
    var email: String?,
    @SerializedName("facebook")
    val facebook: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("instagram")
    val instagram: String?,
    @SerializedName("institute_or_association")
    val instituteOrAssociation: String?,
    @SerializedName("latitude")
    var latitude: Double?,
    @SerializedName("leader")
    var leader: String?,
    @SerializedName("longitude")
    var longitude: Double?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("phone")
    var phone: String?,
    @SerializedName("reviewing")
    val reviewing: Boolean?,
    @SerializedName("service_hours")
    var serviceHoursGeneral: List<ServiceHour>?,
    var serviceHoursFirst: List<ServiceHour>?,
    var serviceHoursSecond: List<ServiceHour>?,
    @SerializedName("streaming_channel")
    val streamingChannel: String?,
    @SerializedName("twitter")
    val twitter: String?,
    @SerializedName("type")
    val type: Type?,
    @SerializedName("website")
    val website: String?,
    @SerializedName("rating")
    val rating: String?,
) {
    private var datesFirst: MutableList<ServiceHour> = mutableListOf()
    private var datesSecond: MutableList<ServiceHour> = mutableListOf()

    private fun initData() {
        val groupByDays = this
            .serviceHoursGeneral
            ?.groupBy { item -> item.day }
            ?.toList()

        var hourStart: String? = null
        var hourEnd: String? = null

        if (groupByDays?.isNotEmpty() == true) {
            hourStart = groupByDays.first().second.first().schedules?.first()?.hourStart
            hourEnd = groupByDays.first().second.first().schedules?.first()?.hourEnd
        }

        val datesFirstInter: MutableList<ServiceHour> = mutableListOf()
        val datesSecondInter: MutableList<ServiceHour> = mutableListOf()

        groupByDays?.forEach { listServices ->
            listServices.second.forEach { serviceHour ->
                val existData =
                    serviceHour.schedules?.firstOrNull { itemSchedule -> itemSchedule.hourStart == hourStart && itemSchedule.hourEnd == hourEnd }
                existData?.let {
                    datesFirstInter.add(serviceHour)
                } ?: kotlin.run {
                    datesSecondInter.add(serviceHour)
                }
            }
        }

        datesFirst = datesFirstInter
        datesSecond = datesSecondInter
    }

    val schedulesLabel: String
        get() = "Días: $days  Horario: $hours"

    val schedulesLabelTwo: String
        get() = "Días: $daysTwo  Horario: $hoursTwo"

    val visibleScheduleTwo: Int
        get() = if (hoursTwo.length < 13) View.GONE else View.VISIBLE

    val hours: String
        get() {

            /*
            val itemSchedule = serviceHours?.firstOrNull()

            return itemSchedule?.let { item ->
                val schedule = item.schedules?.firstOrNull()
                schedule?.let {
                    "${it.hourStart} - ${it.hourEnd}"
                }
            } ?: "-"*/
            initData()
            return "${datesFirst.firstOrNull()?.schedules?.first()?.hourStart ?: ""} - ${datesFirst.firstOrNull()?.schedules?.first()?.hourEnd ?: ""}"
        }

    val days: String
        get() {
            serviceHoursGeneral?.let {
                if (it.isEmpty()) {
                    return "-"
                }
            }

            initData()

            val dayMin = datesFirst.let {
                if (it.isNotEmpty()) {
                    it.minOf { item -> item.day ?: 0 }
                } else
                    0
            }
            val dayMax = datesFirst.let {
                if (it.isNotEmpty()) {
                    it.maxOf { item -> item.day ?: 0 }
                } else
                    0
            }

            return if (dayMin != null && dayMax != null) {
                if (dayMin != dayMax) {
                    "${dayMin.numberToDayNameCompleted()} a ${dayMax.numberToDayNameCompleted()}"
                } else {
                    dayMin.numberToDayNameCompleted()
                }
            } else {
                ""
            }
        }

    val hoursTwo: String
        get() {
            /*val itemSchedule = serviceHours?.firstOrNull()

            return itemSchedule?.let { item ->
                val schedule = item.schedules?.firstOrNull()
                schedule?.let {
                    "${it.hourStart} - ${it.hourEnd}"
                }
            } ?: "-"*/
            initData()
            return "${datesSecond.firstOrNull()?.schedules?.first()?.hourStart ?: ""} - ${datesSecond.firstOrNull()?.schedules?.first()?.hourEnd ?: ""}"
        }

    val daysTwo: String
        get() {
            serviceHoursGeneral?.let {
                if (it.isEmpty()) {
                    return "-"
                }
            }

            initData()
            //return "${1.first().schedules?.first()?.hourStart ?: ""} - ${datesSecond.first().schedules?.first()?.hourEnd ?: ""}"

            val dayMin = datesSecond.let {
                if (it.isNotEmpty()) {
                    it.minOf { item -> item.day ?: 0 }
                } else
                    0
            }
            val dayMax = datesSecond.let {
                if (it.isNotEmpty()) {
                    it.maxOf { item -> item.day ?: 0 }
                } else
                    0
            }

            return if (dayMin != null && dayMax != null) {
                if (dayMin != dayMax) {
                    "${dayMin.numberToDayNameCompleted()} a ${dayMax.numberToDayNameCompleted()}"
                } else {
                    dayMin.numberToDayNameCompleted()
                }
            } else {
                ""
            }
        }

    val phoneAndEmail: String
        get() = "E-mail: ${this.email ?: "-"}\nTeléfono: ${this.phone ?: "-"}"
}

