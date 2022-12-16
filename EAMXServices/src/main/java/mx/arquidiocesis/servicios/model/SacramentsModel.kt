package mx.arquidiocesis.servicios.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SacramentsModel(
    var id: Int,
    var name: String,
    var description:String,
    var file:String,
    var action: String
    ) : Parcelable