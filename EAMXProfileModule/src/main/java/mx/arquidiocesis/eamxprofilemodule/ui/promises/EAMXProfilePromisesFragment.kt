package mx.arquidiocesis.eamxprofilemodule.ui.promises

import android.os.Bundle
import android.view.View
import mx.arquidiocesis.eamxcommonutils.common.EAMXBaseFragment
import mx.arquidiocesis.eamxprofilemodule.R
import mx.arquidiocesis.eamxprofilemodule.databinding.EamxProfilePromisesFragmentsBinding

class EAMXProfilePromisesFragment : EAMXBaseFragment() {
    lateinit var mBinding: EamxProfilePromisesFragmentsBinding

    override fun getLayout(): Int = R.layout.eamx_profile_promises_fragments

    override fun initBinding(view: View) {
        mBinding = EamxProfilePromisesFragmentsBinding.bind(view)
    }

    override fun initDependency(savedInstanceState: Bundle?) {
    }

    override fun initObservers() {

    }

    override fun initView(view: View) {
        initListener(mBinding, eamxBackHandler, requireActivity())
    }

    override fun setViewModel() {
    }

    companion object {
        fun newInstance() = EAMXProfilePromisesFragment()
    }

}