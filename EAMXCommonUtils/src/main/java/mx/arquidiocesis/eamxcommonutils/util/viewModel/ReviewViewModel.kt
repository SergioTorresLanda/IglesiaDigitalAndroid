package mx.arquidiocesis.eamxcommonutils.util.viewModel

import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.util.Repository.ReviewRepository
import retrofit2.http.DELETE

const val  UPDATE = "UPDATE"
const val  DELETE = "DELETE"

class ReviewViewModel(private val repository: ReviewRepository) : ViewModel() {
    var opinionResponse = repository.comentarioResponse
    var opinionListResponse = repository.comentarioListResponse
    var errorResponse = repository.errorResponse
    var reviewDeleteResponse = repository.reviewDeleteResponse
    var reviewUpdateResponse = repository.reviewUpdateResponse

    fun setComentario(locatio: Int, review: String, rating: Int) {
        var jsonObject: JsonObject = JsonObject()
        jsonObject.addProperty("review", review)
        jsonObject.addProperty("rating", rating)
        GlobalScope.launch {
            repository.putComentrio(locatio, jsonObject)
        }
    }

    fun getComentarios(location: Int, page: Int) {
        GlobalScope.launch {
            repository.getComentariosList(location, page)
        }
    }

    fun updateComentario(locatio: Int, idReview: Int, review: String, rating: Int) {
        var jsonObject: JsonObject = JsonObject()
        jsonObject.addProperty("review", review)
        jsonObject.addProperty("rating", rating)
        GlobalScope.launch {
            repository.updateComentrio(locatio, idReview, jsonObject)
        }
    }

    fun deleteComentario(locatio: Int, idReview: Int) {
        GlobalScope.launch {
            repository.deleteComentrio(locatio, idReview)
        }
    }
}