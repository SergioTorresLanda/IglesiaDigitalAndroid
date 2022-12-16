package mx.arquidiocesis.misiglesias.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CatServiceModel(var id: Int, var iconUrl: String, var name: String) :
    Parcelable//SERVICIOS