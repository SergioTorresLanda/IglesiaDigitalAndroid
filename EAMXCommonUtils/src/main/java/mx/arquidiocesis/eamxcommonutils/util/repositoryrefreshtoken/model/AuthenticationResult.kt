package mx.arquidiocesis.eamxcommonutils.util.repositoryrefreshtoken.model

data class AuthenticationResult(
    val AccessToken: String,
    val ExpiresIn: Int,
    val TokenType: String,
    val RefreshToken: String,
    val IdToken: String
)