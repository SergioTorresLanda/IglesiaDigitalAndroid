package mx.arquidiocesis.eamxevent.model


import com.google.gson.annotations.SerializedName
import mx.arquidiocesis.eamxcommonutils.util.live.SingleLiveEvent

data class OtherEvent(
    @SerializedName("horarios")
    val horarios:  MutableList<Schedules>? = null,
    @SerializedName("responsable")
    val responsable: String? = null,
    @SerializedName("correo")
    val correo: String? = null,
    @SerializedName("telefono")
    val telefono: String? = null,
    @SerializedName("direccion")
    val direccion: String? = null,
    @SerializedName("cobro")
    val cobro: Int? = null,
    @SerializedName("descripcion")
    val descripcion: String? = null,
    @SerializedName("publico")
    val publico: String? = null,
    @SerializedName("donantesBool")
    val donantesBool: Int? = null,
    @SerializedName("voluntariosBool")
    val voluntariosBool: Int? = null,
    @SerializedName("donantesTxt")
    val donantesTxt: String? = null,
    @SerializedName("voluntariosTxt")
    val voluntariosTxt: String? = null,
    @SerializedName("status")
    val status: Int? = null,
    @SerializedName("nombre")
    val nombre: String? = null,
    @SerializedName("userId")
    val userId: Int? = null,
    @SerializedName("eventoId")
    val eventoId: Int? = null,
    @SerializedName("tipoEvento")
    val tipoEvento: Int? = null,
)
