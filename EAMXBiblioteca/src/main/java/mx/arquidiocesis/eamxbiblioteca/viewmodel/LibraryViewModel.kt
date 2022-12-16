package mx.arquidiocesis.eamxbiblioteca.viewmodel

import androidx.lifecycle.ViewModel
import mx.arquidiocesis.eamxbiblioteca.repository.LibraryRepository

class LibraryViewModel(private val repository: LibraryRepository) : ViewModel() {

    val homeResponse = repository.homeResponse
    val searchResponse = repository.searchResponse
    val responseContentCategory = repository.responseContentCategory
    val errorResponse = repository.errorResponse

    fun getHomeContent() {
        repository.getHomeContent()
    }

    fun getLibrarySearch(fing: String) {
        repository.getLibrarySearch(fing)
    }

    fun getContentCategory(
        userId: String,
        code: String,
        tagText: String,
        order: String,
        topic: Boolean,
        age: String
    ) {
        repository.getContentCategory(userId, code, tagText, order, topic, age)
    }

    fun getDefaultContentCategory(code: String) {
        repository.getDefaultContentCategory(code)
    }
}