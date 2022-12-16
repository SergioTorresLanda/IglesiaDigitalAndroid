package mx.arquidiocesis.servicios.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationModel(
    var id: Int
) : Parcelable