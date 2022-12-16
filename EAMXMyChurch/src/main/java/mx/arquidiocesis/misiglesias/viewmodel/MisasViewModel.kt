package mx.arquidiocesis.misiglesias.viewmodel

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.misiglesias.repository.RepositoryMeasses

class MisasViewModel(private val repositoryMeasses: RepositoryMeasses) : ViewModel() {

    val response = repositoryMeasses.misasResponse
    val buscarRespose = repositoryMeasses.buscarResponse
    var errorResponse = repositoryMeasses.errorResponse
    val locationResponse = repositoryMeasses.locationResponse

    fun misasList(location: Location) {
        GlobalScope.launch {
            repositoryMeasses.getPrayersType(location)
        }
    }

    fun buscarList(name: String, location: Location?) {
        GlobalScope.launch {
            repositoryMeasses.getMasses(name, location)
        }
    }

    fun getLocation(context: Context) {
        repositoryMeasses.getLocation(context)
    }
}