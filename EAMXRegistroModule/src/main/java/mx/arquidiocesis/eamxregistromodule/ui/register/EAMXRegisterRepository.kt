package mx.arquidiocesis.eamxregistromodule.ui.register

import androidx.lifecycle.Observer
import mx.arquidiocesis.eamxcommonutils.api.core.EAMXInitServices
import mx.arquidiocesis.eamxcommonutils.api.core.EAMXValidResponse
import mx.arquidiocesis.eamxcommonutils.api.core.response.EAMXGenericResponse
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxregistromodule.ui.register.model.EAMXSignUpRequest
import mx.arquidiocesis.eamxregistromodule.ui.register.model.EAMXSignUpResponse
import retrofit2.Call

class EAMXRegisterRepository {

    fun callServiceSignUp(requestModel: EAMXSignUpRequest, observer: Observer<EAMXGenericResponse<EAMXSignUpResponse, String, EAMXSignUpRequest>>) {
        val initService: EAMXInitServices<EAMXSignUpRequest, Call<EAMXSignUpResponse>> = EAMXInitServices()
        EAMXValidResponse(observer, requestModel, EAMXSignUpResponse::class).validationMethod(initService.postExecuteService(requestModel, AppMyConstants.SIGN_UP_END_POINT))
    }
}