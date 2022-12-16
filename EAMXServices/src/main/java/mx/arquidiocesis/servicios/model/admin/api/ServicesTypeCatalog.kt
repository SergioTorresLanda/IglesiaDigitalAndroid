package mx.arquidiocesis.servicios.model.admin.api

import mx.arquidiocesis.servicios.model.StatusServices

class ServicesTypeCatalog : ArrayList<ServicesTypeCatalogItem>()

fun ServicesTypeCatalog.mapToStatusServices() : List<StatusServices> {
    return this.map {
        StatusServices(id = it.id, name = it.name)
    }
}