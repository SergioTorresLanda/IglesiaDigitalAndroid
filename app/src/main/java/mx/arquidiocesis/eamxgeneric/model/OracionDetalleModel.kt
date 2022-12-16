package mx.arquidiocesis.eamxgeneric.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import mx.arquidiocesis.oraciones.model.OracionModel

@Parcelize
data class OracionDetalleModel(  var id: Int,
                                 var name: String,
                                 var description: String,
                                 var image_url: String,
                                 var similars: List<OracionModel>) : Parcelable