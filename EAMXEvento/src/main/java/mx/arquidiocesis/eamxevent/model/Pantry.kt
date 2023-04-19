package mx.arquidiocesis.eamxevent.model

import com.google.gson.annotations.SerializedName

open class Pantry(
    @SerializedName("despensa_id")
    val id: Int? = null,
    @SerializedName("user_id")
    val user_id: Int? = null,
    @SerializedName("horarios")
    val schedule: MutableList<Schedules>? = ArrayList(),
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
    @SerializedName("voluntarios") //Sin uso
    val volunteers: String? = null,
    @SerializedName("donadores") //Sin uso
    val donors: String? = "[]",
    @SerializedName("zona")
    val zone_id: Int? = null,
    @SerializedName("estatus")
    val status: Int? = 0,
    @SerializedName("req_armado")
    val required_armed: Int? = 0,
    @SerializedName("req_entrega")
    val required_delivery: Int? = 0,
    @SerializedName("req_donador")
    val required_donor: Int? = 0,
    @SerializedName("fecha_entrega") //Sin uso
    val distributed : String? = null,
    @SerializedName("fecha_recibido") //Recibido
    val received: Process? = null,
    @SerializedName("fecha_armado") //Armado
    val armed: Process? = null,
    @SerializedName("fecha_repartido") //Entrega
    val delivery: Process? = null,
    @SerializedName("req_descripcion")
    val description_requirements: String? = null,
    @SerializedName("direccion_entrega")
    val address_delivery: String? = null,
    @SerializedName("requisito_donador")
    val requirements_donor: String? = null,
    @SerializedName("longitud_entrega")
    val longitude_delivery: String? = null,
    @SerializedName("latitud_entrega")
    val latitude_delivery: String? = null,
)