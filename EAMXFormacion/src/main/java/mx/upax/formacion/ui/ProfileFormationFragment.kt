package mx.upax.formacion.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.upax.formacion.R
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome

class ProfileFormationFragment: Fragment() {

    companion object {
        fun newInstance(callBack: EAMXHome): ProfileFormationFragment {
            val fragment = ProfileFormationFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    lateinit var callBack: EAMXHome

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_formation_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragment = FormationFragment.newInstance(callBack)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameFormation, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callBack.restoreToolbar()
    }
}