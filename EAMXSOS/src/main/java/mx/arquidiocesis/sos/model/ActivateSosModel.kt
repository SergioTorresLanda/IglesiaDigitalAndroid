package mx.arquidiocesis.sos.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ActivateSosModel(
    var active: Boolean
) : Parcelable