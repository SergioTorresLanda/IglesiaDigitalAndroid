package mx.arquidiocesis.servicios.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TitleExpanderListModel(
    val id: Int,
    val name: String,
    var service: MutableList<ServicesModel>
) : Parcelable