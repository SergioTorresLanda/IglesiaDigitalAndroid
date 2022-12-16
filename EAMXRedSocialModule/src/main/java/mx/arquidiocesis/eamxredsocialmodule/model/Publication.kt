package mx.arquidiocesis.eamxredsocialmodule.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Publication(
    val id: Int,
    val countView: Int,
    val active: Boolean,
    val countComment: Int,
    val type: String,
    val content: String,
    var countReact: Int?,
    val status: String,
    val autor: Autor,
    val area: Area,
    val media: List<Media>,
    val comments: @RawValue Any,
    val location: @RawValue Any,
    val `as`: String,
    val myReaction: @RawValue Any,
    val feeling: @RawValue Any,
    val created: Long
) : Parcelable