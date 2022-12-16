package mx.arquidiocesis.misiglesias.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DevoteeModel(
    val id: Int?,
    val name: String?,
    val first_surname: String?,
    val second_surname: String?,
    val image_url: String?,
    ) : Parcelable