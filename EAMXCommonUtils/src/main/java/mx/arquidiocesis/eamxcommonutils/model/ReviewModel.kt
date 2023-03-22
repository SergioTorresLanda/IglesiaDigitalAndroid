package mx.arquidiocesis.eamxcommonutils.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReviewModel(
    val id: Int?,
    val review: String?,
    val creation_date: String?,
    val rating: String?,
    val devotee: ReviewDevoteModel
    ) : Parcelable