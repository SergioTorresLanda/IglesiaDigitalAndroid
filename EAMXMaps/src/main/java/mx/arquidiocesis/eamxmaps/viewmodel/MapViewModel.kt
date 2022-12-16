package mx.arquidiocesis.eamxmaps.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxmaps.repository.Repository
import java.io.File

class MapViewModel(private val repository: Repository) : ViewModel() {

    val response = repository.iglesiasMapResponse
    val locationResponse = repository.locationResponse
    var errorResponse = repository.errorResponse
    fun getiglesiasList() {
        GlobalScope.launch {
            repository.getiglesiasList()
        }
    }

    fun getiglesiasList(name: String) {
        GlobalScope.launch {
            repository.getiglesiasList(name)
        }
    }
    fun getLocation() {
        repository.getLocation()
    }

}