package mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import mx.arquidiocesis.eamxcommonutils.common.EAMXBaseFragment
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.customui.loader.UtilLoader
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxregistromodule.R
import mx.arquidiocesis.eamxregistromodule.databinding.EamxrSendCodeFragmentBinding
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.repository.EAMXForgotPasswordRepository
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.viewmodel.EAMXForgotPasswordViewModel
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.viewmodel.ERROR_EMAIL


class EAMXSendCodeFragment(val listener: ()->(Unit)) : EAMXBaseFragment() {
    //region without use
    override fun initDependency(savedInstanceState: Bundle?) {}

    override fun setViewModel() {}
    //endregion

    private val TAG = this.javaClass.name
    private val loader by lazy { UtilLoader() }
    private lateinit var binding: EamxrSendCodeFragmentBinding
    private var executeSendCode: Boolean = false

    private val viewModel : EAMXForgotPasswordViewModel by lazy {
        getViewModel {
            EAMXForgotPasswordViewModel(
                EAMXForgotPasswordRepository(requireContext()))
        }
    }

    override fun getLayout() = R.layout.eamxr_send_code_fragment

    override fun initBinding(view: View) {
        binding = EamxrSendCodeFragmentBinding.bind(view)
    }

    override fun initObservers() {
        viewModel.validationInputs.observe(this){ map ->
            executeSendCode = false;

            if (map.containsKey(ERROR_EMAIL)) {
                binding.etEmailSendCode.error = map[ERROR_EMAIL]
                binding.etEmailSendCode.requestFocus()
                viewModel.cleanErrors()

            } else {
                binding.etEmailSendCode.error = null
            }
        }

        viewModel.responseError.observe(this){ messageError ->
            hideLoader()
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_alert))
                .setMessage(messageError)
                .build()
                .show(childFragmentManager, "")

            if(executeSendCode) {
                executeSendCode = false
            }
        }

        viewModel.responseSendCode.observe(this){
            hideLoader()

            if(executeSendCode) {
                executeSendCode = false
                val bundle = Bundle().apply {
                    putString(EAMXEnumUser.USER_CHANGE_PASSWORD.name, binding.etEmailSendCode.text.toString())
                }

                NavigationFragment.Builder()
                        .setActivity(requireActivity())
                        .setView(requireView().parent as ViewGroup)
                        .setAllowStack(true)
                        .setFragment(EAMXConfirmPasswordFragment(listener))
                        .setBundle(bundle)
                        .build().nextWithReplace()
            }
        }
    }

    override fun initView(view: View) {
        binding.apply {
            toolbarModel.btnBack.setOnClickListener {
                listener()
            }
            layoutTerminosCode.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacity_notification))))
            }
            layoutPoliticaCode.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacity_notification))))
            }

            btnSendCode.setOnClickListener {
                if(!executeSendCode) {
                    executeSendCode = true;
                    val text = etEmailSendCode.text?.toString()
                    var error: String? = null
                    if (text.isNullOrEmpty())
                        error = "No puedes dejar campo vacío"
                    if (!Patterns.EMAIL_ADDRESS.matcher(text).matches())
                        error = "Ingresa un correo válido"
                    if (text?.isDigitsOnly() == true) {
                        error = if (text.length != 10) "El numero celular es incorrecto"
                        else null
                    }
                    if (error != null) {
                        executeSendCode = false
                        etEmailSendCode.error = error
                        binding.etEmailSendCode.requestFocus()
                        viewModel.cleanErrors()
                        return@setOnClickListener
                    }
                    viewModel.sendCode(etEmailSendCode.text.toString()) { showLoader(TAG) }
                }
            }
        }
    }

    private fun showLoader(tag : String = "") {
        if (!loader.isAdded) {
            loader.show(childFragmentManager, tag)
        }
    }

    private fun hideLoader() {
        if (loader.isAdded) {
            loader.dismissAllowingStateLoss()
        }
    }
}