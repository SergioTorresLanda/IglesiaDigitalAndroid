package mx.arquidiocesis.misiglesias.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PriestModel(
    val name: String,
    val id: Int
) : Parcelable