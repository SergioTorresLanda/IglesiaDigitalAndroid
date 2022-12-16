package mx.arquidiocesis.misiglesias.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IglesiaBusquedaModel(
    val id: Int,
    val image_url: String,
    val latitude:String,
    val longitude:String,
    val name: String
): Parcelable