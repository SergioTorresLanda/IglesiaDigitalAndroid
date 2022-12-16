package mx.arquidiocesis.eamxcommonutils.util.Repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.application.ConstansApp
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.model.ReviewListModel
import mx.arquidiocesis.eamxcommonutils.model.ReviewModel
import mx.arquidiocesis.eamxcommonutils.model.ReviewRespondeModel
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.viewModel.DELETE
import mx.arquidiocesis.eamxcommonutils.util.viewModel.UPDATE

class ReviewRepository(val context: Context) : ManagerCall() {
    val errorResponse = MutableLiveData<String>()
    val comentarioResponse = MutableLiveData<ReviewRespondeModel>()
    val comentarioListResponse = MutableLiveData<ReviewListModel>()
    val reviewDeleteResponse = MutableLiveData<ReviewRespondeModel>()
    val reviewUpdateResponse = MutableLiveData<ReviewRespondeModel>()

    val userId = eamxcu_preferences.getData(
        EAMXEnumUser.USER_ID.name,
        EAMXTypeObject.INT_OBJECT
    ) as Int

    private val retrofitInstance = RetrofitApp.Build<ApiInterfaceReview>()
        .setContext(context)
        .setEnvironment(true)
        .setClass(ApiInterfaceReview::class.java)


    suspend fun putComentrio(location: Int, jsonObject: JsonObject) {

        managerCallApi(
            call = {
                retrofitInstance
                    .setHost(ConstansApp.hostEncuentro())
                    .builder().instance().putComentario(userId, location, jsonObject).await()
            }, validation = ValidationReview()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    comentarioResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getComentariosList(location: Int, page: Int) {
        managerCallApi(
            call = {
                retrofitInstance
                    .setHost(ConstansApp.hostEncuentro())
                    .builder().instance().getComentariosList(userId, location, page, 20).await()
            },
            validation = ValidationReview()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    comentarioListResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun updateComentrio(location: Int, review: Int, jsonObject: JsonObject) {
        managerCallApi(
            call = {
                retrofitInstance
                    .setHost(ConstansApp.hostEncuentro())
                    .builder().instance().updateComentario(userId, location, review, jsonObject)
                    .await()
            }, validation = ValidationReview()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    reviewUpdateResponse.value =  response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun deleteComentrio(location: Int, review: Int) {
        managerCallApi(
            call = {
                retrofitInstance
                    .setHost(ConstansApp.hostEncuentro())
                    .builder().instance().deleteComentario(userId, location, review).await()
            }, validation = ValidationReview()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    reviewDeleteResponse.value =  response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }
}