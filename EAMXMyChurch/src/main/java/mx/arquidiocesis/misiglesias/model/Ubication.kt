package mx.arquidiocesis.misiglesias.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Ubication(
    var name: String,
    var address: String,
    var latitude: Double,
    var longitude: Double,
    var phone: String,
    var website: String,
    var photo: String,
) : Parcelable