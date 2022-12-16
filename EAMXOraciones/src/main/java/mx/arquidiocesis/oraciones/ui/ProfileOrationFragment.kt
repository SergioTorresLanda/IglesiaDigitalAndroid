package mx.arquidiocesis.oraciones.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arquidiocesis.oraciones.R
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome

class ProfileOrationFragment : Fragment() {

    companion object {
        fun newInstance(callBack: EAMXHome): ProfileOrationFragment {
            val fragment = ProfileOrationFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    lateinit var callBack: EAMXHome

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_oration_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callBack.showToolbar(true, AppMyConstants.oraciones)

        val fragment = OracionesFragment.newInstance()

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameOration, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callBack.restoreToolbar()
    }
}