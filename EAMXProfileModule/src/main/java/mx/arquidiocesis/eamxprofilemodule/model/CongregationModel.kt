package mx.arquidiocesis.eamxprofilemodule.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CongregationModel(
    @SerializedName("descripcion")
    var description: String? = null,
    val id: Int? = -1
) : Parcelable