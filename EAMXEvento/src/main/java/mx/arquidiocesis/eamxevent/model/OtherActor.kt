package mx.arquidiocesis.eamxevent.model


import com.google.gson.annotations.SerializedName
import mx.arquidiocesis.eamxcommonutils.util.live.SingleLiveEvent

data class OtherActor(
    @SerializedName("nombre")
    val nombre: String? = null,
    @SerializedName("correo")
    val correo: String? = null,
    @SerializedName("telefono")
    val telefono: String? = null,
    @SerializedName("status")
    val status: Int? = null,
    @SerializedName("comentarios")
    val comentarios: String? = null,
    @SerializedName("user_id")
    val user_id: Int? = null,
    @SerializedName("evento_id")
    val evento_id: Int? = null,
    @SerializedName("tipo_actor")
    val tipo_actor: Int? = null,
    @SerializedName("actor_id")
    val actor_id: Int? = null,
)
