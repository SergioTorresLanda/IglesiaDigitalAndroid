package mx.arquidiocesis.oraciones.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OracionModel(var id: Int,  var name: String) : Parcelable