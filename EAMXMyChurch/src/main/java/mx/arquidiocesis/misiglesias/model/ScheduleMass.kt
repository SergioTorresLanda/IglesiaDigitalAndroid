package mx.arquidiocesis.misiglesias.model

data class ScheduleMass(
    var day: String,
    var hours: List<Hour>,
)

data class Hour(
    var start_hour: String,
    var end_hour: String?
)
