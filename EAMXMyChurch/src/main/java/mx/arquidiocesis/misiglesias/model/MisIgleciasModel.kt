package mx.arquidiocesis.misiglesias.model

import mx.arquidiocesis.misiglesias.database.model.ChurchModelLocal

data class MisIgleciasModel(
    val assigned: Assigned?,
    val locations: List<Location>?
)

fun MisIgleciasModel.convertToChurchModelLocal(userId : Int) : List<ChurchModelLocal>{
    val listTemp : MutableList<ChurchModelLocal> = mutableListOf()
    this.locations?.let { list ->
         listTemp.addAll(list.map { item ->
            ChurchModelLocal(
            idChurch = item.id,
            isChurchMain = false,
            name = item.name,
            image_url = item.image_url,
            arrayImage =  item.arrayImage,
            userId = userId
        )})
    } ?: kotlin.run { listOf<ChurchModelLocal>() }

    this.assigned?.let { item ->
        listTemp.add(
            ChurchModelLocal(
                idChurch = item.id,
                isChurchMain = true,
                name = item.name,
                image_url = item.image_url,
                arrayImage = item.arrayImage,
                userId = userId
            )
        )
    }

    return listTemp
}

fun List<ChurchModelLocal>.convertToLocationMyChurch() : MisIgleciasModel {
    val churchMain = this.firstOrNull { item -> item.isChurchMain }
    val assigned = churchMain?.let { church ->
        Assigned(
            id = church.idChurch,
            name = church.name,
            image_url = church.image_url,
            schedules = listOf(),
            arrayImage =  church.arrayImage
        )
    }

    val churchFavorites = this.filter { item -> item.isChurchMain.not() }
    val locations = churchFavorites.let {
        it.map { item ->
            Location(
                id = item.idChurch,
                name = item.name,
                image_url = item.image_url,
                schedules = listOf(),
                arrayImage = item.arrayImage
            )
        }
    }

    return MisIgleciasModel(
        assigned = assigned,
        locations = locations
    )
}