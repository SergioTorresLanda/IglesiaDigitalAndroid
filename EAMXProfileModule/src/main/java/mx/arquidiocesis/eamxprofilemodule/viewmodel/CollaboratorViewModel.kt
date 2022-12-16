package mx.arquidiocesis.eamxprofilemodule.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxprofilemodule.model.RequestModuleUpdate
import mx.arquidiocesis.eamxprofilemodule.repository.RepositoryProfile

class CollaboratorViewModel(val repositoryProfile: RepositoryProfile) : ViewModel() {

    val getCollaboratorsResponse = repositoryProfile.getCollaboratorsResponse
    val collaboratorDetailResponse = repositoryProfile.collaboratorDetailResponse
    val updateModulesOfCollaboratorResponse = repositoryProfile.updateModulesOfCollaboratorResponse
    val errorResponse = repositoryProfile.errorResponse
    val errorResponseExit = repositoryProfile.errorResponseExit
    var user : Int = 0

    init {
        user = eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID.name,
            EAMXTypeObject.INT_OBJECT
        ) as Int
    }

    fun getCollaborators(location_id: Int, name: String) {
        GlobalScope.launch {
            repositoryProfile.getCollaborators(location_id, name)
        }
    }

    fun getCollaboratorDetail(location_id: Int, user_id: Int) {
        GlobalScope.launch {
            repositoryProfile.getCollaboratorDetail(location_id, user_id, user)
        }
    }

    fun updateModulesOfCollaborator(
        location_id: Int,
        user_id: Int,
        modules: List<RequestModuleUpdate>
    ) {
        GlobalScope.launch {
            repositoryProfile.updateModulesOfCollaborator(location_id, user_id, modules, user)
        }
    }
}