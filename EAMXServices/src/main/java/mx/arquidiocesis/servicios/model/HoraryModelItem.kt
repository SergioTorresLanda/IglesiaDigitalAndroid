package mx.arquidiocesis.servicios.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HoraryModelItem(
    val days: List<Day>,
    @SerializedName("hour_end")
    val hourEnd: String?,
    val hour_start: String
): Parcelable