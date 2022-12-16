package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class ReviewsResponseItem(
    @SerializedName("devotee")
    val devotee: Devotee?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("rating")
    val rating: Double?,
    @SerializedName("review")
    val review: String?
)