package mx.arquidiocesis.eamxbiblioteca.viewmodel

import androidx.lifecycle.ViewModel
import mx.arquidiocesis.eamxbiblioteca.repository.LibraryRepository

class LibraryDetailViewModel(private val repository: LibraryRepository) : ViewModel() {
    val errorResponse = repository.errorResponse
    val response=repository.libraryDetailResponse
    fun obtenerDetalle(id:Int) {
        repository.obtenerDetalle(id)
    }
}