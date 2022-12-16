package mx.arquidiocesis.eamxprofilemodule.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TopicsModel(
    val `data`: MutableList<DataWithDescription>
): Parcelable