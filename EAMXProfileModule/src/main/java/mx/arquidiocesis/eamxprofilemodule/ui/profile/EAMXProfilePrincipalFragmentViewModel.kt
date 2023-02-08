package mx.arquidiocesis.eamxprofilemodule.ui.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import mx.arquidiocesis.eamxcommonutils.common.EAMXBaseFragment
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXSignOut
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import mx.arquidiocesis.eamxprofilemodule.info.utils.EAMXProfilePagerAdapter
import mx.arquidiocesis.eamxprofilemodule.model.local.UserNames
import mx.arquidiocesis.eamxprofilemodule.ui.profile.model.EAMXViewPagerConstructor

class EAMXProfilePrincipalFragmentViewModel() : ViewModel() {

    private val fragmentsFromViewPagerList: ArrayList<EAMXBaseFragment> = ArrayList()
    var adapter: EAMXProfilePagerAdapter<EAMXBaseFragment>? = null
    private val interactor = EAMXProfilePrincipalInteractor()

    fun initFragments(context: Context, eamxViewPagerConstructor: EAMXViewPagerConstructor, signOut: EAMXSignOut, callBack: EAMXHome, listener: (UserNames) -> Unit) {
        if (adapter == null) {
            adapter = EAMXProfilePagerAdapter(eamxViewPagerConstructor)
        }
        if (fragmentsFromViewPagerList.isEmpty()) {
            EAMXFirebaseManager(context).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "Home_Perfil")
            })
            fragmentsFromViewPagerList.addAll(interactor.getFragmentForContainer(signOut, callBack, listener))
            adapter?.let {
                it.setTitleList(interactor.getTitlesForFragments(context))
                it.setFragmentList(fragmentsFromViewPagerList)
            }
        }
    }
}