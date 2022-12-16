package mx.arquidiocesis.registrosacerdote.model.catalog

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataWithDescription(
    @SerializedName("descripcion")
    val description: String,
    val id: Int
) : Parcelable