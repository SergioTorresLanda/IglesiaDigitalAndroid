package mx.arquidiocesis.eamxbiblioteca.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mx.arquidiocesis.eamxbiblioteca.repository.LibraryRepository
import java.lang.IllegalArgumentException

class LibraryViewModelFactory(val repository: LibraryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
            return LibraryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}