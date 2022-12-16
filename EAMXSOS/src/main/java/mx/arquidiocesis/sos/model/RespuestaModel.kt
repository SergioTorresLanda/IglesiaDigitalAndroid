package mx.arquidiocesis.sos.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class RespuestaModel(
    val service_id: Int,
    val status:String
): Parcelable