package mx.arquidiocesis.eamxevent.model


import com.google.gson.annotations.SerializedName

data class Donor(
    @SerializedName("bancarios")
    val bancarios: String? = null,
    @SerializedName("comentarios")
    val comentarios: String? = null,
    @SerializedName("correo")
    val correo: String? = null,
    @SerializedName("evento_id")
    val eventoId: String? = null,
    @SerializedName("nombre")
    val nombre: String? = null,
    @SerializedName("telefono")
    val telefono: String? = null,
    @SerializedName("tipo_don")
    val tipoDon: String? = null,
    @SerializedName("user_id")
    val userId: String? = null
)