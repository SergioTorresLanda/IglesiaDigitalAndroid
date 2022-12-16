package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class Devotee(
    @SerializedName("first_surname")
    val firstSurname: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("second_surname")
    val secondSurname: String?
)