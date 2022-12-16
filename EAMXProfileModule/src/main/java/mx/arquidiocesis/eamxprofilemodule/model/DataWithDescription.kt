package mx.arquidiocesis.eamxprofilemodule.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataWithDescription(
    var description: String = "",
    val id: Int = -1
) : Parcelable