package mx.arquidiocesis.sos.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PriestX(
    val name: String,
    val priest_id: Int
): Parcelable