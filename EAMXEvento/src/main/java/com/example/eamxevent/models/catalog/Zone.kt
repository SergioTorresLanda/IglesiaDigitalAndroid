package com.example.eamxevent.models.catalog

import com.google.gson.annotations.SerializedName

data class Zone(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("zone")
    val zone: String? = null
)
