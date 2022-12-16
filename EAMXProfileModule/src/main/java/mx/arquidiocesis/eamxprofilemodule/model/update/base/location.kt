package mx.arquidiocesis.eamxprofilemodule.model.update.base

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class location(
    @Expose
    val id: Int
):Parcelable