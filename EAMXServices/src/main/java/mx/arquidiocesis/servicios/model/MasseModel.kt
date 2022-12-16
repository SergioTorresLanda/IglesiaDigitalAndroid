package mx.arquidiocesis.servicios.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MasseModel(
    val days: List<Day>,
    @SerializedName("hour_end")
    val hourEnd: String?
): Parcelable