package mx.arquidiocesis.misiglesias.model

data class Assigned(
    val id: Int,
    val image_url: String,
    val name: String,
    val schedules: List<SchedulesModel>,
    var arrayImage : String? =null)