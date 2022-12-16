package mx.arquidiocesis.eamxcommonutils.managerpictures.config

import mx.arquidiocesis.eamxcommonutils.BuildConfig

object WebConfig {
    const val GET_CATALOG_LIFE_STATE = "catalog/life-states"
    const val GET_CATALOG_PROVIDED_SERVICES = "catalog/provided-services"
    const val GET_CATALOG_CONGREGATION = "catalog/congregations"
    const val GET_CATALOG_TOPICS = "catalog/topics"
    const val GET_DETAIL_USER = "user/detail/{id}"
    const val POST_UPDATE_USER = "user/update"
    const val GET_CHURCH = "locations"

    const val FAVORITE_CHURCH = "users/{user_id}/locations"
    const val GET_MODULES = "locations/{location_id}/modules"
    const val UPDATE_MODULES = "locations/{location_id}/modules"
    const val GET_COLLABORATORS = "locations/{location_id}/collaborators"
    const val GET_COLLABORATOR_DETAIL = "locations/{location_id}/collaborators/{user_id}"
    const val UPDATE_MODULES_OF_COLLABORATOR = "locations/{location_id}/collaborators/{user_id}/modules"

    const val GET_URL_IMAGE = "s3-upload"
}