package mx.arquidiocesis.eamxbiblioteca.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
@SerializedName("code")
    val code: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("name")
    val name: String
) : Parcelable