package mx.arquidiocesis.eamxgeneric.model


import com.google.gson.annotations.SerializedName

data class DataHomeSaintResponse(
    @SerializedName("ending_date")
    val endingDate: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("publish_url")
    val publishUrl: String?,
    @SerializedName("starting_date")
    val startingDate: String?,
    @SerializedName("title")
    val title: String?
)