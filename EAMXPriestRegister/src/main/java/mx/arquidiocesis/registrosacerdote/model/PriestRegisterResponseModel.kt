package mx.arquidiocesis.registrosacerdote.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PriestRegisterResponseModel(
    var status: String,
) : Parcelable