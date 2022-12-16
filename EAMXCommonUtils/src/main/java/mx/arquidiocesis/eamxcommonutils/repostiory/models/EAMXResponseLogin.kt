package mx.arquidiocesis.eamxcommonutils.repostiory.models


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class EAMXResponseLogin(
    @SerializedName("AuthenticationResult")
    @Expose
    var authenticationResult: EAMXAuthenticationResult,
    @SerializedName("UserAttributes")
    @Expose
    var userAttributes: UserAttributes
) {
    data class UserAttributes(
        @SerializedName("email")
        @Expose
        var email: String,
        @SerializedName("id")
        @Expose
        var id: Int,
        @SerializedName("last_name")
        @Expose
        var lastName: String,
        @SerializedName("middle_name")
        @Expose
        var middleName: String,
        @SerializedName("name")
        @Expose
        var name: String,
        @SerializedName("phone_number")
        @Expose
        var phoneNumber: String,
        @SerializedName("profile")
        @Expose
        var profile: String,
        @SerializedName("role")
        @Expose
        var role: String
    )
}
data class EAMXAuthenticationResult(
    @SerializedName("AccessToken")
    @Expose
    var accessToken: String,
    @SerializedName("ExpiresIn")
    @Expose
    var expiresIn: Int,
    @SerializedName("IdToken")
    @Expose
    var idToken: String,
    @SerializedName("RefreshToken")
    @Expose
    var refreshToken: String,
    @SerializedName("TokenType")
    @Expose
    var tokenType: String
)
