package mx.arquidiocesis.misiglesias.model

data class ScheduleAttention(
    var day: String, var status: Boolean, var hours: Hour
)
