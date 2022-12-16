package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class CommunityType(
    @SerializedName("data")
    val `data`: List<Data>?
)