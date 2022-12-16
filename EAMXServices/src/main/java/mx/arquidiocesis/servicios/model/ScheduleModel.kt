package mx.arquidiocesis.servicios.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScheduleModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("image_url")
    val image_url: String,
    @SerializedName("masses")
    val masses: List<Masse>? = null,
    @SerializedName("name")
    val name: String
): Parcelable