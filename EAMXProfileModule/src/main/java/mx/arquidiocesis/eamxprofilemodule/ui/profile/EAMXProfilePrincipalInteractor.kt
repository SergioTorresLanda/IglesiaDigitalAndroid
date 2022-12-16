package mx.arquidiocesis.eamxprofilemodule.ui.profile

import android.content.Context
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXSignOut
import mx.arquidiocesis.eamxprofilemodule.model.local.UserNames

class EAMXProfilePrincipalInteractor {

    private val localRepository = EAMXProfilePrincipalRepository()

    fun getFragmentForContainer(signOut: EAMXSignOut, callBack: EAMXHome, listener: (UserNames) -> Unit) = localRepository.getFragmentForContainer(signOut,callBack, listener)
    fun getTitlesForFragments(context: Context) = localRepository.getTitlesForFragments(context)
}