package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class ActivityX(
    @SerializedName("description")
    val description: String?,
    @SerializedName("geared_toward")
    val gearedToward: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("service_hours")
    val serviceHours: List<ServiceHourXXXX>?
)