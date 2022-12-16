package com.wallia.eamxcomunidades.model

data class CommunityLinkedModel(
    val communityName : String,
    val instituteOrAssociation: String = "",
    val managerName : String,
    val phone : String,
    val email : String
)
