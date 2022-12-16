package mx.arquidiocesis.eamxprofilemodule.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CongregationsModel(
    val `data`: List<CongregationModel>
) : Parcelable