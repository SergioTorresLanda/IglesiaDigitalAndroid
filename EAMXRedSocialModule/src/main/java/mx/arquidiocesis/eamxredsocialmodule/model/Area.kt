package mx.arquidiocesis.eamxredsocialmodule.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Area(
    val id: Int,
    val name: String,
    val active: Boolean,
    val img: String
) : Parcelable