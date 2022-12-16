package mx.arquidiocesis.misiglesias.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PrincipalModel(
    val name: String,
    val id: Int
) : Parcelable