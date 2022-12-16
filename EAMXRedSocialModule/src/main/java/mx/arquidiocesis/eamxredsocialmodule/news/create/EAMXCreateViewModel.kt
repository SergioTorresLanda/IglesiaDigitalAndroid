package mx.arquidiocesis.eamxredsocialmodule.news.create

import android.content.Context
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import id.zelory.compressor.Compressor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import mx.arquidiocesis.eamxcommonutils.api.core.request.EAMXGenericRequest
import mx.arquidiocesis.eamxcommonutils.api.core.response.EAMXGenericResponse
import mx.arquidiocesis.eamxcommonutils.api.core.status.EAMXStatusRequestEnum
import mx.arquidiocesis.eamxcommonutils.application.EAMXGenericMutableLiveData
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXRequestWithValidation
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXValidationModel
import mx.arquidiocesis.eamxredsocialmodule.news.create.model.EAMXCreatePostRequest
import mx.arquidiocesis.eamxredsocialmodule.news.create.model.EAMXCreatePostResponse
import mx.arquidiocesis.eamxredsocialmodule.news.create.model.EAMXMultimedia
import java.io.File
import io.reactivex.rxjava3.core.Observable as RxObservable

class EAMXCreateViewModel : ViewModel() {

    companion object {
        const val ERROR_SEND_MULTIMEDIA = "ERROR_SEND_MULTIMEDIA"
    }

    private val repository = EAMXCreateRepository
    private val coordinator = EAMXCreateCoordinator()

    val responseGeneric: EAMXGenericRequest<EAMXGenericResponse<EAMXCreatePostResponse, String, EAMXCreatePostRequest>> =
        EAMXGenericRequest()

    val validationDataActionFromActivity: EAMXGenericMutableLiveData<EAMXRequestWithValidation<EAMXCreatePostRequest>> =
        EAMXGenericMutableLiveData()


    fun attemptCreatePost(context: Context?, requestModel: EAMXCreatePostRequest) {
        //Show loading
        responseGeneric.postValue(EAMXGenericResponse(EAMXStatusRequestEnum.LOADING, requestData = requestModel))

        // If images/video is empty
        if (requestModel.multimedia?.isEmpty() == true) {
            attemptStorePost(requestModel)
            return
        }

        // /storage/emulated/0/DCIM/Camera/IMG_20210408_205251.jpg
        attemptStoreMultimedia(context, requestModel) { haveError, error ->
            if (haveError) {
                responseGeneric.postValue(EAMXGenericResponse(EAMXStatusRequestEnum.FAILURE, requestData = requestModel, errorData = ERROR_SEND_MULTIMEDIA))
            } else {
                attemptStorePost(requestModel)
            }
        }
    }

    private fun attemptStorePost(requestModel: EAMXCreatePostRequest) {
        repository.callServiceCreatePost(requestModel, observeSocialNetworkResponse())
    }

    private fun observeSocialNetworkResponse() =
        Observer<EAMXGenericResponse<EAMXCreatePostResponse, String, EAMXCreatePostRequest>> {
            responseGeneric.postValue(it)
        }

    private fun attemptStoreMultimedia(
        context: Context?,
        requestModel: EAMXCreatePostRequest,
        cbResult: (success: Boolean, error: Any?) -> Unit
    ) {
        var haveError = false
        RxObservable.fromIterable(requestModel.multimedia)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { multimedia ->
                return@flatMap RxObservable.create<EAMXMultimedia> { emmiter ->
                    var file = File(multimedia.path)
                    if (multimedia.isImage()) {
                        file = tryCompressFile(context, file)
                    }
                    repository.storeImages(file, multimedia.displayName!!) { success, uri ->
                        emmiter.onNext(multimedia.apply {
                            if (success) url = uri.toString()
                        })
                        emmiter.onComplete()
                    }
                }
            }.subscribe({
                // onNext
            }, {
                it.printStackTrace()
                haveError = true
            }, {
                cbResult(haveError, null)
            })
    }

    private fun tryCompressFile(context: Context?, file: File): File {
        return if (context == null) {
            file
        } else {
            Compressor(context).compressToFile(file)
        }
    }

    //Validations

    fun requestValidationRegister(
        modelRequest: EAMXCreatePostRequest,
        validationList: ArrayList<EAMXValidationModel>
    ) {
        coordinator.createPostRequest(modelRequest, validationList, observeValidationIsValid())
    }

    private fun observeValidationIsValid() =
        Observer<EAMXRequestWithValidation<EAMXCreatePostRequest>> {
            validationDataActionFromActivity.postValue(it)
        }

}