package mx.arquidiocesis.eamxbiblioteca.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
): Parcelable