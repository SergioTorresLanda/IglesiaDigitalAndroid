package mx.arquidiocesis.misiglesias.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SchedulesModel(
    var day: String,
    var start_hour: String,
    var end_hour: String
) :
    Parcelable