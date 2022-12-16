package mx.arquidiocesis.sos.viewmodel

import android.provider.Settings
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.sos.repository.SOSRepository

class SOSServicesPriestViewModel(private val repository: SOSRepository) : ViewModel() {
    val errorResponse = repository.errorResponse
    val priestServices = repository.priestServices

    fun getPriestServices(userId: Int,status: String) {
        GlobalScope.launch {
            repository.getPriestServices(userId,  status)
        }
    }
}