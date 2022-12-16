
package mx.arquidiocesis.eamxcommonutils.util.repositoryrefreshtoken

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.customui.loader.UtilLoader
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxcommonutils.retrofit.model.dataclass.ResponseData
import mx.arquidiocesis.eamxcommonutils.retrofit.model.exception.TokenCodeException
import mx.arquidiocesis.eamxcommonutils.util.repositoryrefreshtoken.RefreshTokenRepository

open class ViewModelTokenRefresh(context : Context) : ViewModel(){

    val repository = RefreshTokenRepository(context)

    suspend fun validErrorToken (exception: Exception) {
        if (exception is TokenCodeException) {
            repository.getRefreshToken()
        }
    }
}