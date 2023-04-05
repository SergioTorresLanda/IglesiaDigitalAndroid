package mx.arquidiocesis.eamxevent.model

import com.google.gson.annotations.SerializedName

open class Event(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("nombre")
    val name: String? = null,
    @SerializedName("user_id")
    val user_id: Int? = null,
    @SerializedName("horarios")
    val schedule: MutableList<Schedules>,
    @SerializedName("responsable")
    val responsability: String? = null,
    @SerializedName("correo")
    val email: String? = null,
    @SerializedName("telefono")
    val phone: String? = null,
    @SerializedName("direccion")
    val address: String? = null,
    @SerializedName("longitud")
    val longitude: String? = null,
    @SerializedName("latitud")
    val latitude: String? = null,
    @SerializedName("cobro")
    val amount: String? = "0",
    @SerializedName("requisitos")
    val requirements: String? = null,
    @SerializedName("voluntarios") //Sera similar para dispensas
    val volunteers: Int? = null,
    @SerializedName("donantes")
    val donors: ArrayList<Int> = ArrayList(),
    @SerializedName("zona")
    val zone_id: Int? = null,
    @SerializedName("status")
    val status: Int? = 0
)