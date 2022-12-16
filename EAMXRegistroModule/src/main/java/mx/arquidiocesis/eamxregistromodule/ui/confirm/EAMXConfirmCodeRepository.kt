package mx.arquidiocesis.eamxregistromodule.ui.confirm

import androidx.lifecycle.Observer
import mx.arquidiocesis.eamxcommonutils.api.core.EAMXInitServices
import mx.arquidiocesis.eamxcommonutils.api.core.EAMXValidResponse
import mx.arquidiocesis.eamxregistromodule.ui.confirm.model.EAMXConfirmCodeRequest
import retrofit2.Call
import mx.arquidiocesis.eamxcommonutils.api.core.response.EAMXGenericResponse
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxregistromodule.ui.confirm.model.EAMXConfirmCodeResponseModel
import mx.arquidiocesis.eamxregistromodule.ui.confirm.model.EAMXResendCodeRequest

class EAMXConfirmCodeRepository {

    fun callServiceConfirm(requestModel: EAMXConfirmCodeRequest, observer: Observer<EAMXGenericResponse<EAMXConfirmCodeResponseModel, String, EAMXConfirmCodeRequest>>) {
        val initService: EAMXInitServices<EAMXConfirmCodeRequest, Call<EAMXConfirmCodeResponseModel>> = EAMXInitServices()
        EAMXValidResponse(observer, requestModel, EAMXConfirmCodeResponseModel::class).validationMethod(initService.postExecuteService(requestModel, AppMyConstants.CONFIRM_CODE_END_POINT))
    }

    fun callServiceResend(requestModel: EAMXResendCodeRequest, observer: Observer<EAMXGenericResponse<Void, String, EAMXResendCodeRequest>>) {
        val initService: EAMXInitServices<EAMXResendCodeRequest, Call<Void>> = EAMXInitServices()
        EAMXValidResponse(observer, requestModel, Void::class).validationMethod(initService.postExecuteServiceVoid(requestModel, AppMyConstants.RESEND_CODE_END_POINT))
    }
}