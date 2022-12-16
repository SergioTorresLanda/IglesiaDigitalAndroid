package com.wallia.eamxcomunidades.model.church


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class HoraryModelItem(
    var days: List<Day>,
    var hour_start: String,
    var hour_end: String
)