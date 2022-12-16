package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class Community(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("status")
    val status: String?
)