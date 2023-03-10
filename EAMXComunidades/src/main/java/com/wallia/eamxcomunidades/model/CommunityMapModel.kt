package com.wallia.eamxcomunidades.model

import com.google.gson.annotations.SerializedName

data class CommunityMapModel (
    val id: Int?,
    @SerializedName("img_url")
    val imgUrl: String?,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("address")
    val address: String?,
)