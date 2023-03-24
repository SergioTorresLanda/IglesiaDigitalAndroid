package mx.arquidiocesis.eamxevent.model


import com.google.gson.annotations.SerializedName

data class Donor(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("bancarios")
    val bancarios: String? = null,
    @SerializedName("comentarios")
    val comentarios: String? = null,
    @SerializedName("correo")
    val correo: String? = null,
    @SerializedName("comedor_id")
    val comedor_id: Int? = null,
    @SerializedName("nombre")
    val nombre: String? = null,
    @SerializedName("telefono")
    val telefono: String? = null,
    @SerializedName("tipo_don")
    val tipo_don: String? = null,
    @SerializedName("user_id")
    val user_id: Int? = null
)