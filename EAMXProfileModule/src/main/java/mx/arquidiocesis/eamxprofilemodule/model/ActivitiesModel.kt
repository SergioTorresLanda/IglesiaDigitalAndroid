package mx.arquidiocesis.eamxprofilemodule.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ActivitiesModel(
    var id: Int,
    var name: String
) : Parcelable
