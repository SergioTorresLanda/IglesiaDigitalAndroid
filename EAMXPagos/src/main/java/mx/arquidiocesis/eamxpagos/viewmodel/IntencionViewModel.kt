package mx.arquidiocesis.eamxpagos.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import mx.arquidiocesis.eamxpagos.Repository.RepositoryServices
import mx.arquidiocesis.eamxpagos.model.MentionRequestPost

class IntencionViewModel(private val repository: RepositoryServices) : ViewModel() {

    val response = repository.serviciosResponse
    var errorResponse = repository.errorResponse

    val zipCodeResponse = repository.zipCodeResponse


    fun sendMention(mentionRequest: MentionRequestPost) {
        GlobalScope.launch {
            repository.sendMention(mentionRequest)
        }
    }
}