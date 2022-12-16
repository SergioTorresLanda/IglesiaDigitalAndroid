package mx.arquidiocesis.eamxprofilemodule.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataWithNameCode(
    val name: String,
    val id: Int,
    val code: String
) : Parcelable
