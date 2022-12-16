package mx.arquidiocesis.eamxredsocialmodule.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScopeModel(
    val id: Int?,
    val image: String?,
    val name: String?,
    val typeId: Int?
): Parcelable