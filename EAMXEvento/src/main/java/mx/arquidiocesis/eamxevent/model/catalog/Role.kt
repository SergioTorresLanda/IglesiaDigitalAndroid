package mx.arquidiocesis.eamxevent.model.catalog

import com.google.gson.annotations.SerializedName

data class Role(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("type_action_id")
    val type_action_id: Int?
)
