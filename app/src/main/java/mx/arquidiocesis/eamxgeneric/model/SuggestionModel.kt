package mx.arquidiocesis.eamxgeneric.model


import com.google.gson.annotations.SerializedName

data class SuggestionModel(
    @SerializedName("category")
    val category: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("publish_date")
    val publishDate: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("article_url")
    val article_url: String?
)