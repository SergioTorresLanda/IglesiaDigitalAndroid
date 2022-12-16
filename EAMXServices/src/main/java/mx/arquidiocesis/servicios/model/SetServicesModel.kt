package mx.arquidiocesis.servicios.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SetServicesModel(
    var devotee: DevoteeModel,
    var service: ServiceModel,
    var location: LocationModel,
    var family: String,
    var address: String
) :
    Parcelable
