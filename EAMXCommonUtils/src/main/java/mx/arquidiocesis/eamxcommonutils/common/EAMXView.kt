package mx.arquidiocesis.eamxcommonutils.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class EAMXView(
    val userId : Int,
    val rol: EAMXProfile,
    val mapInfo : Map<Int, String>,
    val mapVisible : Map<Int, Int>) : Parcelable