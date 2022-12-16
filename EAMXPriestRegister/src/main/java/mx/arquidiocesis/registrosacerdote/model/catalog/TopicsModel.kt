package mx.arquidiocesis.registrosacerdote.model.catalog

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TopicsModel(
    val data: List<DataWithDescription>
): Parcelable