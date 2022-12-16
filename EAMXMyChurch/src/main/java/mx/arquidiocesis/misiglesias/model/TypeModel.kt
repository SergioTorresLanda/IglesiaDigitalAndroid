package mx.arquidiocesis.misiglesias.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TypeModel(
    val id: Int,
    val name: String,
    val image_url:String?=null
): Parcelable