package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("community")
    val community: Community?,
    @SerializedName("congregation")
    val congregation: Congregation?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("first_surname")
    val firstSurname: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("interest_topics")
    val interestTopics: List<Any>?,
    @SerializedName("life_status")
    val lifeStatus: LifeStatusX?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("pastoral_work")
    val pastoralWork: Any?,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("profile")
    val profile: String?,
    @SerializedName("second_surname")
    val secondSurname: String?
)