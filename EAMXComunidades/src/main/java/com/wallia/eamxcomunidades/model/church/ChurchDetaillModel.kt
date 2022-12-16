package com.wallia.eamxcomunidades.model.church

data class ChurchDetaillModel(
    val id: Int?,
    var name: String?,
    var image_url: String?,
    var description: String?,
    var address: String?,
    var latitude: String?,
    var longitude: String?,
    var distance: String?,
    var email: String?,
    var phone: String?,
    var stream: String?,
    var website: String?,
    var facebook: String?,
    var twitter: String?,
    var intagram: String?,
    var rating: String?,
    var bank_account: String?,
    var reviewing: Boolean?
)