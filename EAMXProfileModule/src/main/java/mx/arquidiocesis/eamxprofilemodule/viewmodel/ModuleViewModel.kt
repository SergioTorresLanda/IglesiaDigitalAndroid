package mx.arquidiocesis.eamxprofilemodule.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxprofilemodule.model.RequestModuleUpdate
import mx.arquidiocesis.eamxprofilemodule.repository.RepositoryProfile

class ModuleViewModel(val repositoryProfile: RepositoryProfile) : ViewModel() {
    val getModulesResponse = repositoryProfile.getModulesResponse
    val updateModulesResponse = repositoryProfile.updateModulesResponse
    val errorResponse = repositoryProfile.errorResponse

    fun getModules(location_id: Int) {
        GlobalScope.launch {
            repositoryProfile.getModules(location_id)
        }
    }

    fun updateModules(location_id: Int, modules: List<RequestModuleUpdate>) {
        GlobalScope.launch {
            repositoryProfile.updateModules(location_id, modules)
        }
    }
}