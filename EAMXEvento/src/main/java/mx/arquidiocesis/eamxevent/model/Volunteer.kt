package mx.arquidiocesis.eamxevent.model

import com.google.gson.annotations.SerializedName

data class Volunteer(
    @SerializedName("correo")
    val correo: String,
    @SerializedName("direccion")
    val direccion: String,
    @SerializedName("multiuser")
    val multiuser: ArrayList<String> = ArrayList(),
    @SerializedName("nombre_comedor")
    val nombreComedor: String,
    @SerializedName("nombre_voluntario")
    val nombreVoluntario: String,
    @SerializedName("responsable")
    val responsable: String,
    @SerializedName("telefono")
    val telefono: String
)