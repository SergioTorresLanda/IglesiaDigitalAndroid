package mx.arquidiocesis.eamxredsocialmodule.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MultimediaModel(
    val id: Int,
    var format: String,
    val thumnail: String,
    val url: String
): Parcelable