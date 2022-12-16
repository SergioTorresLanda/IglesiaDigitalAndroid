package mx.arquidiocesis.sos.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReviewModel(
    val rating: String?,
    val review_value: String?
) : Parcelable