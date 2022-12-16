package mx.arquidiocesis.sos.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ServicePriestModel(
    val id: Int,
    val name: String,
    val type: String
) : Parcelable
