package mx.arquidiocesis.eamxevent.model


import mx.arquidiocesis.eamxevent.model.catalog.Zone
import com.google.gson.annotations.SerializedName

data class Event(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("nombre")
    val name: String? = null,
    @SerializedName("horarios")
    val schedule: List<Schedules>,
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
    val amount: Int? = null,
    @SerializedName("requisitos")
    val requirements: String? = null,
    @SerializedName("voluntarios")
    val volunteers: String? = null,
    @SerializedName("donantes")
    val donors: List<Int>,
    @SerializedName("zona")
    val zone_id: List<Zone>,
    @SerializedName("status")
    val status: Int? = null,
    //@SerializedName("type_event_id")
    //val type_event_id: Int? = null
)