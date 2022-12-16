package mx.arquidiocesis.registrosacerdote.model.catalog

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InterestTopic(
    @Expose
    val id: Int
):Parcelable