package mx.arquidiocesis.misiglesias.model


data class HoraryModelItem(
    var days: List<Day>,
    var hour_start: String,
    var hour_end: String
)