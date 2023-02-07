package mx.arquidiocesis.servicios.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IgleciasModel(
    val id: Int,
    val name:String,
    val address: String,
    val image_url: String,
    val latitude: String,
    val longitude: String,
) : Parcelable