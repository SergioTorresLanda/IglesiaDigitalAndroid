package mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.model

import com.google.gson.annotations.SerializedName


class ConfirmPasswordModel(
        @SerializedName("username")
        val userName : String,
        val code : String,
        val password : String,
)



