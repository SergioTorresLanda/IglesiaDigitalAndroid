package mx.arquidiocesis.servicios.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PriestModel(
    val name: String,
    val priest_id: String
) : Parcelable