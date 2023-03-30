package mx.arquidiocesis.eamxevent.model

import com.google.gson.annotations.SerializedName

data class Volunteer(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("user_id")
    val user_id: Int? = null,
    @SerializedName("responsable")
    val responsable: String? = null,
    @SerializedName("direccion")
    val direccion: String? = null,
    @SerializedName("nombre_comedor")
    val comedor_id: String? = null,
    @SerializedName("voluntario")
    val nombre_voluntario: String? = null,
    @SerializedName("telefono")
    val telefono: String? = null,
    @SerializedName("multiuser")
    val multiuser: MutableList<GuestModel>? = mutableListOf(),
    @SerializedName("correo")
    val correo: String? = null
)