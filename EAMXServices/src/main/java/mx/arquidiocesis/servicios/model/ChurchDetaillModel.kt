package mx.arquidiocesis.servicios.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ChurchDetaillModel(
    val id: Int?,
    val name: String?,
    val image_url: String?,
    val description: String?,
    val address: String?,
    val latitude: String?,
    val longitude: String?,
    val email: String?,
    val phone: String?,
    val stream: String?,
    val bank_account: String?,
    val parson: ParsonModel?,
    val priests: List<PriestModel>?,
    val horary: List<HoraryModelItem>?,
    val attention: List<HoraryModelItem>?,
    val masses: List<MasseModel>?,
    val services: List<ServiceModel>?
) : Parcelable