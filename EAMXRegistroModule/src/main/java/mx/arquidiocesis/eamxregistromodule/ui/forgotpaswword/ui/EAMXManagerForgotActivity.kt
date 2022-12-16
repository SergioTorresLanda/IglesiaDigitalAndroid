package mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.ui

import android.view.View
import mx.arquidiocesis.eamxcommonutils.common.EAMXBaseActivity
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxregistromodule.R
import mx.arquidiocesis.eamxregistromodule.databinding.EamxrForgotPasswordFragmentBinding


class EAMXManagerForgotActivity : EAMXBaseActivity() {

    lateinit var callBack: EAMXHome

    private lateinit var binding: EamxrForgotPasswordFragmentBinding

    override fun getLayout() = R.layout.eamxr_register_activity

    override fun initBinding(view: Int): View {
        binding = EamxrForgotPasswordFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initDependency() {
    }

    override fun initObservers() {
    }

    override fun setViewModel() {
    }

    override fun initView() {
        binding?.apply {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(frameManager.id, EAMXSendCodeFragment(){
                finish()
            })
            transaction.disallowAddToBackStack()
            transaction.commit()
        }
    }
}