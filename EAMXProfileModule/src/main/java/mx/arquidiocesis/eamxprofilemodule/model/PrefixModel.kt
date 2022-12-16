package mx.arquidiocesis.eamxprofilemodule.model

import kotlinx.android.parcel.Parcelize
import mx.arquidiocesis.eamxprofilemodule.model.DataWithDescription
import android.os.Parcelable as Parcelable

@Parcelize
data class PrefixModel(
    val `data`: MutableList<DataWithDescription>
) : Parcelable