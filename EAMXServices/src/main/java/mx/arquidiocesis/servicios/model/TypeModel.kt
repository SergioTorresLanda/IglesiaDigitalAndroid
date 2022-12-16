package mx.arquidiocesis.servicios.model


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TypeModel(
    val id: Int?,
    val name: String?
): Parcelable