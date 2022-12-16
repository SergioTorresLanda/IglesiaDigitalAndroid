package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class LinkedCommunity(
    @SerializedName("email")
    val email: String?,
    @SerializedName("leader")
    val leader: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("phone")
    val phone: String?
)