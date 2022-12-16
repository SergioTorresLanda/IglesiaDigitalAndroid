package mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship

import androidx.lifecycle.Observer
import mx.arquidiocesis.eamxcommonutils.api.core.EAMXInitServices
import mx.arquidiocesis.eamxcommonutils.api.core.EAMXValidResponse
import mx.arquidiocesis.eamxcommonutils.api.core.response.EAMXGenericResponse
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship.model.getpublitaion.EAMXGetInPublicationRequest
import mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship.model.getpublitaion.EAMXGetInPublicationResponse
import mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship.model.totallike.EAMXTotalReactionRequest
import mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship.model.totallike.EAMXTotalReactionResponse
import retrofit2.Call

class EAMXTotalReactionRepository {
    fun callTotalReaction(request: EAMXTotalReactionRequest, observer: Observer<EAMXGenericResponse<EAMXTotalReactionResponse, String, EAMXTotalReactionRequest>>) {
        val initService: EAMXInitServices<EAMXTotalReactionRequest, Call<EAMXTotalReactionResponse>> = EAMXInitServices(AppMyConstants.BASE_NEWS_URL)
        EAMXValidResponse(observer, request, EAMXTotalReactionResponse::class).validationMethod(initService.postExecuteService(request, AppMyConstants.REACTIONSTOP_END_POINT))
    }

    fun getInPublication(request: EAMXGetInPublicationRequest, observer: Observer<EAMXGenericResponse<EAMXGetInPublicationResponse, String, EAMXGetInPublicationRequest>>) {
        val initService: EAMXInitServices<EAMXGetInPublicationRequest, Call<EAMXGetInPublicationResponse>> = EAMXInitServices(AppMyConstants.BASE_NEWS_URL)
        EAMXValidResponse(observer, request, EAMXGetInPublicationResponse::class).validationMethod(initService.postExecuteService(request, AppMyConstants.REACTIONS_IN_PUBLICATION_END_POINT))
    }
}