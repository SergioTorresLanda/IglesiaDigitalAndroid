package mx.arquidiocesis.eamxregistromodule.ui.confirm

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import mx.arquidiocesis.eamxcommonutils.api.core.status.EAMXStatusRequestEnum
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.common.EAMXBaseActivity
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnums
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import mx.arquidiocesis.eamxcommonutils.util.eamxLog
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxregistromodule.R
import mx.arquidiocesis.eamxregistromodule.databinding.EamxrConfirmCodeActivityBinding
import mx.arquidiocesis.eamxregistromodule.ui.confirm.model.EAMXResendCodeRequest
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.repository.EAMXForgotPasswordRepository
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.viewmodel.EAMXForgotPasswordViewModel

class EAMXConfirmCodeActivity : EAMXBaseActivity() {
    private lateinit var mBinding: EamxrConfirmCodeActivityBinding
    private lateinit var viewModel: EAMXConfirmCodeViewModel
    private lateinit var arrayListOfEditText: ArrayList<EditText>
    private lateinit var username: String
    private lateinit var name: String
    private lateinit var phone: String
    private lateinit var resendCode: String

    private val viewModelRecovery: EAMXForgotPasswordViewModel by lazy {
        getViewModel {
            EAMXForgotPasswordViewModel(
                EAMXForgotPasswordRepository(this)
            )
        }
    }


    override fun initBinding(view: Int): View {
        mBinding = EamxrConfirmCodeActivityBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun getLayout() = R.layout.eamxr_confirm_code_activity

    override fun initDependency() {}

    override fun setViewModel() {
        viewModel = ViewModelProvider(this).get(EAMXConfirmCodeViewModel::class.java)
    }

    val timer = object : CountDownTimer(180000, 1000) {

        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            val timeUntil = millisUntilFinished / 1000
            val minutes = timeUntil / 60
            val seconds = timeUntil % 60
            mBinding.lbTimer.text = "$minutes:${if (seconds < 10) "0" else ""}$seconds"
        }

        override fun onFinish() {
            this.cancel()
            enableResend(true)
        }
    }

    private fun enableResend(enable: Boolean) {
        mBinding.btnLbResendCode.visibility = if (enable) View.VISIBLE else View.INVISIBLE
        mBinding.llResend.visibility = if (enable) View.VISIBLE else View.INVISIBLE
        mBinding.btnResendCode.visibility = if (enable) View.VISIBLE else View.INVISIBLE
        if (!enable) {
            mBinding.edtNumberOne.setText("")
            mBinding.edtNumberTwo.setText("")
            mBinding.edtNumberThree.setText("")
            mBinding.edtNumberFour.setText("")
            mBinding.edtNumberFive.setText("")
            mBinding.edtNumberSix.setText("")
        }
    }

    override fun initObservers() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = resources.getColor(R.color.white)
        viewModel.responseGeneric.observe(this) {
            when (it.statusRequest) {
                EAMXStatusRequestEnum.LOADING -> {
                    showProgressBarCustom()
                }
                EAMXStatusRequestEnum.SUCCESS -> {
                    hideProgressBarCustom()
                    setResult(Activity.RESULT_OK, intent.putExtra(EAMXEnums.CONFIRMATED.name, true))
                    finish()
                }
                EAMXStatusRequestEnum.FAILURE -> {
                    hideProgressBarCustom()
                    it.errorData?.let { errorMessage ->
                        UtilAlert.Builder()
                            .setMessage(errorMessage)
                            .build().show(supportFragmentManager, "")
                    }
                }
                EAMXStatusRequestEnum.NONE -> {
                    hideProgressBarCustom()
                }
            }
        }
        viewModel.responseGenericResendRequest.observe(this) {
            when (it.statusRequest) {
                EAMXStatusRequestEnum.LOADING -> {
                    showProgressBarCustom()
                }
                EAMXStatusRequestEnum.SUCCESS -> {
                    hideProgressBarCustom()
                    UtilAlert.Builder()
                        .setMessage(getString(R.string.meessage_success_sent_code))
                        .build().show(supportFragmentManager, "")
                }
                EAMXStatusRequestEnum.FAILURE -> {
                    hideProgressBarCustom()
                    UtilAlert.Builder()
                        .setMessage(it.errorData ?: "Error desconocido.")
                        .build().show(supportFragmentManager, "")
                }
                EAMXStatusRequestEnum.NONE -> {
                    hideProgressBarCustom()
                }
            }
        }
    }


    override fun initView() {
        EAMXFirebaseManager(applicationContext).setLogEvent("screen_view", Bundle().apply {
            putString("screen_class", "Login_CodigoConfirmacion")
        })
        intent.extras?.let {
            it.apply {
                username = getString(EAMXEnumUser.USER_EMAIL.name) ?: ""
                name = getString(EAMXEnumUser.USER_NAME.name) ?: ""
                phone = getString(EAMXEnumUser.USER_PHONE.name) ?: ""
                resendCode = getString(AppMyConstants.RESEND_CODE_END_POINT) ?: ""
            }
        }

        if (resendCode?.isNotEmpty()) {
            showProgressBarCustom()
            viewModel.requestResendCode(EAMXResendCodeRequest(username))
            viewModelRecovery.getInfoCode(username)
        }

        mBinding.apply {

            timer.start()
            arrayListOfEditText = arrayListOf(
                edtNumberOne, edtNumberTwo, edtNumberThree,
                edtNumberFour, edtNumberFive, edtNumberSix
            )
            setFocus(arrayListOfEditText)

            toolbarModel.btnBack.setOnClickListener { finish() }
            btnVerifyCodeNumber.setOnClickListener {
                requestConfirmCode(
                    username,
                    viewModel,
                    arrayListOfEditText,
                    this@EAMXConfirmCodeActivity
                )
            }
            btnResendCode.setOnClickListener {
                enableResend(false)
                timer.start()
                viewModel.requestResendCode(EAMXResendCodeRequest(username))
            }
            layoutTerminos.setOnClickListener {
                val url = "https://arquidiocesismexico.org.mx/aviso-de-privacidad/"
                val uri = Uri.parse(url)
                val i = Intent(Intent.ACTION_VIEW, uri)
                startActivity(i)
            }
            layoutPolitica.setOnClickListener {
                val url = "https://arquidiocesismexico.org.mx/aviso-de-privacidad/"
                val uri = Uri.parse(url)
                val i = Intent(Intent.ACTION_VIEW, uri)
                startActivity(i)
            }

            btnLbResendCode.setOnClickListener {
                viewModel.requestResendCode(EAMXResendCodeRequest(username))
                enableResend(false)
                timer.start()
            }
        }
    }
}