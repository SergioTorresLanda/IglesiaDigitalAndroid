package mx.arquidiocesis.eamxcommonutils.api.core.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EAMXUserModel(
        val username: String? = "",
        val email: String? = "",
        val phone_number: String? = "",
        val name: String? = "",
        val last_name: String? = "",
        val middle_name: String? = "",
        val password: String? = "",
        val role: String? = ""
) : Parcelable

