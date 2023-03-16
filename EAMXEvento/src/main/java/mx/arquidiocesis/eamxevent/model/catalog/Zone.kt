package mx.arquidiocesis.eamxevent.model.catalog

import com.google.gson.annotations.SerializedName
import mx.arquidiocesis.eamxevent.model.enum.Zone

data class Zone(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("zone")
    val zone: Int? = null
)
