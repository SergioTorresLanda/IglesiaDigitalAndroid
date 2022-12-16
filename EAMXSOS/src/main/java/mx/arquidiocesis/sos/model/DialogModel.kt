package mx.arquidiocesis.sos.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DialogModel(
    val text1: String,
    val text2: String,
    val star:Int
): Parcelable