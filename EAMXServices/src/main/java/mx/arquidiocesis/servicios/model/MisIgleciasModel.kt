package mx.arquidiocesis.servicios.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MisIgleciasModel(
    val assigned: Assigned?,
    val locations: List<Location>?
) : Parcelable