package mx.arquidiocesis.eamxgeneric.model


import com.google.gson.annotations.SerializedName

data class DataHomeReleaseResponse(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("publish_date")
    val publishDate: String?,
    @SerializedName("publish_url")
    val publishUrl: String?,
    @SerializedName("title")
    val title: String?
)