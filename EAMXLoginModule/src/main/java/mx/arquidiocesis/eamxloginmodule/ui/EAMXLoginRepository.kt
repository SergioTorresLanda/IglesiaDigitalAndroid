package mx.arquidiocesis.eamxloginmodule.ui

import androidx.lifecycle.Observer
import mx.arquidiocesis.eamxcommonutils.api.core.EAMXInitServices
import mx.arquidiocesis.eamxcommonutils.api.core.EAMXValidResponse
import mx.arquidiocesis.eamxcommonutils.api.core.response.EAMXGenericResponse
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.util.repositoryrefreshtoken.model.EAMXUserLoginRequest
import mx.arquidiocesis.eamxcommonutils.util.repositoryrefreshtoken.model.UserResponse
import retrofit2.Call

class EAMXLoginRepository {
    fun callServiceSignIn(
        request: EAMXUserLoginRequest,
        observer: Observer<EAMXGenericResponse<UserResponse, String, EAMXUserLoginRequest>>
    ) {
        val initService: EAMXInitServices<EAMXUserLoginRequest, Call<UserResponse>> =
            EAMXInitServices()
        EAMXValidResponse(
            observer,
            request,
            UserResponse::class
        ).validationMethod(
            initService.postExecuteService(
                request,
                AppMyConstants.SIGN_IN_END_POINT
            )
        )
    }
}