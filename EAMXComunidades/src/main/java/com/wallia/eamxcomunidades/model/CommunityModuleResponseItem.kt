package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class CommunityModuleResponseItem(
    @SerializedName("category")
    val category: String?,
    @SerializedName("enable")
    val enable: Boolean?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
)