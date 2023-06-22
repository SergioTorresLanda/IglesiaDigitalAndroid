package mx.arquidiocesis.sos.ui

import android.os.Bundle
import android.util.Log
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
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
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

         //val userIsAdmin=userAllowAccessAsAdmin(EAMXEnumUser.USER_PERMISSION_SOS.name)
        val userIsAdmin = when (eamxcu_preferences.getData(EAMXEnumUser.USER_PROFILE.name, EAMXTypeObject.STRING_OBJECT) as String) {
            EAMXProfile.PriestAdmin.rol,//sacer admin
            EAMXProfile.DeanPriest.rol,//sacer decano
            EAMXProfile.DevotedAdmin.rol,//fiel admin
            EAMXProfile.VicariaClero.rol,
            EAMXProfile.VicariaPastoral.rol,
            EAMXProfile.VicariaVidaPastoral.rol
            -> true
            else -> false
        }
        val fromSOS = eamxcu_preferences.getData("FROMSOS", EAMXTypeObject.BOOLEAN_OBJECT) as Boolean

        callBack.showToolbar(true, if (fromSOS) AppMyConstants.sos else  AppMyConstants.notificaciones)

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameSOSProfile,
            if (userIsAdmin) {//Sacerdote
                println("SOS ::: ES AdmiNN:: SI")
                activity?.let {
                    EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                        putString("screen_class", "Home_SOSPriest")
                    })
                }
                PriestProfileFragment.newInstance()
            }else{//Fiel
                println("SOS ::: ES AdmiNN:: NO")
                activity?.let {
                    EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                        putString("screen_class", "Home_SOSPrincipal")
                    })
                }
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