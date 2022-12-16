package mx.arquidiocesis.eamxgeneric.repository

import androidx.lifecycle.MutableLiveData
import mx.arquidiocesis.eamxgeneric.model.*
import mx.arquidiocesis.eamxgeneric.retrofit.MainRetrofitClient
import mx.arquidiocesis.eamxprofilemodule.model.userdetail.UserResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainRepository {
    val sendTokenResponse = MutableLiveData<Response<ResponseBody>>()
    val deleteTokenResponse = MutableLiveData<Response<ResponseBody>>()
    val dataHomeReleaseResponse = MutableLiveData<Response<List<DataHomeReleaseResponse>>>()
    val dataHomeSaintResponse = MutableLiveData<Response<List<DataHomeSaintResponse>>>()
    val dataHomeSuggestionResponse = MutableLiveData<Response<List<SuggestionModel>>>()
    val prayResponse = MutableLiveData<Response<OracionDetalleModel>>()
    val errorResponse = MutableLiveData<String>()

    fun sendToken(id: Int, tokenObj: TokenObj) {
        val call = MainRetrofitClient.MAIN_API_INTERFACE.sendToken(id, tokenObj)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                sendTokenResponse.value = response
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                errorResponse.value = "Error Appointments : $t"
            }
        })
    }

    fun deleteToken(tokenObj: String) {
        val call = MainRetrofitClient.MAIN_API_INTERFACE.deleteToken(tokenObj)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                deleteTokenResponse.value = response
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                errorResponse.value = "Error Appointments : $t"
            }
        })
    }

    fun getHomeRelease(id: Int, type: String, starting_date: String) {
        val call =
            MainRetrofitClient.MAIN_API_INTERFACE_HOME.getHomeRelease(id, type, starting_date)
        call.enqueue(object : Callback<List<DataHomeReleaseResponse>> {
            override fun onResponse(
                call: Call<List<DataHomeReleaseResponse>>,
                response: Response<List<DataHomeReleaseResponse>>
            ) {
                dataHomeReleaseResponse.postValue(response)
            }

            override fun onFailure(call: Call<List<DataHomeReleaseResponse>>, t: Throwable) {
                errorResponse.value = "Error Appointments : $t"
            }
        })
    }

    fun getHomeSaint(id: Int, type: String, startingDate: String) {
        val call = MainRetrofitClient.MAIN_API_INTERFACE_HOME.getHomeSaint(id, type, startingDate)
        call.enqueue(object : Callback<List<DataHomeSaintResponse>> {
            override fun onResponse(
                call: Call<List<DataHomeSaintResponse>>,
                response: Response<List<DataHomeSaintResponse>>
            ) {
                dataHomeSaintResponse.postValue(response)
            }

            override fun onFailure(call: Call<List<DataHomeSaintResponse>>, t: Throwable) {
                errorResponse.value = "Error Appointments : $t"
            }
        })
    }

    fun getHomeSuggestion(id: Int, type: String) {
        val call = MainRetrofitClient.MAIN_API_INTERFACE_HOME.getHomeSuggestion(id, type)
        call.enqueue(object : Callback<List<SuggestionModel>> {
            override fun onResponse(
                call: Call<List<SuggestionModel>>,
                response: Response<List<SuggestionModel>>
            ) {
                dataHomeSuggestionResponse.postValue(response)
            }

            override fun onFailure(call: Call<List<SuggestionModel>>, t: Throwable) {
                errorResponse.value = "Error Appointments : $t"
            }
        })
    }

    fun obtenerDetalle(idOracion: Int) {
        val call = MainRetrofitClient.MAIN_API_INTERFACE_HOME.getDetalleOracionesAsync(idOracion)
        call.enqueue(object : Callback<OracionDetalleModel> {
            override fun onResponse(
                call: Call<OracionDetalleModel>,
                response: Response<OracionDetalleModel>
            ) {
                prayResponse.postValue(response)
            }

            override fun onFailure(call: Call<OracionDetalleModel>, t: Throwable) {
                errorResponse.value = "Error Appointments : $t"
            }
        })
    }
}