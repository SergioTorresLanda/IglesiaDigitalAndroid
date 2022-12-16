package mx.arquidiocesis.eamxgeneric.fragments.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import mx.arquidiocesis.eamxgeneric.R
import mx.arquidiocesis.eamxgeneric.databinding.EamxSecondViewBinding

class EAMXSecondView : Fragment(R.layout.eamx_second_view) {
    lateinit var mBinding: EamxSecondViewBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding = EamxSecondViewBinding.bind(view)
        super.onViewCreated(mBinding.root, savedInstanceState)
    }
}