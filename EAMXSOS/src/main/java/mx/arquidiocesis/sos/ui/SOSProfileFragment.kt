package mx.arquidiocesis.sos.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.upax.eamxsos.R
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXProfile
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.userAllowAccessAsAdmin

class SOSProfileFragment : Fragment() {

    companion object {
        fun newInstance(callBack: EAMXHome): SOSProfileFragment {
            val fragment = SOSProfileFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    lateinit var callBack: EAMXHome

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_s_o_s_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userIsAdmin = userAllowAccessAsAdmin(EAMXEnumUser.USER_PERMISSION_SOS.name)

        val fromSOS = eamxcu_preferences.getData("FROMSOS", EAMXTypeObject.BOOLEAN_OBJECT) as Boolean

        callBack.showToolbar(true, if (fromSOS) AppMyConstants.sos else  AppMyConstants.notificaciones)

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameSOSProfile,
            if (userIsAdmin) {//Sacerdote
                PriestProfileFragment.newInstance()
            }else{//Fiel
                 FaithfulProfileFragment.newInstance()

                /*if (fromSOS) {
                 FaithfulProfileFragment.newInstance()
                } else {
                    // SOSNotificationFaithfulFragment.newInstance()
                }*/
            })
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callBack.restoreToolbar()
    }
}