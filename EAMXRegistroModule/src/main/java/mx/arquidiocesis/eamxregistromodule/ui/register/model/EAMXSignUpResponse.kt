package mx.arquidiocesis.eamxregistromodule.ui.register.model

data class EAMXSignUpResponse(
        val UserConfirmed: Boolean,
        val CodeDeliveryDetails: EAMXCodeDeliveryDetailsModel,
        val UserSub: String
)