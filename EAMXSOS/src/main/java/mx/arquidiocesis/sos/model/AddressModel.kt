package mx.arquidiocesis.sos.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddressModel(
    val description: String,
    val longitude: String,
    val latitude: String
) : Parcelable