package mx.arquidiocesis.servicios.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ZipCodeModel(
    var data: List<CodigoPostalModel>
) : Parcelable