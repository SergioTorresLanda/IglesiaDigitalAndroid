package mx.arquidiocesis.misiglesias.viewmodel

import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.misiglesias.model.ChurchDetaillModel
import mx.arquidiocesis.misiglesias.repository.Repository

class DetalleIglesiaViewModel(private val repository: Repository) : ViewModel() {

    val response = repository.detalleResponse
    val responseFavotritas = repository.allChurchList
    var errorResponse = repository.errorResponse
    var errorExitScreenResponse = repository.errorExitScreenResponse
    var favResponse = repository.favResponse
    var delResponse = repository.delResponse

    val userId : Int by lazy {
        eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID.name,
            EAMXTypeObject.INT_OBJECT) as Int
    }

    fun obtenerDetalle(id: Int) {
        GlobalScope.launch {
            repository.obtenerDetalle(id)
        }
    }

    fun favoritasList() {
        GlobalScope.launch {
            repository.iglesiasList(userId)
        }
    }

    fun setFavoritas(church : ChurchDetaillModel, isPrincipal: Boolean) {
        GlobalScope.launch {
            repository.setFavorita(church, isPrincipal, userId)
        }
    }

    fun delFavoritas(id: Int, idIglecia: Int) {
        GlobalScope.launch {
            repository.delFavorita(id, idIglecia)
        }
    }
}