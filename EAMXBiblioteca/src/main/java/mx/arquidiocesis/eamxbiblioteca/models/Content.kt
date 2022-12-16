package mx.arquidiocesis.eamxbiblioteca.models


import com.google.gson.annotations.SerializedName

data class Content(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)