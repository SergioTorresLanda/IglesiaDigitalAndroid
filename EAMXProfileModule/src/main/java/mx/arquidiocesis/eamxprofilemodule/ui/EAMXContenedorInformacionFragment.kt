package mx.arquidiocesis.eamxprofilemodule.ui

import android.os.Bundle
import android.view.View
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.common.EAMXBaseFragment
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXSignOut
import mx.arquidiocesis.eamxprofilemodule.R
import mx.arquidiocesis.eamxprofilemodule.model.local.UserNames
import mx.arquidiocesis.eamxprofilemodule.ui.information.EAMXProfileInfoFragment

class EAMXContenedorInformacionFragment : EAMXBaseFragment() {

    lateinit var callBack: EAMXHome
    lateinit var signOut: EAMXSignOut
    lateinit var listener: (UserNames) -> Unit

    override fun getLayout() = R.layout.eamx_contenedor_informacion_fragment

    override fun setViewModel() {}


    override fun initBinding(view: View) {}

    override fun initDependency(savedInstanceState: Bundle?) {}

    override fun initObservers() {}

    override fun initView(view: View) {
        callBack.showToolbar(false, AppMyConstants.perfil)
        eamxBackHandler.addFragment(EAMXProfileInfoFragment.newInstance(signOut, callBack, listener), R.id.contentFragmentInformacion)
    }


    companion object {
        @JvmStatic
        fun newInstance(callBack: EAMXHome, signOut: EAMXSignOut, listener: (UserNames) -> Unit): EAMXContenedorInformacionFragment {
            val contentFragmentInformacion = EAMXContenedorInformacionFragment()
            contentFragmentInformacion.callBack = callBack
            contentFragmentInformacion.signOut = signOut
            contentFragmentInformacion.listener = listener
            return  contentFragmentInformacion
        }
    }
}
