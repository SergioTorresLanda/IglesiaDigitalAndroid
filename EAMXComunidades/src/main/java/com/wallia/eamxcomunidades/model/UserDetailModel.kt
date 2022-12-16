package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class UserDetailModel(
    @SerializedName("data")
    val `data`: DataX?
)