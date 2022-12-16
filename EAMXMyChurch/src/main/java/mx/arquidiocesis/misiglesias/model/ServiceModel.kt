package mx.arquidiocesis.misiglesias.model

data class ServiceModel(
    val schedules: List<HoraryModelItem>?,
    val type: TypeModel,
    val geared_toward: String?,
    val description: String?
)