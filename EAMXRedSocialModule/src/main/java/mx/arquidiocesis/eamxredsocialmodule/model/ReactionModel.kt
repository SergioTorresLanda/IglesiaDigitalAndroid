package mx.arquidiocesis.eamxredsocialmodule.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReactionModel(
    val id: Int,
    val type: Int
) : Parcelable