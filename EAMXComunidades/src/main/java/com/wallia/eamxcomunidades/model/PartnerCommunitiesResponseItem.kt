package com.wallia.eamxcomunidades.model


import com.google.gson.annotations.SerializedName

data class PartnerCommunitiesResponseItem(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("institute_or_association")
    val instituteOrAssociation: InstituteOrAssociation?,
    @SerializedName("name")
    val name: String?
)