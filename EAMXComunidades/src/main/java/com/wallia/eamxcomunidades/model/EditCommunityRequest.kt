package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class EditCommunityRequest(
    @SerializedName("activities")
    val activities: List<Activity>?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("charisma")
    val charisma: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("facebook")
    val facebook: String?,
    @SerializedName("instagram")
    val instagram: String?,
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("longitude")
    val longitude: Double?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("service_hours")
    val serviceHours: List<ServiceHourXXX>?,
    @SerializedName("service_hours")
    val serviceHoursExtras: List<ServiceHourXXX>?,
    @SerializedName("streaming_channel")
    val streamingChannel: String?,
    @SerializedName("twitter")
    val twitter: String?,
    @SerializedName("type_id")
    val typeId: Int?,
    @SerializedName("website")
    val website: String?
)