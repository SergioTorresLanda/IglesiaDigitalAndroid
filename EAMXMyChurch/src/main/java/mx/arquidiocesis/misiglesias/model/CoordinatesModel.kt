package mx.arquidiocesis.misiglesias.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CoordinatesModel(var latitude:String, var longitude:String) : Parcelable