package mx.arquidiocesis.eamxregistromodule.ui.register.model

data class EAMXSignUpRequest(
        val username: String? = null,
        val password: String? = null,
        val email: String? = null,
        val phone_number: String? = null,
        val name: String? = null,
        val last_name: String? = null,
        val middle_name: String? = null,
        val role: String? = null,
        val type_person: String? =  "1"
)

