package com.wallia.eamxcomunidades.model

import com.google.gson.annotations.SerializedName
import com.wallia.eamxcomunidades.database.model.CommunityLocal

data class MainCommunityResponse(
    @SerializedName("assigned")
    val assigned: Location?,
    @SerializedName("locations")
    val locations: List<Location>?
)


fun MainCommunityResponse.convertToCommunityLocal(userId : Int) : List<CommunityLocal>{
    val listTemp : MutableList<CommunityLocal> = mutableListOf()
    this.locations?.let { list ->
        listTemp.addAll(list.map { item ->
            CommunityLocal(
                idCommunity = item.id ?: 0,
                userId = userId,
                isCommunityMain = false,
                name = item.name ?: "",
                image_url = item.imageUrl ?: "",
                arrayImage =  item.arrayImage
            )
        })
    } ?: kotlin.run { listOf<CommunityLocal>() }

    this.assigned?.let { item ->
        listTemp.add(
            CommunityLocal(
                idCommunity = item.id ?: 0,
                userId = userId,
                isCommunityMain = true,
                name = item.name?: "",
                image_url = item.imageUrl ?: "",
                arrayImage = item.arrayImage
            )
        )
    }

    return listTemp
}

fun List<CommunityLocal>.convertToLocationCommunity() : MainCommunityResponse {
    val churchMain = this.firstOrNull { item -> item.isCommunityMain }
    val assigned = churchMain?.let { church ->
        Location(
            id = church.idCommunity,
            name = church.name,
            imageUrl = church.image_url,
            arrayImage =  church.arrayImage
        )
    }

    val churchFavorites = this.filter { item -> item.isCommunityMain.not() }
    val locations = churchFavorites.let {
        it.map { item ->
            Location(
                id = item.idCommunity,
                name = item.name,
                imageUrl = item.image_url,
                arrayImage = item.arrayImage
            )
        }
    }

    return MainCommunityResponse(
        assigned = assigned,
        locations = locations
    )
}