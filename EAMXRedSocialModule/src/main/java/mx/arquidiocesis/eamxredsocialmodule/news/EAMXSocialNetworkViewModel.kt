package mx.arquidiocesis.eamxredsocialmodule.news

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.api.core.request.EAMXGenericRequest
import mx.arquidiocesis.eamxcommonutils.api.core.response.EAMXGenericResponse
import mx.arquidiocesis.eamxcommonutils.api.core.status.EAMXStatusRequestEnum
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxredsocialmodule.Repository.Repository
import mx.arquidiocesis.eamxredsocialmodule.model.EAMXPublicationsAllRequest
import mx.arquidiocesis.eamxredsocialmodule.model.EAMXPublicationsAllResponse
import mx.arquidiocesis.eamxredsocialmodule.model.MultimediaModel
import mx.arquidiocesis.eamxredsocialmodule.model.like.EAMXLikeRequest
import mx.arquidiocesis.eamxredsocialmodule.model.like.EAMXLikeResponse

class EAMXSocialNetworkViewModel(private val repositoryNew: Repository) : ViewModel() {
    private val repository = EAMXSocialNetworkRepository()
    val responseAllPost = repositoryNew.allPost
    val responsePost = repositoryNew.post
    val error = repositoryNew.errorResponse
    val userId = eamxcu_preferences.getData(
        EAMXEnumUser.USER_ID.name,
        EAMXTypeObject.INT_OBJECT
    ) as Int
    val responseGeneric: EAMXGenericRequest<EAMXGenericResponse<EAMXPublicationsAllResponse, String, EAMXPublicationsAllRequest>> =
        EAMXGenericRequest()
    val responseGenericLike: EAMXGenericRequest<EAMXGenericResponse<EAMXLikeResponse, String, EAMXLikeRequest>> =
        EAMXGenericRequest()

    fun requestPublicationsAll(requestModel: EAMXPublicationsAllRequest) {
        requestModel.dateEnd = EAMXPublicationsAllRequest.getDateLong()

        responseGeneric.postValue(
            EAMXGenericResponse(
                EAMXStatusRequestEnum.LOADING,
                requestData = requestModel
            )
        )
        repository.callPublicationsAll(requestModel, observeSocialNetworkResponse())
    }
    fun registrySOS(
        content: String,
        multimediaList: List<MultimediaModel>
    ) {


        var jsonObject: JsonObject = JsonObject()
        jsonObject.addProperty("userId", userId)
        jsonObject.addProperty("content", content)
        var multimediaArray=JsonArray()
        multimediaList.forEach {
            var multimedia: JsonObject = JsonObject()
            //   multimedia.addProperty("id", serviceId)
            multimedia.addProperty("url", it.url)
            multimedia.addProperty("thumbnail", it.thumnail)
            multimedia.addProperty("format", it.format)
            multimediaArray.add(multimedia)
        }
        jsonObject.add("multimedia", multimediaArray)
        jsonObject.addProperty("groupId", 20)
        jsonObject.addProperty("as", 0)
        GlobalScope.launch {
            repositoryNew.setPost(jsonObject)
        }
    }
    fun requestAllpost() {
        GlobalScope.launch {
            repositoryNew.getAllPost(userId)
        }
    }

    fun requestLike(requestModel: EAMXLikeRequest) {
        responseGenericLike.postValue(
            EAMXGenericResponse(
                EAMXStatusRequestEnum.LOADING,
                requestData = requestModel
            )
        )
        repository.reactionLike(requestModel, observeSocialLikeResponse())
    }

    fun loadMorePublications(
        isLoadingMore: Boolean,
        visibleItemCount: Int,
        totalItemCount: Int,
        firstVisibleItemPosition: Int,
        totalLastResult: Int,
        requestModel: EAMXPublicationsAllRequest,
        isLoadMoreListener: () -> Unit
    ) {

        if (isLoadingMore || totalLastResult == 0) {
            return
        }

        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= requestModel.limit) {
            requestModel.skip++
            isLoadMoreListener()
            requestModel.dateEnd = EAMXPublicationsAllRequest.getDateLong()
            responseGeneric.postValue(
                EAMXGenericResponse(
                    EAMXStatusRequestEnum.LOADING,
                    requestData = requestModel
                )
            )
            repository.callPublicationsAll(requestModel, observeSocialNetworkResponse())
        }
    }

    private fun observeSocialNetworkResponse() =
        Observer<EAMXGenericResponse<EAMXPublicationsAllResponse, String, EAMXPublicationsAllRequest>> {
            responseGeneric.postValue(it)
        }

    private fun observeSocialLikeResponse() =
        Observer<EAMXGenericResponse<EAMXLikeResponse, String, EAMXLikeRequest>> {
            responseGenericLike.postValue(it)
        }
}