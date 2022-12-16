package mx.arquidiocesis.servicios.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Masse(
    @SerializedName("day")
    val day: String,
    @SerializedName("schedules")
    val schedules: List<Schedule>
): Parcelable