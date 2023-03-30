package mx.arquidiocesis.eamxevent.model

import com.google.gson.annotations.SerializedName

data class GuestModel(
    @SerializedName("nombre")
    var nombre: String? = null,
    @SerializedName("telefono")
    var telefono: String? = null
)