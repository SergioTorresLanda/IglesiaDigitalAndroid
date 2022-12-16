package mx.arquidiocesis.sos.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationSOSModel(
    val id: Int,
    val name: String,
    val image_url: String,
    val distance: Double,
    val phone: String?,
    val schedules: List<SchedHourModel>,
    val support_contacts:List<SupportModel>
): Parcelable