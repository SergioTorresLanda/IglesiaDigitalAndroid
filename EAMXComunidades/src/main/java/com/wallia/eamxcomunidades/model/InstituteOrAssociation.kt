package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class InstituteOrAssociation(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
)