package mx.arquidiocesis.eamxevent.model


import com.google.gson.annotations.SerializedName

data class Day(
    @SerializedName("checked")
    val checked: Boolean,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)