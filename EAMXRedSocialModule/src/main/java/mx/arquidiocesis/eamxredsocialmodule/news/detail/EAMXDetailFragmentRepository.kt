package mx.arquidiocesis.eamxredsocialmodule.news.detail

import androidx.lifecycle.Observer
import mx.arquidiocesis.eamxcommonutils.api.core.EAMXInitServices
import mx.arquidiocesis.eamxcommonutils.api.core.EAMXValidResponse
import mx.arquidiocesis.eamxcommonutils.api.core.response.EAMXGenericResponse
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxredsocialmodule.model.like.EAMXLikeRequest
import mx.arquidiocesis.eamxredsocialmodule.model.like.EAMXLikeResponse
import retrofit2.Call

class EAMXDetailFragmentRepository {

    fun reactionLike(request: EAMXLikeRequest, observer: Observer<EAMXGenericResponse<EAMXLikeResponse, String, EAMXLikeRequest>>) {
        val initService: EAMXInitServices<EAMXLikeRequest, Call<EAMXLikeResponse>> = EAMXInitServices(AppMyConstants.BASE_NEWS_URL)
        EAMXValidResponse(observer, request, EAMXLikeResponse::class).validationMethod(initService.postExecuteService(request, AppMyConstants.REACTPUBLICATION_END_POINT))
    }
}