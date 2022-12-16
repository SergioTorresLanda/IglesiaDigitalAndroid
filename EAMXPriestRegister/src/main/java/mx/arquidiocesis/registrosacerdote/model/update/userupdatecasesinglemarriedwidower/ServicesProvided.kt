package mx.arquidiocesis.registrosacerdote.model.update.userupdatecasesinglemarriedwidower

import com.google.gson.annotations.Expose

data class ServicesProvided(
    @Expose
    val location_id: Int,
    @Expose
    val service_id: Int
)