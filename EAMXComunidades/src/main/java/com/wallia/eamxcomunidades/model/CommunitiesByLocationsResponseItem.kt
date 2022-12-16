package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class CommunitiesByLocationsResponseItem(
    @SerializedName("address")
    val address: String?,
    @SerializedName("distance")
    val distance: Any?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("img_url")
    val imgUrl: String?,
    @SerializedName("institute_or_association")
    val instituteOrAssociation: String?,
    @SerializedName("latitude")
    val latitude: String?,
    @SerializedName("longitude")
    val longitude: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("phone")
    val phone: String?
)