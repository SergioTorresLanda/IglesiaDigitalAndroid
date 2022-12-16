package mx.arquidiocesis.sos.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SchedHourModel(
    val start_hour: String,
    val end_hour: String
): Parcelable