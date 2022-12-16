package mx.arquidiocesis.servicios.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ServicesModel(
    var id: Int,
    var name: String,
    var description: String,
    var action: String
) :
    Parcelable