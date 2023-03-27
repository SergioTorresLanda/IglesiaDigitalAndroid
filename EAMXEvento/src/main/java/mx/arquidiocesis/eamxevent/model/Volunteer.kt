package mx.arquidiocesis.eamxevent.model

import com.google.gson.annotations.SerializedName

data class Volunteer(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("correo")
    val correo: String? = null,
    @SerializedName("user_id")
    val user_id: Int? = null,
    @SerializedName("direccion")
    val direccion: String? = null,
    @SerializedName("multiuser")
    val multiuser: ArrayList<String>? = ArrayList(),
    @SerializedName("comedor_id")
    val comedor_id: String? = null,
    @SerializedName("nombre_voluntario")
    val nombre_voluntario: String? = null,
    @SerializedName("responsable")
    val responsable: String? = null,
    @SerializedName("telefono")
    val telefono: String? = null
)