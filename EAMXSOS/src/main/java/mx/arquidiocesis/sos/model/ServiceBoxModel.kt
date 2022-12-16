package mx.arquidiocesis.sos.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ServiceBoxModel(
    val id: Int,
    val name: String
) : Parcelable