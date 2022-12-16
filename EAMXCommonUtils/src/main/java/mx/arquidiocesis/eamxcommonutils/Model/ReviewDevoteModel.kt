package mx.arquidiocesis.eamxcommonutils.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReviewDevoteModel(
    val id: Int?,
    val name: String?,
    val first_surname: String?,
    val second_surname: String?,
    val image_url: String?
) : Parcelable