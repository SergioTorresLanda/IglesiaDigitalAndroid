package mx.arquidiocesis.misiglesias.model

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
    var instagram: String?,
    var rating: String?,
    var bank_account: String?,
    var reviewing: Boolean?,
    var principal: PrincipalModel?,
    var priests: List<PriestModel>?,
    var schedules: List<HoraryModelItem>?,
    var attention: List<HoraryModelItem>?,
    var masses: List<HoraryModelItem>?,
    var services: List<ServiceModel>?
)