package mx.arquidiocesis.sos.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Devotee(
    val devotee_id: Int,
    val name: String,
    val phone: String
): Parcelable