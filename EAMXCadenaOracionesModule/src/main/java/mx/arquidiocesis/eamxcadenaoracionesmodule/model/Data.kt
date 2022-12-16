package mx.arquidiocesis.eamxcadenaoracionesmodule.model

import com.google.gson.annotations.SerializedName

data class Data(
    val creation_date: String,
    val description: String,
    @SerializedName("fiel_image_url")
    val imageProfile: String,
    val fiel_name: String,
    val id: Int,
    val reaction: Reaction,
    val praying: Int
)