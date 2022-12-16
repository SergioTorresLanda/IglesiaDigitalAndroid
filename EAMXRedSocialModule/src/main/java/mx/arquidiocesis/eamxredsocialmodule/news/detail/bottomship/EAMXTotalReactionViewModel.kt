package mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import mx.arquidiocesis.eamxcommonutils.api.core.request.EAMXGenericRequest
import mx.arquidiocesis.eamxcommonutils.api.core.response.EAMXGenericResponse
import mx.arquidiocesis.eamxcommonutils.api.core.status.EAMXStatusRequestEnum
import mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship.model.getpublitaion.EAMXGetInPublicationRequest
import mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship.model.getpublitaion.EAMXGetInPublicationResponse
import mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship.model.totallike.EAMXTotalReactionRequest
import mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship.model.totallike.EAMXTotalReactionResponse

class EAMXTotalReactionViewModel : ViewModel() {

    private val repository = EAMXTotalReactionRepository()
    val responseGeneric: EAMXGenericRequest<EAMXGenericResponse<EAMXTotalReactionResponse, String, EAMXTotalReactionRequest>> = EAMXGenericRequest()
    val responseGenericTotalTop: EAMXGenericRequest<EAMXGenericResponse<EAMXGetInPublicationResponse, String, EAMXGetInPublicationRequest>> = EAMXGenericRequest()

    fun requestTotalReaction(requestModel: EAMXTotalReactionRequest) {
        responseGeneric.postValue(EAMXGenericResponse(EAMXStatusRequestEnum.LOADING, requestData = requestModel))
        repository.callTotalReaction(requestModel, observeTotalReactionResponse())
    }

    fun requestGetInPublication(requestModel: EAMXGetInPublicationRequest) {
        responseGenericTotalTop.postValue(EAMXGenericResponse(EAMXStatusRequestEnum.LOADING, requestData = requestModel))
        repository.getInPublication(requestModel, observeGetInPublicationResponse())
    }



    private fun observeTotalReactionResponse() =
        Observer<EAMXGenericResponse<EAMXTotalReactionResponse, String, EAMXTotalReactionRequest>> {
            responseGeneric.postValue(it)
        }

    private fun observeGetInPublicationResponse() =
        Observer<EAMXGenericResponse<EAMXGetInPublicationResponse, String, EAMXGetInPublicationRequest>> {
            responseGenericTotalTop.postValue(it)
        }
}