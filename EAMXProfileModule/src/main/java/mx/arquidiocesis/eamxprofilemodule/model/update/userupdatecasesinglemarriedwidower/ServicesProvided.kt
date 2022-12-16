package mx.arquidiocesis.eamxprofilemodule.model.update.userupdatecasesinglemarriedwidower

import com.google.gson.annotations.Expose

data class ServicesProvided(
    @Expose
    var location_id: Int?,
    @Expose
    var service_id: Int?,
    @Expose
    var service_description: String?
)