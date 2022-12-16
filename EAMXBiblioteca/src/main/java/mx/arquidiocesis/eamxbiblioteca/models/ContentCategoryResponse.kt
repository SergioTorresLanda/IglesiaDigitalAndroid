package mx.arquidiocesis.eamxbiblioteca.models


import com.google.gson.annotations.SerializedName

data class ContentCategoryResponse(
    @SerializedName("contents")
    val contents: ArrayList<Content>,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("title")
    val title: String
)