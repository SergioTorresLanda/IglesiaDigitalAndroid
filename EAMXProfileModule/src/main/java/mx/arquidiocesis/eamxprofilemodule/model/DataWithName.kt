package mx.arquidiocesis.eamxprofilemodule.model

data class DataWithName(
    val name: String,
    val id: Int
)

fun DataWithName.toChurchModel() : ChurchModel{
    return ChurchModel(
        id = this.id,
        name = this.name,
        address = "",
        imageUrl = "",
        latitude = "0.0",
        longitude = "0.0"
    )
}