package mx.arquidiocesis.servicios.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IglesiaBusquedaModel(
    val id: Int,
    val image: String,
    val name: String
): Parcelable