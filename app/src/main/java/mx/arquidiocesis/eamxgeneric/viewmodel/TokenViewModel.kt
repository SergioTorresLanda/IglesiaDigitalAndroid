package mx.arquidiocesis.eamxgeneric.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxgeneric.model.TokenObj
import mx.arquidiocesis.eamxgeneric.repository.MainRepository2

class TokenViewModel(val repository: MainRepository2) : ViewModel() {

    val sendTokenResponse = repository.sendTokenResponse
    val deleteTokenResponse = repository.deleteTokenResponse
    val dataHomeReleaseResponse = repository.dataHomeReleaseResponse
    val dataHomeSaintResponse = repository.dataHomeSaintResponse
    val dataHomeSuggestionResponse = repository.dataHomeSuggestionResponse
    val prayResponse = repository.prayResponse
    val errorResponse = repository.errorResponse

    fun sendToken(id: Int, tokenObj: TokenObj) {
        GlobalScope.launch {
            repository.sendToken(id, tokenObj)
        }
    }

    fun deleteToken(tokenObj: String) {
        GlobalScope.launch {
            repository.deleteToken(tokenObj)
        }
    }

    fun getHomeRelease(id: Int, type: String, starting_date: String) {
        GlobalScope.launch {
            repository.getHomeRelease(id, type, starting_date)
        }
    }

    fun getHomeSaint(id: Int, type: String, starting_date: String) {
        GlobalScope.launch {
            repository.getHomeSaint(id, type, starting_date)
        }
    }

    fun getHomeSuggestion(id: Int, type: String){
        GlobalScope.launch {
            repository.getHomeSuggestion(id, type)
        }
    }

    fun getPrayDetail(id: Int) {
        GlobalScope.launch {
            repository.getPrayDetail(id)
        }
    }
}