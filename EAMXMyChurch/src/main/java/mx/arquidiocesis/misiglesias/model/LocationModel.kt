package mx.arquidiocesis.misiglesias.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationModel(
    var locationId: Int,
    var name: String,
    var image: String,
    var longitude: String,
    var latitude: String
) :
    Parcelable