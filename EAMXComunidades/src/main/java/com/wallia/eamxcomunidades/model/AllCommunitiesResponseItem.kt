package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class AllCommunitiesResponseItem(
    @SerializedName("address")
    val address: String?,
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