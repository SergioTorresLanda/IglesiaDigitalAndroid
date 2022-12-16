package mx.arquidiocesis.sos.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationBoxModel(
    val id: Int,
    val distance: Double,
    val image_url: String,
    val name: String
): Parcelable