package mx.arquidiocesis.eamxcommonutils.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ViewPagerModel(
    var contenido: String,
    var imagenes: Bitmap?,
    var tipo: Int,
    var list: List<String> = listOf(),
    var titulo: Boolean=false
) : Parcelable