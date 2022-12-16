package mx.arquidiocesis.misiglesias.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OpinioModel(
    val id: Int?,
    val review: String?,
    val rating: String?,
    val devotee: DevoteeModel?,

) : Parcelable