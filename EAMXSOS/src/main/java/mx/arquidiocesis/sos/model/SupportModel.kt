package mx.arquidiocesis.sos.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SupportModel(
    val name: String,
    val contact_id: Int,
    val id: Int
): Parcelable