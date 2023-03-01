package mx.arquidiocesis.eamxregistromodule.ui.register

import androidx.lifecycle.Observer
import mx.arquidiocesis.eamxcommonutils.api.core.EAMXInitServices
import mx.arquidiocesis.eamxcommonutils.api.core.EAMXValidResponse
import mx.arquidiocesis.eamxcommonutils.api.core.response.EAMXGenericResponse
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxregistromodule.ui.register.model.EAMXPriestRequest
import mx.arquidiocesis.eamxregistromodule.ui.register.model.EAMXPriestResponse
import mx.arquidiocesis.eamxregistromodule.ui.register.model.EAMXSignUpRequest
import mx.arquidiocesis.eamxregistromodule.ui.register.model.EAMXSignUpResponse
import retrofit2.Call

class EAMXPriestRepositoy {

    fun callServiceSignUp(
        requestModel: EAMXPriestRequest,
        observer: Observer<EAMXGenericResponse<EAMXPriestResponse, String, EAMXPriestRequest>>
    ) {
        val initService: EAMXInitServices<EAMXPriestRequest, Call<EAMXPriestResponse>> = EAMXInitServices()
        EAMXValidResponse(observer, requestModel, EAMXPriestResponse::class).validationMethod(
            initService.postExecuteService(requestModel, AppMyConstants.SIGN_UP_END_POINT)
        )
    }
}