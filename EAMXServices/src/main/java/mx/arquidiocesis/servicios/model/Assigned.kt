package mx.arquidiocesis.servicios.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Assigned(
    val id: Int,
    val image_url: String,
    val name: String
): Parcelable