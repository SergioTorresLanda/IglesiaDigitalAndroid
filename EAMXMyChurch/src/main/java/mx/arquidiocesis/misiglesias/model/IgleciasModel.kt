package mx.arquidiocesis.misiglesias.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IgleciasModel(
    val id: Int,
    val name:String,
    @SerializedName("image_url")
    val imageUrl: String,
    val latitude: String,
    val longitude: String,
    val category:String
) : Parcelable