package mx.arquidiocesis.sos.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProgressHistoryModel(
    val creation_date: String,
    val status: String,
    val sub_status: String
): Parcelable