package mx.arquidiocesis.eamxcommonutils.util.repositoryrefreshtoken.model

import com.google.gson.annotations.SerializedName

data class RefreshTokenRequest(
    @SerializedName("refresh_token")
    val token: String
)