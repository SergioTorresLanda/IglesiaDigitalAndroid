package mx.arquidiocesis.eamxprofilemodule.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataWithDescriptionCode(
    var description: String = "",
    val id: Int = -1,
    val code: String = ""
) : Parcelable