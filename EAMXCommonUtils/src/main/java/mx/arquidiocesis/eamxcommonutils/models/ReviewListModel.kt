package mx.arquidiocesis.eamxcommonutils.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReviewListModel(
    val my_review: ReviewModel?,
    val other_reviews: List<ReviewModel>?
) : Parcelable