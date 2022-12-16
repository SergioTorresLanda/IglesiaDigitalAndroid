package mx.arquidiocesis.eamxredsocialmodule.news.detail

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import mx.arquidiocesis.eamxcommonutils.api.core.request.EAMXGenericRequest
import mx.arquidiocesis.eamxcommonutils.api.core.response.EAMXGenericResponse
import mx.arquidiocesis.eamxcommonutils.api.core.status.EAMXStatusRequestEnum
import mx.arquidiocesis.eamxredsocialmodule.model.like.EAMXLikeRequest
import mx.arquidiocesis.eamxredsocialmodule.model.like.EAMXLikeResponse

class EAMXDetailFragmentViewModel : ViewModel() {

    private val repository = EAMXDetailFragmentRepository()
    val responseGenericLike: EAMXGenericRequest<EAMXGenericResponse<EAMXLikeResponse, String, EAMXLikeRequest>> = EAMXGenericRequest()

    fun requestLike(requestModel: EAMXLikeRequest) {
        responseGenericLike.postValue(EAMXGenericResponse(EAMXStatusRequestEnum.LOADING, requestData = requestModel))
        repository.reactionLike(requestModel, observeSocialLikeResponse())
    }

    private fun observeSocialLikeResponse() =
        Observer<EAMXGenericResponse<EAMXLikeResponse, String, EAMXLikeRequest>> {
            responseGenericLike.postValue(it)
        }
}