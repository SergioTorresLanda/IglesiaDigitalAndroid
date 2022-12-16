package mx.arquidiocesis.eamxbiblioteca.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeResponse(
    @SerializedName("categories")
    val categories: List<Category>,
    @SerializedName("featured")
    val featured: List<Featured>,
    @SerializedName("news")
    val news: List<New>
): Parcelable