package com.wallia.eamxcomunidades.config

import com.wallia.eamxcomunidades.BuildConfig
import mx.arquidiocesis.eamxcommonutils.application.ConstansApp

object WebConfig {
    val HOST_USER : String = ConstansApp.hostUser();
    val HOSTAPI = ConstansApp.hostEncuentro()
    const val GET_DETAIL_USER = "user/detail/{id_user}"
    const val GETCOMMUNITYMODULES = "locations/{location_id}/modules"
    const val COMMUNITY = "users/{user_id}/locations"
    const val DELETECOMMUNITY = "users/{user_id}/locations/{location_id}"
    const val GETCOMMUNITYTYPES = "catalog/community-types"
    const val LOCATIONS = "locations"
    const val GETLOCATIONS = "locations/{location_id}"
    const val PUTLOCATIONS = "locations/{location_id}"
    const val GETACTIVITIES = "locations/{location_id}/activities"
    const val REVIEW = "locations/{location_id}/reviews"
}