package mx.arquidiocesis.eamxbiblioteca.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mx.arquidiocesis.eamxbiblioteca.repository.LibraryRepository
import java.lang.IllegalArgumentException

class LibraryDetailViewModelFactory(val repository: LibraryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LibraryDetailViewModel::class.java)) {
            return LibraryDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}