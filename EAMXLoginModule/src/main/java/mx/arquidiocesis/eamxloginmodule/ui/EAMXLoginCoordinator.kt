package mx.arquidiocesis.eamxloginmodule.ui

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXRequestWithValidation
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXValidHelper
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXValidationModel
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnums
import mx.arquidiocesis.eamxcommonutils.common.EAMXPutExtraModel
import mx.arquidiocesis.eamxcommonutils.util.repositoryrefreshtoken.model.EAMXUserLoginRequest

class EAMXLoginCoordinator {
    fun signIn(modelRequest: EAMXUserLoginRequest, validation: ArrayList<EAMXValidationModel>, observer: Observer<EAMXRequestWithValidation<EAMXUserLoginRequest>>) {
        val resultValidation = EAMXValidHelper(modelRequest).helperValidation(validation)
        observer.onChanged(resultValidation)
    }

    fun nextToView(requestCode: Int, resultCode: Int, data: Intent?, observer: Observer<EAMXPutExtraModel>) {
        if (requestCode == EAMXEnums.CONFIRM_CODE.code) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    observer.onChanged(EAMXPutExtraModel(it, data.getBooleanExtra(EAMXEnums.CONFIRMATED.name, false)))
                }
            } else {
                observer.onChanged(EAMXPutExtraModel(data,false))
            }
        }
    }
}