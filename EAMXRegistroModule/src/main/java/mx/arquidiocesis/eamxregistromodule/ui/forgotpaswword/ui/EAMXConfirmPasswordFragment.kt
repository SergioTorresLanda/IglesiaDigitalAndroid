package mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import kotlinx.android.synthetic.main.eamxr_confirm_password_activity.*
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXFieldValidation
import mx.arquidiocesis.eamxcommonutils.common.EAMXBaseFragment
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.customui.loader.UtilLoader
import mx.arquidiocesis.eamxcommonutils.util.buildTextWithUnderscore
import mx.arquidiocesis.eamxcommonutils.util.deleteFocusEditText
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxcommonutils.util.nextFocusEditText
import mx.arquidiocesis.eamxregistromodule.R
import mx.arquidiocesis.eamxregistromodule.databinding.EamxrConfirmPasswordActivityBinding
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.repository.EAMXForgotPasswordRepository
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.viewmodel.EAMXForgotPasswordViewModel
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.viewmodel.ERROR_CODE
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.viewmodel.ERROR_CONFIRM_PASSWORD
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.viewmodel.ERROR_PASSWORD

class EAMXConfirmPasswordFragment(val listener: () -> (Unit)) : EAMXBaseFragment() {
    //region without use
    override fun setViewModel() {}
    override fun initDependency(savedInstanceState: Bundle?) {}
    //endregion

    private val TAG = this.javaClass.name
    private val loader by lazy { UtilLoader() }
    private lateinit var binding: EamxrConfirmPasswordActivityBinding

    private val viewModel: EAMXForgotPasswordViewModel by lazy {
        getViewModel {
            EAMXForgotPasswordViewModel(
                EAMXForgotPasswordRepository(requireContext())
            )
        }
    }

    override fun getLayout() = R.layout.eamxr_confirm_password_activity

    override fun initBinding(view: View) {
        binding = EamxrConfirmPasswordActivityBinding.bind(view)
        timer.start()
        enableResend(false)
    }

    override fun initObservers() {

        viewModel.validationInputs.observe(this) { map ->
            binding.apply {
                if (map.containsKey(ERROR_PASSWORD)) {
                    tilCodePassword.error = map[ERROR_PASSWORD]
                } else {
                    tilCodePassword.error = null
                }

                if (map.containsKey(ERROR_CONFIRM_PASSWORD)) {
                    tilCodeConfirmPassword.error = map[ERROR_CONFIRM_PASSWORD]
                } else {
                    tilCodeConfirmPassword.error = null
                }

                if (map.containsKey(ERROR_CODE)) {
                    UtilAlert.Builder()
                        .setTitle(getString(R.string.title_alert))
                        .setMessage(map[ERROR_CODE].toString())
                        .build()
                        .show(childFragmentManager, TAG)
                }
            }
        }

        viewModel.responseError.observe(this) { messageError ->
            hideLoader()
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_alert))
                .setMessage(messageError)
                .build()
                .show(childFragmentManager, TAG)
        }

        viewModel.responseSendCode.observe(this) {
            binding.apply {
                etCodeSix.text.clear()
                etCodeFive.text.clear()
                etCodeFour.text.clear()
                etCodeThree.text.clear()
                etCodeTwo.text.clear()
                etCodeOne.text.clear()
                etCodeOne.requestFocus()
            }
            //initCounter()
            timer.start()
            hideLoader()
            /*UtilAlert.Builder()
                .setTitle(getString(R.string.title_alert))
                .setMessage(getString(R.string.meessage_success_sent_code))
                .build()
                .show(childFragmentManager, TAG)*/
        }

        viewModel.responseConfirmPassword.observe(this) {
            hideLoader()
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_alert))
                .setIsCancel(false)
                .setMessage(getString(R.string.meessage_success_confirm_password))
                .setListener {
                    listener()
                }
                .build()
                .show(childFragmentManager, TAG)
        }




        viewModel.responseErrorConfirmPassword.observe(this) { messageError ->
            hideLoader()
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_alert))
                .setMessage(messageError)
                .build()
                .show(childFragmentManager, "")
        }

        viewModel.launchRequestCode.observe(this) { result ->
            if (result) {
                hideLoader()
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_alert))
                    .setMessage(getString(R.string.message_retry_max))
                    .setListener {
                        viewModel.sendCode(arguments?.getString(EAMXEnumUser.USER_CHANGE_PASSWORD.name)!!) {
                            showLoader(TAG)
                        }
                    }
                    .build()
                    .show(childFragmentManager, "")
            }
        }
    }

    val timer = object : CountDownTimer(180000, 1000) {

        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            val timeUntil = millisUntilFinished / 1000
            val minutes = timeUntil / 60
            val seconds = timeUntil % 60
            binding.tvTimer.text = "$minutes:${if (seconds < 10) "0" else ""}$seconds"
        }

        override fun onFinish() {
            this.cancel()
            enableResend(true)
            binding.tvTimer.text = ""

        }
    }

    private fun enableResend(enable: Boolean) {
        binding.tvReSendCode.visibility = if (enable) View.VISIBLE else View.INVISIBLE
        if (!enable) {
            binding.etCodeOne.setText("")
            binding.etCodeTwo.setText("")
            binding.etCodeThree.setText("")
            binding.etCodeFour.setText("")
            binding.etCodeFive.setText("")
            binding.etCodeSix.setText("")
            binding.txtWarning.visibility = View.VISIBLE
        } else {
            binding.apply {
                val colorText = ContextCompat.getColor(requireContext(), R.color.underscopetext)
                tvReSendCode.isEnabled = true
                tvReSendCode.text = getString(R.string.label_re_send_code)
                tvReSendCode.buildTextWithUnderscore(colorText = colorText)
                tvTimer.text = ""
                txtWarning.visibility = View.GONE


            }
        }
    }

    override fun initView(view: View) {
        if (arguments == null || !(requireArguments().containsKey(EAMXEnumUser.USER_CHANGE_PASSWORD.name))) {
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_alert))
                .setMessage(getString(R.string.message_info_load_fail_code))
                .setListener {
                    listener()
                }
                .build()
                .show(childFragmentManager, TAG)
        }

        binding.apply {

            setFocus(arrayOf(etCodeOne, etCodeTwo, etCodeThree, etCodeFour, etCodeFive, etCodeSix))

            toolbarModel.btnBack.setOnClickListener {
                NavigationFragment.Builder()
                    .setActivity(requireActivity())
                    .setView(requireView().parent as ViewGroup)
                    .build().back()
            }

            tvReSendCode.setOnClickListener {
                timer.start()
                enableResend(false)
                viewModel.sendCode(arguments?.getString(EAMXEnumUser.USER_CHANGE_PASSWORD.name)!!) {
                    showLoader(TAG)
                }
            }

            tvChangeUser.setOnClickListener {
                NavigationFragment.Builder()
                    .setActivity(requireActivity())
                    .setView(requireView().parent as ViewGroup)
                    .build().back()
            }

            btnConfirmPassword.setOnClickListener {
                viewModel.confirmPassword(
                    arguments?.getString(EAMXEnumUser.USER_CHANGE_PASSWORD.name)!!,
                    buildCode(),
                    etCodePassword.text.toString(),
                    etCodeConfirmPassword.text.toString()
                ) {
                    showLoader(TAG)
                }
            }
            etCodePassword.addTextChangedListener {
                val text = it.toString()
                text.let { passText ->

                    EAMXFieldValidation.passValidation(
                        passText,
                        tilCodePassword,
                        etCodeConfirmPassword.text.toString(),
                        tilCodeConfirmPassword,
                        "Ingresa una contraseña válida"
                    )

                }
            }

            etCodeConfirmPassword.addTextChangedListener {
                val text = it.toString()
                text.let { confirmtext ->
                    if (confirmtext.isNotEmpty()) {
                        if (confirmtext != etCodePassword.text.toString()) {
                            tilCodeConfirmPassword.error = "La confirmación de la contraseña debe ser igual a la contraseña"
                        } else {
                            tilCodeConfirmPassword.error = null
                        }
                    } else {
                        tilCodeConfirmPassword.error = "Ingresa la confirmación de la contraseña"
                    }
                }
            }

            layoutTerminosPassword.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacity_notification))))
            }

            layoutPoliticaPassword.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacity_notification))))
            }

//            etCodePassword.doAfterTextChanged { editable ->
//                if(editable!!.isNotEmpty()){
//                    tilCodePassword.error = null
//                }
//            }

//            etCodeConfirmPassword.doAfterTextChanged { editable ->
//                if (editable!!.isNotEmpty()) {
//                    tilCodeConfirmPassword.error = null
//                }
//            }
        }
    }

    private fun buildCode(): String {
        var code = ""
        binding.apply {
            code = etCodeOne.text.toString() +
                    etCodeTwo.text.toString() +
                    etCodeThree.text.toString() +
                    etCodeFour.text.toString() +
                    etCodeFive.text.toString() +
                    etCodeSix.text.toString()
        }

        return code
    }

    private fun showLoader(tag: String = "") {
        if (!loader.isAdded) {
            loader.show(childFragmentManager, tag)
        }
    }

    private fun hideLoader() {
        if (loader.isAdded) {
            loader.dismissAllowingStateLoss()
        }
    }

    private fun setFocus(arrayItems: Array<EditText>) {
        for (i in arrayItems.indices) {
            arrayItems[i].deleteFocusEditText()
            when (i) {
                0 -> {
                    arrayItems[i].nextFocusEditText(null, arrayItems[i + 1])
                }
                arrayItems.size - 1 -> {
                    arrayItems[i].nextFocusEditText(arrayItems[i - 1], binding.etCodePassword)
                }
                else -> {
                    arrayItems[i].nextFocusEditText(arrayItems[i - 1], arrayItems[i + 1])
                }
            }

        }
    }

    private fun initCounter() {
        binding.apply {
            tvReSendCode.isEnabled = false
            tvReSendCode.text = getString(R.string.text_label_counter_inet)
            tvTimer.visibility = View.VISIBLE
        }
        viewModel.counterJob()
    }
}