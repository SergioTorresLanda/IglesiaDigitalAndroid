package mx.arquidiocesis.oraciones.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OracionesDetalleModel(var id: Int,
                                 var name: String,
                                 var description: String,
                                 var image_url: String,
                                 var similars: List<OracionesModel>) : Parcelable