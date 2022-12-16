package mx.arquidiocesis.servicios.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DevoteeModel(
    var devotee_id: Int,
    var phone: String,
    var name: String,
    var email: String
) :
    Parcelable