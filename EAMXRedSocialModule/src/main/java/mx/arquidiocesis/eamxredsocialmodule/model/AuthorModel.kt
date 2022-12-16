package mx.arquidiocesis.eamxredsocialmodule.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AuthorModel(
    val id: Int,
    val name: String,
    val image:String?,
    val thumbnail:String?,
    val type: Int
): Parcelable