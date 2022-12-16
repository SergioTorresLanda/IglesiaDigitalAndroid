package mx.arquidiocesis.oraciones.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.oraciones.repository.Repository

class OracionDetallesViewModel(private val repository: Repository) : ViewModel() {

    val response = repository.detalleResponse
    var errorResponse = repository.errorResponse

    fun obtenerDetalle(id:Int) {
        GlobalScope.launch {
            repository.obtenerDetalle(id)
        }
    }
}