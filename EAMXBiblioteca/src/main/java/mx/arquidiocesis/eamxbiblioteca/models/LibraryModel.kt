package mx.arquidiocesis.eamxbiblioteca.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LibraryModel
    (var id: Int,
    var image: String,
    var title: String,
    var subtitle: String,
    var description: String,
    var resources: List<ResourcesModel>
) : Parcelable