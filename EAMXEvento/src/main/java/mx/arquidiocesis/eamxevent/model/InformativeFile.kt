package mx.arquidiocesis.eamxevent.model

import com.google.gson.annotations.SerializedName

data class InformativeFile(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("date")
    val date: String? = null,
    @SerializedName("time")
    val time: String? = null,
    @SerializedName("organizer")
    val organizer: String? = null,
    @SerializedName("church")
    val church: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("places_available")
    val places_available: String? = null,
    @SerializedName("recovery_amount")
    val recovery_amount: Int? = null,
)
