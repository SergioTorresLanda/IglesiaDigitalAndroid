package mx.arquidiocesis.registrosacerdote.model.catalog

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataLifeState(
    val name: String,
    val id: Int
) : Parcelable