package mx.arquidiocesis.oraciones.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.oraciones.repository.Repository

class OracionViewModel(private val repository: Repository) : ViewModel() {

    val tipoResponse = repository.tipoOracionResponse
    var expandibleInSearch: Boolean = false
    var errorResponse = repository.errorResponse
    var buscarResponse = repository.buscarOracionResponse

    /*fun oracionesList(type: String) {
        repository.oracionesList(type)
    }*/
    fun oracionesBuscar(type: String) {
        expandibleInSearch = type.isNotEmpty()
        if (expandibleInSearch) {
            GlobalScope.launch {
                repository.oracionesBuscar(type)
            }
        } else {
            GlobalScope.launch(Dispatchers.IO) {
                oracionTipo()
            }
        }
    }

    suspend fun oracionTipo() {
        expandibleInSearch = false
        repository.oracionesTipo()
    }
}