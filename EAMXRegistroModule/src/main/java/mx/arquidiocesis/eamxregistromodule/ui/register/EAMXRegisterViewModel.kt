package mx.arquidiocesis.eamxregistromodule.ui.register

import android.content.Intent
import androidx.lifecycle.*
import mx.arquidiocesis.eamxcommonutils.api.core.request.EAMXGenericRequest
import mx.arquidiocesis.eamxcommonutils.api.core.response.EAMXGenericResponse
import mx.arquidiocesis.eamxcommonutils.api.core.status.EAMXStatusRequestEnum
import mx.arquidiocesis.eamxcommonutils.application.EAMXGenericMutableLiveData
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXRequestWithValidation
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXValidationModel
import mx.arquidiocesis.eamxcommonutils.common.EAMXPutExtraModel
import mx.arquidiocesis.eamxregistromodule.ui.register.model.EAMXSignUpRequest
import mx.arquidiocesis.eamxregistromodule.ui.register.model.EAMXSignUpResponse

class EAMXRegisterViewModel : ViewModel() {
    private val repository = EAMXRegisterRepository()
    private val coordinatorRegister = EAMXRegisterCoordinator()
    val responseGeneric: EAMXGenericRequest<EAMXGenericResponse<EAMXSignUpResponse, String, EAMXSignUpRequest>> = EAMXGenericRequest()
    val openLoginFromActivity: MutableLiveData<EAMXPutExtraModel> = MutableLiveData()
    val validationDataActionFromActivity: EAMXGenericMutableLiveData<EAMXRequestWithValidation<EAMXSignUpRequest>> = EAMXGenericMutableLiveData()

    /**
    Metodo ejecutado desde EAMXRegisterActivity
     */
    fun requestSignUp(requestModel: EAMXSignUpRequest) {
        responseGeneric.postValue(EAMXGenericResponse(EAMXStatusRequestEnum.LOADING, requestData = requestModel))
        repository.callServiceSignUp(requestModel, observeSignUpResponse())
    }
    fun nextToView(requestCode: Int, resultCode: Int, data: Intent?) {
        coordinatorRegister.nextToView(requestCode, resultCode, data, observerOpenLoginFromActivity())
    }
    /**-------------------------------------------------------------------------------------*/

    /**
    Metodo ejecutado desde EAMXRegisterCodeExtend
     */
    fun requestValidationRegister(modelRequest: EAMXSignUpRequest, validationList: ArrayList<EAMXValidationModel>) {
        coordinatorRegister.signUpRequest(modelRequest, validationList, observeValidationIsValid())
    }
    /**-------------------------------------------------------------------------------------*/

    /**
    Metodos que notifican EAMXRegisterActivity
    */
    private fun observeValidationIsValid() = Observer<EAMXRequestWithValidation<EAMXSignUpRequest>> {
        validationDataActionFromActivity.postValue(it)
    }
    private fun observeSignUpResponse() = Observer<EAMXGenericResponse<EAMXSignUpResponse, String, EAMXSignUpRequest>> {
        responseGeneric.postValue(it)
    }
    private fun observerOpenLoginFromActivity() = Observer<EAMXPutExtraModel> {
        openLoginFromActivity.postValue(it)
    }
    /**-------------------------------------------------------------------------------------*/
}