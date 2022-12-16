package mx.arquidiocesis.servicios.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HoraryTypeModel(
    val days: List<Day>,
    @SerializedName("hour_start")
    val hourStart: String?,
    @SerializedName("hour_end")
    val hourEnd: String?
): Parcelable