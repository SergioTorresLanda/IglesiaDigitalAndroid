package mx.arquidiocesis.servicios.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ServiceMenuMainModel(
    var id: Int,
    var title: String,
    var desc: String,
    var img:String
    ) : Parcelable