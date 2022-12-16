package mx.arquidiocesis.servicios.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Schedule(
    @SerializedName("hour_start")
    val hour_start: String
): Parcelable