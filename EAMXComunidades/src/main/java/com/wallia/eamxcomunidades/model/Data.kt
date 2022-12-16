package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("description")
    val description: String?,
    @SerializedName("id")
    val id: Int?
)