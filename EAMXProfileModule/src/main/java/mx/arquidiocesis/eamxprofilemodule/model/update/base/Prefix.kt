package mx.arquidiocesis.eamxprofilemodule.model.update.base

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Prefix(
    @Expose
    val id: Int
):Parcelable