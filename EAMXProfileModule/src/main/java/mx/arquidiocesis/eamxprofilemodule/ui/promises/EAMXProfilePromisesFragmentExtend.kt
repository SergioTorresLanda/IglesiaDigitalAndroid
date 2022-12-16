package mx.arquidiocesis.eamxprofilemodule.ui.promises

import androidx.fragment.app.FragmentActivity
import mx.arquidiocesis.eamxcommonutils.common.EAMXBackHandler
import mx.arquidiocesis.eamxcommonutils.util.toast
import mx.arquidiocesis.eamxprofilemodule.R
import mx.arquidiocesis.eamxprofilemodule.databinding.EamxProfilePromisesFragmentsBinding
import mx.arquidiocesis.eamxprofilemodule.ui.promises.createpromise.fragment.EAMXProfilePromisesCreate

fun initListener(mBinding: EamxProfilePromisesFragmentsBinding, eamxBackHandler: EAMXBackHandler, requireActivity: FragmentActivity) {

    mBinding.apply {
        btnCreatePromise.setOnClickListener{
            eamxBackHandler.changeFragment(EAMXProfilePromisesCreate.newInstance(), R.id.contentFragmentProfile, EAMXProfilePromisesCreate::class.java.simpleName)
            requireActivity.toast("Crear promesa")
        }

        //mBinding.recycler.layoutManager = LinearLayoutManager(requireActivity)
        //val listModel = listOf(Model("hola"))
        //mBinding.recycler.adapter = RecyclerAdapter(requireActivity, listModel)
    }
}