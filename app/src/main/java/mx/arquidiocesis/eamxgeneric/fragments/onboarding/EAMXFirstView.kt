package mx.arquidiocesis.eamxgeneric.fragments.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import mx.arquidiocesis.eamxgeneric.R
import mx.arquidiocesis.eamxgeneric.databinding.EamxFirstViewBinding

class EAMXFirstView : Fragment(R.layout.eamx_first_view) {

    lateinit var mBinding: EamxFirstViewBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding = EamxFirstViewBinding.bind(view)
        super.onViewCreated(mBinding.root, savedInstanceState)
    }
}