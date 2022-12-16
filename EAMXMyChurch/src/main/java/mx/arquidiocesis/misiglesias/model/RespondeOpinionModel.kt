package mx.arquidiocesis.misiglesias.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RespondeOpinionModel(
    val rating: String?,
    val id: Int
) : Parcelable