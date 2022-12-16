package mx.arquidiocesis.sos.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ServicesPriestModel(
    val id: Int,
    val status: String,
    val sub_status: String,
    val funeral_home: String,
    val service: ServicePriestModel,
    val location: LocationBoxModel,
    val devotee: Devotee,
    val modification_date: String,
    val priest: Priest
) : Parcelable