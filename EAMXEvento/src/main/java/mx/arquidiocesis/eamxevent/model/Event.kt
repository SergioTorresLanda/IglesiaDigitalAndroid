package mx.arquidiocesis.eamxevent.model

import com.google.gson.annotations.SerializedName

/*public sealed interface CampaignContent {
    public data class PopupModal(
        public val imageUrl: String,
        public val text: String,
        public val subtext: String,
    ) : CampaignContent

    public data class Link(
        public val linkText: String,
        public val url: String,
        public val linkIcon: String? = null,
    ) : CampaignContent
}*/

open class Event(
    @SerializedName("id")
    val id: Int? = null,
    //Comedor
    @SerializedName("nombre")
    val name: String? = null,
    //General//
    @SerializedName("user_id")
    val user_id: Int? = null,
    //General//
    @SerializedName("horarios")
    val schedule: MutableList<Schedules>,
    //General//
    @SerializedName("responsable")
    val responsability: String? = null,
    //General//
    @SerializedName("correo")
    val email: String? = null,
    //Comedor
    @SerializedName("telefono")
    val phone: String? = null,
    //General//
    @SerializedName("direccion")
    val address: String? = null,
    //General//
    @SerializedName("longitud")
    val longitude: String? = null,
    //General//
    @SerializedName("latitud")
    val latitude: String? = null,
    //Comedor
    @SerializedName("cobro")
    val amount: String? = "0",
    //Comedor
    @SerializedName("requisitos")
    val requirements: String? = null,
    //General//
    @SerializedName("voluntarios") //Sera similar para dispensas
    val volunteers: Int? = null,
    //General//
    @SerializedName("donantes")
    val donors: ArrayList<Int> = ArrayList(),
    //General//
    @SerializedName("zona")
    val zone_id: Int? = null,
    //Comedor
    @SerializedName("status")
    val status: Int? = 0,
    //Dispensa
    @SerializedName("req_armado")
    val required_armed: Int? = 0,
    //Dispensa
    @SerializedName("req_entrega")
    val required_delivery: Int? = 0,
    //Dispensa
    @SerializedName("req_donador")
    val required_donor: Int? = 0,
    //Dispensa
    @SerializedName("fecha_entrega")
    val date_deadline: String? = "",
    //Dispensa
    @SerializedName("fecha_recibido")
    val date_received: String? = "",
    //Dispensa
    @SerializedName("fecha_repartido")
    val date_delivery: String? = "",
    //Dispensa
    @SerializedName("req_descripcion")
    val required_description: String? = "",
    //Dispensa
    @SerializedName("direccion_entrega")
    val address_deadline: String? = "",
    //Dispensa
    @SerializedName("requisito_donador")
    val requirements_donor: String? = "",
)