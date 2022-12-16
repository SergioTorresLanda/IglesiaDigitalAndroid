package mx.arquidiocesis.misiglesias.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RespuestaModel(
    var status: String
) : Parcelable