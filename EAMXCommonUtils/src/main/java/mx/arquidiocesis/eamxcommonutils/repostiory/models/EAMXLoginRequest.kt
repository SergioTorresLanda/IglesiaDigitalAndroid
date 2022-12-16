package mx.arquidiocesis.eamxcommonutils.repostiory.models

import com.google.gson.annotations.SerializedName

data class EAMXLoginRequest(
    @SerializedName("username")
    val user: String,
    @SerializedName("password")
    val pas4sd: String
)