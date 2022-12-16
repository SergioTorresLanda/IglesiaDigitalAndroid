package mx.arquidiocesis.eamxredsocialmodule.news.create

import androidx.lifecycle.Observer
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXRequestWithValidation
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXValidHelper
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXValidationModel
import mx.arquidiocesis.eamxredsocialmodule.news.create.model.EAMXCreatePostRequest
import mx.arquidiocesis.eamxredsocialmodule.news.create.model.EAMXCreatePostResponse
import mx.arquidiocesis.eamxredsocialmodule.news.create.model.EAMXCretePostModel

class EAMXCreateCoordinator {

    fun createPostRequest(modelRequest: EAMXCreatePostRequest, validation: ArrayList<EAMXValidationModel>, observer: Observer<EAMXRequestWithValidation<EAMXCreatePostRequest>>) {
        val resultValidation = EAMXValidHelper(modelRequest).helperValidation(validation)
        observer.onChanged(resultValidation)
    }
}