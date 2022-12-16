package mx.arquidiocesis.servicios.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DedicatedTo(
    val from: String,
    val to: String
) : Parcelable