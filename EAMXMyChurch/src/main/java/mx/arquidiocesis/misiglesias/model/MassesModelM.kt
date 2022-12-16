package mx.arquidiocesis.misiglesias.model


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MassesModelM(
    var id:Int,
    var name: String,
    var imageUrl: String,
    var distance:String,
    var schedules:String
) :
    Parcelable//MISAS
