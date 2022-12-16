package mx.arquidiocesis.eamxbiblioteca.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResourcesModel
    (var id: Int,
     var title: String,
     var description: String,
     var type: String,
     var url: String
) : Parcelable