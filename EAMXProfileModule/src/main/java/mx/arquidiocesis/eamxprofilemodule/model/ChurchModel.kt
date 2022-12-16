package mx.arquidiocesis.eamxprofilemodule.model

import com.google.gson.annotations.SerializedName

data class ChurchModel(
    val id: Int,
    val name:String,
    val address: String,
    @SerializedName("image_url")
    val imageUrl: String?,
    val latitude: String?,
    val longitude: String?,
    val category: String?=null
)