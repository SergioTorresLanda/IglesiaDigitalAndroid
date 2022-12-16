package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class CreateCommunityRequest(
    @SerializedName("address")
    val address: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("longitude")
    val longitude: Double?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("type_id")
    val typeId: Int?
)