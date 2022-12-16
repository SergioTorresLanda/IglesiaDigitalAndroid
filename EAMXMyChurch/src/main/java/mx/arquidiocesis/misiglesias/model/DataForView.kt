package mx.arquidiocesis.misiglesias.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import mx.arquidiocesis.eamxcommonutils.common.EAMXProfile

@Parcelize
class DataForView(
    val userId : Int,
    val profile: EAMXProfile,
    val mapInfo : Map<Int, String>,
    val mapVisible : Map<Int, Int>) : Parcelable