package mx.arquidiocesis.misiglesias.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.misiglesias.repository.Repository

class MisIgleciasViewModel(private val repository: Repository) : ViewModel() {

    val allChurchList = repository.allChurchList
    val getIglesiasBusqueda = repository.getIglesiasBusqueda
    val suggestionList = repository.suggestionList
    val responseList = repository.iglesiasMapResponse
    val locationResponse = repository.locationResponse
    var errorResponse = repository.errorResponse

    fun iglesiasList() {
        val userId = eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID.name,
            EAMXTypeObject.INT_OBJECT
        ) as Int
        GlobalScope.launch {
            repository.iglesiasList(userId)
        }
    }
    fun getSuggestion() {
        GlobalScope.launch {
            repository.suggestions()
        }
    }
    fun getBuscarIglesias(iglesia: String) {
        GlobalScope.launch {
            repository.getIglesiasBusqueda(iglesia)
        }
    }

    fun getiglesiasList() {
        GlobalScope.launch {
            repository.getiglesiasList()
        }
    }

    fun descargaImagen(context: Context, list: String, id: Int) {
        repository.descargarImagen(context, list, id)
    }

    fun getLocation() {
        repository.getLocation()
    }
}