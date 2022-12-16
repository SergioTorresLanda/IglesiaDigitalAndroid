package mx.arquidiocesis.eamxpagos.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CodigoPostalModel(
    var id: Int,
    var name:String
) : Parcelable