package mx.arquidiocesis.eamxdonaciones.model

data class DonationModel(
    var id: Int,
    var name: String,
    val image_url: String?,
    )