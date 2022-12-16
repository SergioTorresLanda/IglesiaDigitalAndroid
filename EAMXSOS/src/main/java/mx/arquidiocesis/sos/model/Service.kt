package mx.arquidiocesis.sos.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Service(
    val id: Int,
    val name: String,
    val description: String,
    var select : Boolean = false)
: Parcelable