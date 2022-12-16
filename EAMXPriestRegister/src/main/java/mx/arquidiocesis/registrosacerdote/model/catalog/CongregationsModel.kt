package mx.arquidiocesis.registrosacerdote.model.catalog

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CongregationsModel(
    val data: List<DataWithDescription>
) : Parcelable