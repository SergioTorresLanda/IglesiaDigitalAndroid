package mx.arquidiocesis.eamxcommonutils.managerpictures.model
import com.google.gson.annotations.SerializedName
data class ImageRequestModel(
        @SerializedName("element_id")
        val userIdentifier: Int,
        @SerializedName("type")
        val source: String,
        val filename: String,
        val content: String,
)
