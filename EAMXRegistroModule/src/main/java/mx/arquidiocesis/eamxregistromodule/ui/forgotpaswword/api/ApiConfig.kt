package mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.api

import mx.arquidiocesis.eamxcommonutils.application.ConstansApp
import mx.arquidiocesis.eamxregistromodule.BuildConfig

object ApiConfig {
     val HOST : String = ConstansApp.hostUser();
     const val SEND_CODE : String = "user/forgot_password"
     const val CONFIRM_PASSWORD : String = "user/confirm_password"
     const val INFO_USER_BASIC : String = "user/info"
}