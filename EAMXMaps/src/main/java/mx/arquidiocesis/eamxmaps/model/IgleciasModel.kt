package mx.arquidiocesis.eamxmaps.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IgleciasModel(
    val id: Int,
    val name: String,
    val address: String,
    @SerializedName("image_url")
    val imageUrl: String,
    val latitude: String,
    val longitude: String,
    val category: String
) : Parcelable