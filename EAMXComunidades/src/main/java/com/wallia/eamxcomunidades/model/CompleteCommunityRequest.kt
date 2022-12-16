package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class CompleteCommunityRequest(
    @SerializedName("type_id")
    val typeId: Int,
    @SerializedName("activities")
    val activities: List<ActivityX>?,
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
    val serviceHours: List<ServiceHourXXXXX>?,
    @SerializedName("streaming_channel")
    val streamingChannel: String?,
    @SerializedName("twitter")
    val twitter: String?,
    @SerializedName("website")
    val website: String?
)