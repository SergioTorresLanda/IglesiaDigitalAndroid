package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image_url")
    val imageUrl: String?,
    var arrayImage: String? = null,
    @SerializedName("name")
    val name: String?,
)