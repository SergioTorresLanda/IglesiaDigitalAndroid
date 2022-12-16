package mx.arquidiocesis.eamxprofilemodule.ui

import android.os.Bundle
import android.view.View
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.common.EAMXBaseFragment
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXSignOut
import mx.arquidiocesis.eamxprofilemodule.R
import mx.arquidiocesis.eamxprofilemodule.ui.profile.EAMXProfilePrincipalFragment

class EAMXContenedorPrincipalFragment : EAMXBaseFragment() {

    override fun getLayout() = R.layout.eamx_contenedor_principal_fragment

    override fun setViewModel() {}


    override fun initBinding(view: View) {}

    override fun initDependency(savedInstanceState: Bundle?) {}

    override fun initObservers() {}

    override fun initView(view: View) {
        (requireActivity() as EAMXHome).showToolbar(false, AppMyConstants.perfil)
        eamxBackHandler.addFragment(
            EAMXProfilePrincipalFragment.newInstance(),
            R.id.contentFragmentProfile
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(): EAMXContenedorPrincipalFragment {
            val contentFragmentPrincipal = EAMXContenedorPrincipalFragment()
            return contentFragmentPrincipal
        }
    }
}
