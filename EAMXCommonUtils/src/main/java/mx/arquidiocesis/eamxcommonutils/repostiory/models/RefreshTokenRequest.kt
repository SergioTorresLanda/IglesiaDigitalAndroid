package mx.arquidiocesis.eamxcommonutils.repostiory.models

import com.google.gson.annotations.SerializedName

data class EAMXRefreshTokenRequest(
    @SerializedName("refresh_token")
    val token: String
)