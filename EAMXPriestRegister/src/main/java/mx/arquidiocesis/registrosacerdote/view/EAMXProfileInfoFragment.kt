package mx.arquidiocesis.registrosacerdote.view

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import mx.arquidiocesis.registrosacerdote.R
import mx.arquidiocesis.registrosacerdote.databinding.EamxProfileInfoFragmentBinding

class EAMXProfileInfoFragment(    val listener: (Int) -> Unit,
) : Fragment(R.layout.eamx_profile_info_fragment) {

    lateinit var mBinding: EamxProfileInfoFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding = EamxProfileInfoFragmentBinding.bind(view)
        super.onViewCreated(mBinding.root, savedInstanceState)

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sp_estado_civil,
            android.R.layout.simple_spinner_dropdown_item).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            mBinding.btnIamSacerdote.setOnClickListener{
               listener(1)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            EAMXProfileInfoFragment
    }
}