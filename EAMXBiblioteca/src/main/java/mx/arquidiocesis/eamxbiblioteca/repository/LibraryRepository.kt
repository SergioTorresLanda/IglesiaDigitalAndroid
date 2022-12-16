package mx.arquidiocesis.eamxbiblioteca.repository

import androidx.lifecycle.MutableLiveData
import mx.arquidiocesis.eamxbiblioteca.models.ContentCategoryResponse
import mx.arquidiocesis.eamxbiblioteca.models.SearchResponse
import mx.arquidiocesis.eamxbiblioteca.models.HomeResponse
import mx.arquidiocesis.eamxbiblioteca.retrofit.RetrofitClientLibrary
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import mx.arquidiocesis.eamxbiblioteca.models.LibraryModel
import mx.arquidiocesis.eamxbiblioteca.config.WebConfi
import java.lang.Exception

class LibraryRepository {
    val homeResponse = MutableLiveData<HomeResponse>()
    val searchResponse = MutableLiveData<List<SearchResponse>>()
    val responseContentCategory = MutableLiveData<ContentCategoryResponse>()
    val errorResponse = MutableLiveData<String>()
    val libraryDetailResponse = MutableLiveData<LibraryModel>()

    fun obtenerDetalle(id: Int): MutableLiveData<LibraryModel> {
        val call = RetrofitClientLibrary(WebConfi.HOST).apiInterface.getDetalle(id)
        call.enqueue(object : Callback<LibraryModel> {
            override fun onFailure(call: Call<LibraryModel>, t: Throwable) {
                errorResponse.value = "Error Appointments : $t"
            }

            override fun onResponse(
                call: Call<LibraryModel>,
                response: Response<LibraryModel>
            ) {
                try {
                    val data = response.body()
                    libraryDetailResponse.value = data
                } catch (error: Exception) {
                    errorResponse.value = "Error: $error"
                }
            }
        })

        return libraryDetailResponse
    }


    fun getHomeContent() {
        val call = RetrofitClientLibrary(WebConfi.HOST).apiInterface.getContentHome()
        call.enqueue(object : Callback<HomeResponse> {
            override fun onResponse(call: Call<HomeResponse>, response: Response<HomeResponse>) {
                if (response.code() == 200) {
                    homeResponse.value = response.body()
                } else {
                    errorResponse.value = response.message()
                }
            }

            override fun onFailure(call: Call<HomeResponse>, t: Throwable) {
                errorResponse.value = "Error Appointments : $t"
            }
        })
    }

    fun getLibrarySearch(tag: String) {
        val call = RetrofitClientLibrary(WebConfi.HOST).apiInterface.getLibrarySearch(tag)
        call.enqueue(object : Callback<List<SearchResponse>> {
            override fun onResponse(
                call: Call<List<SearchResponse>>,
                response: Response<List<SearchResponse>>
            ) {
                if (response.code() == 200) {
                    searchResponse.value = response.body()
                } else {
                    errorResponse.value = response.message()
                }
            }

            override fun onFailure(call: Call<List<SearchResponse>>, t: Throwable) {
                errorResponse.value = "Error Appointments : $t"
            }
        })
    }

    fun getContentCategory(
        userId: String,
        code: String,
        tagText: String,
        order: String,
        topic: Boolean,
        age: String
    ) {
        val call = RetrofitClientLibrary(WebConfi.HOST).apiInterface.getContentCategory(userId, code, if (tagText != "") tagText else null, if (order != "") order else null , topic, if (age != "") age else null)
        call.enqueue(object : Callback<ContentCategoryResponse> {
            override fun onResponse(
                call: Call<ContentCategoryResponse>,
                response: Response<ContentCategoryResponse>
            ) {
                if (response.code() == 200) {
                    responseContentCategory.value = response.body()
                } else {
                    errorResponse.value = "Error Appointments : ${response.errorBody()?.string()}"
                }
            }

            override fun onFailure(call: Call<ContentCategoryResponse>, t: Throwable) {
                errorResponse.value = "Error Appointments : $t"
            }
        })
    }

    fun getDefaultContentCategory(code: String) {
        val call = RetrofitClientLibrary(WebConfi.HOST).apiInterface.getDefaultContentCategory(code)
        call.enqueue(object : Callback<ContentCategoryResponse> {
            override fun onResponse(
                call: Call<ContentCategoryResponse>,
                response: Response<ContentCategoryResponse>
            ) {
                if (response.code() == 200) {
                    responseContentCategory.value = response.body()
                } else {
                    errorResponse.value = "Error Appointments : ${response.errorBody()?.string()}"
                }
            }

            override fun onFailure(call: Call<ContentCategoryResponse>, t: Throwable) {
                errorResponse.value = "Error Appointments : $t"
            }
        })
    }
}