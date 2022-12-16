package mx.arquidiocesis.eamxcadenaoracionesmodule.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcadenaoracionesmodule.model.EAMXPraySend
import mx.arquidiocesis.eamxcadenaoracionesmodule.model.EAMXSendPrayStatus
import mx.arquidiocesis.eamxcadenaoracionesmodule.repository.EAMXCadenaOracionesRepository

class EAMXCadenaOracionesViewModel(val repository: EAMXCadenaOracionesRepository) : ViewModel() {

    val prayListResponse = repository.prayListResponse
    val sendPrayResponse = repository.sendPrayResponse
    val prayingResponse = repository.prayingResponse
    val errorResponse = repository.errorResponse

    var prayId : Int = -1

    fun getPrayers(userId: Int) {
        GlobalScope.launch {
            repository.getPrayers(userId)
        }
    }

    fun sendPrayer(eamxPraySend: EAMXPraySend) {
        GlobalScope.launch {
            repository.sendPrayer(eamxPraySend)
        }
    }

    fun prayingOration(idPray: Int, eamxSendPrayStatus: EAMXSendPrayStatus) {
        prayId = idPray
        GlobalScope.launch {
            repository.prayingOration(idPray, eamxSendPrayStatus)
        }
    }
}