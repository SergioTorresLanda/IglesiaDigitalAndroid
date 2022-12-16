package mx.arquidiocesis.servicios.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize


@Parcelize
data class IglesiasModel(
    var id:Int,
    var name: String,
    var image_url: String,
    var distance:String,
    var schedules:String
) :
    Parcelable
