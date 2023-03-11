package com.example.eamxevent.models

import com.google.gson.annotations.SerializedName

data class Actor(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("id_user")
    val id_user: Int?,
    @SerializedName("rol_id")
    val rol_id: Int?
)
