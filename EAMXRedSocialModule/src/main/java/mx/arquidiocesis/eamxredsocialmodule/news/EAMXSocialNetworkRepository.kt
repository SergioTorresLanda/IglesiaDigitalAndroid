package mx.arquidiocesis.eamxredsocialmodule.news

import androidx.lifecycle.Observer
import mx.arquidiocesis.eamxcommonutils.api.core.EAMXInitServices
import mx.arquidiocesis.eamxcommonutils.api.core.EAMXValidResponse
import mx.arquidiocesis.eamxcommonutils.api.core.response.EAMXGenericResponse
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxredsocialmodule.model.EAMXPublicationsAllRequest
import mx.arquidiocesis.eamxredsocialmodule.model.EAMXPublicationsAllResponse
import mx.arquidiocesis.eamxredsocialmodule.model.like.EAMXLikeRequest
import mx.arquidiocesis.eamxredsocialmodule.model.like.EAMXLikeResponse
import retrofit2.Call

class EAMXSocialNetworkRepository {
    fun callPublicationsAll(request: EAMXPublicationsAllRequest, observer: Observer<EAMXGenericResponse<EAMXPublicationsAllResponse, String, EAMXPublicationsAllRequest>>) {
        val initService: EAMXInitServices<EAMXPublicationsAllRequest, Call<EAMXPublicationsAllResponse>> = EAMXInitServices(AppMyConstants.BASE_NEWS_URL)
        EAMXValidResponse(observer, request, EAMXPublicationsAllResponse::class).validationMethod(initService.postExecuteService(request, AppMyConstants.PUBLICATIONS_ALL_END_POINT))
    }

    fun reactionLike(request: EAMXLikeRequest, observer: Observer<EAMXGenericResponse<EAMXLikeResponse, String, EAMXLikeRequest>>) {
        val initService: EAMXInitServices<EAMXLikeRequest, Call<EAMXLikeResponse>> = EAMXInitServices(AppMyConstants.BASE_NEWS_URL)
        EAMXValidResponse(observer, request, EAMXLikeResponse::class).validationMethod(initService.postExecuteService(request, AppMyConstants.REACTPUBLICATION_END_POINT))
    }
}