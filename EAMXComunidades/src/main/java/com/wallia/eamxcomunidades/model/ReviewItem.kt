package com.wallia.eamxcomunidades.model

import com.google.gson.annotations.SerializedName

data class ReviewItem(

    @SerializedName("id")
    val id: Int?,
    @SerializedName("rating")
    val rating: String?,

)