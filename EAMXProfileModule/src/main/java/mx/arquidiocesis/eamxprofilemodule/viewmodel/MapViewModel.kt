package mx.arquidiocesis.eamxprofilemodule.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxprofilemodule.repository.RepositoryProfile


class MapViewModel(private val repositoryProfile: RepositoryProfile) : ViewModel() {

    val response = repositoryProfile.iglesiasMapResponse
    var errorResponse = repositoryProfile.errorResponse
    fun getChurchList () {
        GlobalScope.launch {
            repositoryProfile.getChurch()
        }
    }
    fun getCommunitiesByName(name: String) {
        GlobalScope.launch {
            repositoryProfile.getCommunitiesByName("COMMUNITY", name)
        }
    }

    fun getChurchList (name:String) {
        GlobalScope.launch {
            repositoryProfile.getChurch(name)
        }
    }
}