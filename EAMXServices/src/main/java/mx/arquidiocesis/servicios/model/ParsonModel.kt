package mx.arquidiocesis.servicios.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ParsonModel(
    val name: String,
    val priest_id: String
) : Parcelable