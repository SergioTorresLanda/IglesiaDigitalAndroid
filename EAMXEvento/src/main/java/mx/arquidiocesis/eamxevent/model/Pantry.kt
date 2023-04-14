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
    val longitude: Float? = null,
    @SerializedName("latitud")
    val latitude: Float? = null,
    @SerializedName("voluntarios") //Sin uso
    val volunteers: String? = null,
    @SerializedName("donadores") //Sin uso
    val donors: String? = "[]",
    @SerializedName("zona")
    val zone_id: Int? = null,
    @SerializedName("status")
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
    val received: MutableList<Process>? = ArrayList(),
    @SerializedName("fecha_armado") //Armado
    val armed: MutableList<Process>? = ArrayList(),
    @SerializedName("fecha_repartido") //Entrega
    val delivery: MutableList<Process>? = ArrayList(),
    @SerializedName("req_descripcion")
    val description_requirements: String? = null,
    @SerializedName("direccion_entrega")
    val address_delivery: String? = null,
    @SerializedName("requisito_donador")
    val requirements_donor: String? = null,
    @SerializedName("longitud_entrega")
    val longitude_delivery: Float? = null,
    @SerializedName("latitud_entrega")
    val latitude_delivery: Float? = null,
)