package mx.arquidiocesis.servicios.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.*
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.userAllowAccessAsAdmin
import mx.arquidiocesis.servicios.R
import mx.arquidiocesis.servicios.ui.admin.ServiceAdminMainFragment

class ServicesMainFragment : FragmentBase() {

    companion object {
        fun newInstance(callBack: EAMXHome): ServicesMainFragment {
            val fragment = ServicesMainFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_frame, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callBack.let {
            callBack.showToolbar(true, AppMyConstants.servicios)
        }

        val fragment = loadFragmentByUserRol()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callBack.restoreToolbar()
    }

    private fun loadFragmentByUserRol(): Fragment {

        return when (eamxcu_preferences.getData(EAMXEnumUser.USER_PROFILE.name, EAMXTypeObject.STRING_OBJECT) as String) {
            EAMXProfile.PriestAdmin.rol,//sacer admin
            EAMXProfile.DeanPriest.rol,//sacer decano
            EAMXProfile.DevotedAdmin.rol,//fiel admin
            EAMXProfile.VicariaClero.rol,
            EAMXProfile.VicariaPastoral.rol,
            EAMXProfile.VicariaVidaPastoral.rol
            -> ServiceAdminMainFragment.newInstance(callBack)

            else -> ServicesMenuFragment.newInstance(callBack)
        }
    }
}