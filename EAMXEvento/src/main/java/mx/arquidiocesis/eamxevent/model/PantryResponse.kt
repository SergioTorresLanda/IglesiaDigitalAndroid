package mx.arquidiocesis.eamxevent.model

import com.google.gson.annotations.SerializedName

data class PantryResponse(
    @SerializedName("correo")
    val correo: String? = null,
    @SerializedName("despensa_id")
    val despensaId: Int? = null,
    @SerializedName("direccion")
    val direccion: String? = null,
    @SerializedName("direccion_entrega")
    val direccionEntrega: String? = null,
    @SerializedName("donadores")
    val donadores: String? = "[]",
    @SerializedName("estatus")
    val estatus: Int? = null,
    @SerializedName("fecha_armado")
    val fechaArmado: MutableList<Process>? = null,
    @SerializedName("fecha_entrega")
    val fechaEntrega: String? = null,
    @SerializedName("fecha_recibido")
    val fechaRecibido: MutableList<Process>? = null,
    @SerializedName("fecha_repartido")
    val fechaRepartido: MutableList<Process>? = null,
    @SerializedName("horarios")
    val horarios: MutableList<Schedules>? = null,
    @SerializedName("latitud")
    val latitud: Float? = null,
    @SerializedName("latitud_entrega")
    val latitudEntrega: Float? = null,
    @SerializedName("longitud")
    val longitud: Float? = null,
    @SerializedName("longitud_entrega")
    val longitudEntrega: Float? = null,
    @SerializedName("req_armado")
    val reqArmado: Int? = null,
    @SerializedName("req_descripcion")
    val reqDescripcion: String? = null,
    @SerializedName("req_donador")
    val reqDonador: Int? = null,
    @SerializedName("req_entrega")
    val reqEntrega: Int? = null,
    @SerializedName("requisito_donador")
    val requisitoDonador: String? = null,
    @SerializedName("responsable")
    val responsable: String? = null,
    @SerializedName("telefono")
    val telefono: String? = null,
    @SerializedName("user_id")
    val userId: Int? = null,
    @SerializedName("voluntarios")
    val voluntarios: String? = "[]",
    @SerializedName("zona")
    val zona: Int? = null
)