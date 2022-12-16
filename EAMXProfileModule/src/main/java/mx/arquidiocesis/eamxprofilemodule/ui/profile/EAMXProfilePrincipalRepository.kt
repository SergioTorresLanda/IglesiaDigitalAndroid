package mx.arquidiocesis.eamxprofilemodule.ui.profile

import android.content.Context
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXSignOut
import mx.arquidiocesis.eamxprofilemodule.R
import mx.arquidiocesis.eamxprofilemodule.model.local.UserNames
import mx.arquidiocesis.eamxprofilemodule.ui.EAMXContenedorInformacionFragment

class EAMXProfilePrincipalRepository {

    var titlesForFragments = arrayListOf<String>()

    fun getFragmentForContainer(signOut: EAMXSignOut, callBack: EAMXHome, listener: (UserNames) -> Unit) = arrayListOf(
        EAMXContenedorInformacionFragment.newInstance(callBack, signOut, listener)
    )


    fun getTitlesForFragments(context: Context): ArrayList<String> {
        context.apply {
            titlesForFragments = arrayListOf(
                getString(
                    R.string.information
                ), ""
            )
        }
        return titlesForFragments
    }
}