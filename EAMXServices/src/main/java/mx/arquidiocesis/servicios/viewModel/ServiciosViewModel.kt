package mx.arquidiocesis.servicios.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import mx.arquidiocesis.servicios.model.MentionRequestPost
import mx.arquidiocesis.servicios.repository.Repository

class ServiciosViewModel(private val repository: Repository) : ViewModel() {

    val response = repository.serviciosResponse
    val responseSacraments = repository.sacramentsResponse
    val iglesiasResponse = repository.iglesiasResponse
    val churchesResponse = repository.churchesResponse
    val findChurchesResponse = repository.findChurchesResponse
    val churchDetailResponse = repository.churchDetailResponse
    val otherServicesResponse = repository.otherServicesResponse
    val blessigResponse = repository.blessigResponse
    val celebrationResponse = repository.celebrationResponse
    var errorResponse = repository.errorResponse
    var errorResponseExit = repository.errorResponseExit
    var massesScheduleResponse = repository.massesScheduleResponse
    var mentionResponse = repository.mentionResponse

    val intentionsResponse = repository.intentionsResponse
    val zipCodeResponse = repository.zipCodeResponse

    val mainCommunityResponse = repository.mainCommunityResponse
    val communitiesResponse = repository.communitiesResponse

    fun serviciosList() {
        GlobalScope.launch {
            repository.serviciosList()
        }
    }

    fun getSacramentsList() {
        GlobalScope.launch {
            repository.getSacramentsList()
        }
    }

    fun getOtherServices() {
        GlobalScope.launch {
            repository.getOtherServicesList()
        }
    }

    fun getCelebration() {
        GlobalScope.launch {
            repository.getCelebrationList()
        }
    }

    fun getBlessig() {
        GlobalScope.launch {
            repository.getBlessigList()
        }
    }

    fun getMyChurches(id: Int, esSacerdote: String) {
        GlobalScope.launch {
            repository.iglesiasList(id, esSacerdote)
        }
    }

    fun getAllChurches() {
        GlobalScope.launch {
            repository.getAllChurches("CHURCH")
        }
    }

    fun getChurchDetail(idChurch: Int) {
        GlobalScope.launch {
            repository.getChurchDetail(idChurch)
        }
    }

    fun getMassesSchedule(locationId: Int, type: String) {
        GlobalScope.launch {
            repository.getMassesSchedule(locationId, type)
        }
    }

    fun sendMention(mentionRequest: MentionRequestPost) {
        GlobalScope.launch {
            repository.sendMention(mentionRequest)
        }
    }

    fun getIntentions() {
        GlobalScope.launch {
            repository.getIntentions()
        }
    }

    fun getZipCode(code: String) {
        GlobalScope.launch {
            repository.getZipCode(code)
        }
    }

    fun getMainCommunity() {
        GlobalScope.launch {
            repository.getMainCommunity("COMMUNITY")
        }
    }

    fun getAllCommunities() {
        GlobalScope.launch {
            repository.getAllCommunities("COMMUNITY")
        }
    }
}