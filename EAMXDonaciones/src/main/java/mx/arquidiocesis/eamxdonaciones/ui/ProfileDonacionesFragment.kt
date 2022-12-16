package mx.arquidiocesis.eamxdonaciones.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eamxdonaciones.R
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome

class ProfileDonacionesFragment : Fragment() {

    companion object {
        fun newInstance(): ProfileDonacionesFragment {
            val fragment = ProfileDonacionesFragment()
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_donaciones, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as EAMXHome).showToolbar(true, AppMyConstants.ofrenda)

        val fragment = OfrendaFragment.newInstance()

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameDonacion, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as EAMXHome).restoreToolbar()
    }
}