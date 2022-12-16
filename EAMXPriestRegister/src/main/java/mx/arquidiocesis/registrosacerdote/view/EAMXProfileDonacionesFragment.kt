package mx.arquidiocesis.registrosacerdote.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import mx.arquidiocesis.registrosacerdote.databinding.EamxProfileDonacionesFragmentsBinding
import mx.arquidiocesis.registrosacerdote.R

class EAMXProfileDonacionesFragment : Fragment(R.layout.eamx_profile_donaciones_fragments) {

    lateinit var mBinding: EamxProfileDonacionesFragmentsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding = EamxProfileDonacionesFragmentsBinding.bind(view)
        super.onViewCreated(mBinding.root, savedInstanceState)

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            EAMXProfileDonacionesFragment
    }
}