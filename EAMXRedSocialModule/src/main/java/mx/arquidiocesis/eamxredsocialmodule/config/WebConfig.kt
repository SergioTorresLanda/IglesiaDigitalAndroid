package mx.arquidiocesis.eamxredsocialmodule.config

import mx.arquidiocesis.eamxcommonutils.application.ConstansApp
import mx.arquidiocesis.eamxredsocialmodule.BuildConfig


object WebConfig {
    val HOST_BAZ = ConstansApp.hostRedSocial();
    val HOST_API = ConstansApp.hostEncuentro()

    const val USERSPOST = "users/{userId}/timeline"
    const val POSTUPDATE = "posts/{id}"
    const val POST = "posts"
    const val POSTREACT = "posts/{id}/react"
    const val REACTD = "reactions/{id}"
    const val COMMENT = "posts/{id}/comments"
    const val COMMENTPOST = "comments"
    const val COMMENTUPDATE = "comments/{id}"
    const val PROFILE = "profile/{id}"
    const val MULTIPROFILE = "user/{id}/relations"
    const val FOLLOW = "entity/follow"
    const val UNFOLLOW = "entity/{id}/unfollow"
    const val FOLLOWGET = "entity/{id}/follows"
    const val SEARCH = "searcher"

    const val UPLOAPMULTIMEDIA = "s3-credentials"

}