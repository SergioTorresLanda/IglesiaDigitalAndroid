package mx.arquidiocesis.eamxloginmodule.ui

import android.content.Intent
import androidx.lifecycle.*
import mx.arquidiocesis.eamxcommonutils.api.core.request.EAMXGenericRequest
import mx.arquidiocesis.eamxcommonutils.api.core.response.EAMXGenericResponse
import mx.arquidiocesis.eamxcommonutils.api.core.status.EAMXStatusRequestEnum
import mx.arquidiocesis.eamxcommonutils.application.EAMXGenericMutableLiveData
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXRequestWithValidation
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXValidationModel
import mx.arquidiocesis.eamxcommonutils.common.EAMXPutExtraModel
import mx.arquidiocesis.eamxcommonutils.util.repositoryrefreshtoken.model.EAMXUserLoginRequest
import mx.arquidiocesis.eamxcommonutils.util.repositoryrefreshtoken.model.UserResponse

class EAMXLoginViewModel : ViewModel() {

    private val repository = EAMXLoginRepository()
    private val coordinatorRegister = EAMXLoginCoordinator()
    val responseGeneric: EAMXGenericRequest<EAMXGenericResponse<UserResponse, String, EAMXUserLoginRequest>> = EAMXGenericRequest()
    val openSplashFromActivity: MutableLiveData<EAMXPutExtraModel> = MutableLiveData()
    val validationDataActionFromActivity: EAMXGenericMutableLiveData<EAMXRequestWithValidation<EAMXUserLoginRequest>> = EAMXGenericMutableLiveData()


    /**
    Metodo ejecutado desde EAMXRegisterActivity
     */
    fun requestSignIn(requestModel: EAMXUserLoginRequest) {
        responseGeneric.postValue(EAMXGenericResponse(EAMXStatusRequestEnum.LOADING, requestData = requestModel))
        repository.callServiceSignIn(requestModel, observeSignInResponse())
    }
    fun nextToView(requestCode: Int, resultCode: Int, data: Intent?) {
        coordinatorRegister.nextToView(requestCode, resultCode, data, observerOpenSplashFromActivity())
    }
    /**-------------------------------------------------------------------------------------*/

    /**
    Metodo ejecutado desde EAMXRegisterCodeExtend
     */
    fun requestValidationSingIn(modelRequest: EAMXUserLoginRequest, validationList: ArrayList<EAMXValidationModel>) {
        coordinatorRegister.signIn(modelRequest, validationList, observeValidationIsValid())
    }
    /**-------------------------------------------------------------------------------------*/

    /**
    Metodos que notifican EAMXRegisterActivity
     */
    private fun observeValidationIsValid() = Observer<EAMXRequestWithValidation<EAMXUserLoginRequest>> {
        validationDataActionFromActivity.postValue(it)
    }
    private fun observeSignInResponse() = Observer<EAMXGenericResponse<UserResponse, String, EAMXUserLoginRequest>> {
        responseGeneric.postValue(it)
    }
    private fun observerOpenSplashFromActivity() = Observer<EAMXPutExtraModel> {
        openSplashFromActivity.postValue(it)
    }
    /**-------------------------------------------------------------------------------------*/
}