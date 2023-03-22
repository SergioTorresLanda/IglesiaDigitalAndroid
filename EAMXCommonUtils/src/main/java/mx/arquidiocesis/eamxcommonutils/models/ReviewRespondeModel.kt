package mx.arquidiocesis.eamxcommonutils.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReviewRespondeModel(
    val rating: String?,
    val id: Int?
) : Parcelable