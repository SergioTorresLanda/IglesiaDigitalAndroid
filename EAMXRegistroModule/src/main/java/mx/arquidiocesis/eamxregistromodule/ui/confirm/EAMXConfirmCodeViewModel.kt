package mx.arquidiocesis.eamxregistromodule.ui.confirm

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import mx.arquidiocesis.eamxcommonutils.api.core.request.EAMXGenericRequest
import mx.arquidiocesis.eamxcommonutils.api.core.response.EAMXGenericResponse
import mx.arquidiocesis.eamxcommonutils.api.core.status.EAMXStatusRequestEnum
import mx.arquidiocesis.eamxregistromodule.ui.confirm.model.EAMXConfirmCodeRequest
import mx.arquidiocesis.eamxregistromodule.ui.confirm.model.EAMXConfirmCodeResponseModel
import mx.arquidiocesis.eamxregistromodule.ui.confirm.model.EAMXResendCodeRequest
import okhttp3.ResponseBody
import org.json.JSONObject

class EAMXConfirmCodeViewModel: ViewModel() {

    private val confirmCodeRepository = EAMXConfirmCodeRepository()
    val responseGeneric: EAMXGenericRequest<EAMXGenericResponse<EAMXConfirmCodeResponseModel, String, EAMXConfirmCodeRequest>> = EAMXGenericRequest()
    val responseGenericResendRequest: EAMXGenericRequest<EAMXGenericResponse<Void, String, EAMXResendCodeRequest>> = EAMXGenericRequest()

    fun requestCodeConfirm(requestModel: EAMXConfirmCodeRequest) {
        responseGeneric.postValue(EAMXGenericResponse(EAMXStatusRequestEnum.LOADING, requestData = requestModel))
        confirmCodeRepository.callServiceConfirm(requestModel, observerCodeResponse())
    }

    fun requestResendCode(requestModel: EAMXResendCodeRequest) {
        responseGenericResendRequest.postValue(EAMXGenericResponse(EAMXStatusRequestEnum.LOADING, requestData = requestModel))
        confirmCodeRepository.callServiceResend(requestModel, observerResendCode())
    }

    private fun observerCodeResponse() = Observer<EAMXGenericResponse<EAMXConfirmCodeResponseModel, String,EAMXConfirmCodeRequest>> {
        responseGeneric.postValue(it)
    }

    private fun observerResendCode() = Observer<EAMXGenericResponse<Void, String,EAMXResendCodeRequest>> {
        responseGenericResendRequest.postValue(it)
    }
}