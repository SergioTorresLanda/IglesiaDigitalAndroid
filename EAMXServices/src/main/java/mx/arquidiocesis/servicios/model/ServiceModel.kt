package mx.arquidiocesis.servicios.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ServiceModel(
    val type: TypeModel?,
    val horary : List<HoraryTypeModel?>?
) :
    Parcelable