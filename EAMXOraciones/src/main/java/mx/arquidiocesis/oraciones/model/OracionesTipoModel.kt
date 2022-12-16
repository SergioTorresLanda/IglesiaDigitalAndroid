package mx.arquidiocesis.oraciones.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OracionesTipoModel(var id: Int, var name: String, var icon_url: String, var devotions :List<OracionesModel>) : Parcelable