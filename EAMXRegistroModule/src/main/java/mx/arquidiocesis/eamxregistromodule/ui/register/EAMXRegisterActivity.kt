package mx.arquidiocesis.eamxregistromodule.ui.register

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.util.Patterns
import android.view.View
import androidx.core.view.isEmpty
import androidx.core.widget.addTextChangedListener
import androidx.databinding.adapters.CompoundButtonBindingAdapter.setChecked
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.rxjava3.schedulers.Schedulers.start
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.eamxr_register_activity.*
import mx.arquidiocesis.eamxcommonutils.api.core.errorresponse.EAMXErrorResponseEnum
import mx.arquidiocesis.eamxcommonutils.api.core.status.EAMXStatusRequestEnum
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXFieldValidation
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXStatusValidation
import mx.arquidiocesis.eamxcommonutils.common.*
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.log
import mx.arquidiocesis.eamxcommonutils.util.visibility
import mx.arquidiocesis.eamxregistromodule.R
import mx.arquidiocesis.eamxregistromodule.databinding.EamxrRegisterActivityBinding
import mx.arquidiocesis.eamxregistromodule.ui.confirm.EAMXConfirmCodeActivity
import okhttp3.internal.EMPTY_REQUEST
import okhttp3.internal.EMPTY_RESPONSE
import java.util.regex.Pattern

class EAMXRegisterActivity : EAMXBaseActivity() {

    lateinit var mBinding: EamxrRegisterActivityBinding
    lateinit var viewModelEAMX: EAMXRegisterViewModel
    lateinit var contexto: Context

    override fun getLayout() = R.layout.eamxr_register_activity

    override fun initBinding(view: Int): View {
        mBinding = EamxrRegisterActivityBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun setViewModel() {
        viewModelEAMX = ViewModelProvider(this).get(EAMXRegisterViewModel::class.java)
    }

    override fun initDependency() {}

    override fun initObservers() {
        viewModelEAMX.responseGeneric.observe(this) { response ->
            when (response.statusRequest) {
                EAMXStatusRequestEnum.LOADING -> {
                    showProgressBarCustom()
                }
                EAMXStatusRequestEnum.SUCCESS -> {
                    hideProgressBarCustom()
                    startActivityForResult(
                        Intent(
                            this,
                            EAMXConfirmCodeActivity::class.java
                        ).putExtra(EAMXEnumUser.USER_EMAIL.name, response.requestData.email)
                            .putExtra(EAMXEnumUser.USER_NAME.name, response.requestData.name)
                            .putExtra(
                                EAMXEnumUser.USER_PHONE.name,
                                response.requestData.phone_number?.substring(3)
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), EAMXEnums.CONFIRM_CODE.code
                    )
                }
                EAMXStatusRequestEnum.FAILURE -> {
                    hideProgressBarCustom()
                    response.errorData?.let { errorMessage ->
                        when (errorMessage) {
                            EAMXErrorResponseEnum.USER_ALREADY_CONFIRMED.messageError -> {
                                UtilAlert.Builder().setTitle("Aviso")
                                    .setMessage(getString(R.string.you_have_already_an_account))
                                    .setListener {
                                        setResult(
                                            Activity.RESULT_CANCELED, intent.putExtra(
                                                EAMXEnumUser.USER_EMAIL.name,
                                                response.requestData.email
                                            )
                                        )
                                        EAMXRegisterActivity@ finish()
                                    }.build().show(supportFragmentManager, "")
                                return@let
                            }
                            EAMXErrorResponseEnum.USER_IS_NOT_CONFIRMED.messageError -> {
                                UtilAlert.Builder().setTitle("Aviso")
                                    .setMessage(getString(R.string.your_user_already_exists_confirm_to_login))
                                    .setListener {
                                        startActivityForResult(
                                            Intent(
                                                this,
                                                EAMXConfirmCodeActivity::class.java
                                            ).putExtra(
                                                EAMXEnumUser.USER_EMAIL.name,
                                                response.requestData.email
                                            ).putExtra(
                                                AppMyConstants.RESEND_CODE_END_POINT,
                                                AppMyConstants.RESEND_CODE_END_POINT
                                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                                            EAMXEnums.CONFIRM_CODE.code
                                        )
                                    }.build().show(supportFragmentManager, "")
                                return@let
                            }
                            else -> {
                                UtilAlert.Builder().setMessage(errorMessage).build()
                                    .show(supportFragmentManager, "")
                            }
                        }
                    }
                }
                EAMXStatusRequestEnum.NONE -> {
                    hideProgressBarCustom()
                }
            }
        }

        viewModelEAMX.openLoginFromActivity.observe(this) {
            if (!it.result) {
                setResult(Activity.RESULT_CANCELED)
            } else {
                UtilAlert.Builder()
                    .setMessage("Registro completo")
                    .setListener { _ ->
                        setResult(Activity.RESULT_OK, it.data)
                        finish()
                    }
                    .setIsCancel(false)
                    .build()
                    .show(supportFragmentManager)
            }
        }

        viewModelEAMX.validationDataActionFromActivity.observe(this) {
            when (it.statusValidation) {
                EAMXStatusValidation.CORRECT -> viewModelEAMX.requestSignUp(it.request)
                EAMXStatusValidation.INCORRECT -> {
                    UtilAlert.Builder().setMessage("Verifique sus datos").build()
                        .show(supportFragmentManager, "")

                }
            }
        }
        //Priest
        viewModelEAMX.responsePriest.observe(this) { response ->
            response.statusRequest.toString().log()
            when (response.statusRequest) {
                EAMXStatusRequestEnum.LOADING -> {
                    showProgressBarCustom()
                }
                EAMXStatusRequestEnum.SUCCESS -> {
                    hideProgressBarCustom()
                    if (!response.successData?.name.isNullOrEmpty()) {
                        response.successData?.let {
                            it.name.log()
                            it.fcappaterno.log()
                            it.fcapmaterno.log()
                            it.fccelular.log()
                            it.fccorreo.log()
                            etName.setText(response.successData!!.name)
                            rName.visibility = View.VISIBLE
                            etName.isEnabled = false
                            etLastNameMother.setText(response.successData!!.fcapmaterno)
                            rLasNameMother.visibility = View.VISIBLE
                            etLastNameMother.isEnabled = false
                            etLastNameFather.setText(response.successData!!.fcappaterno)
                            rLasNameFather.visibility = View.VISIBLE
                            etLastNameFather.isEnabled = false
                            etNumberPhone.setText(response.successData!!.fccelular)
                            rPhone.visibility = View.VISIBLE
                            etNumberPhone.isEnabled = false
                            etEmail.setText(response.successData!!.fccorreo)
                            rEmail.visibility = View.VISIBLE
                            rPassword.visibility = View.VISIBLE
                            rNewPassword.visibility = View.VISIBLE
                            switch1.visibility = View.GONE
                            btnEnviar.visibility = View.GONE
                            laySac.visibility = View.GONE
                            SWSacerdote.visibility = View.GONE
                            UpdateDataPriest.visibility = View.VISIBLE
                            btnRegistrar.visibility = View.VISIBLE
                            UpdateDataPriest.visibility = View.VISIBLE
                        }
                    } else {
                        UtilAlert.Builder().setTitle("¡Ups!")
                            .setMessage(getString(R.string.user_is_not_a_priest))
                            .build().show(supportFragmentManager, "")

                        tilNumberPhone.isEmpty()
                        etNumberPhone.setText("")
                        }
                }
                EAMXStatusRequestEnum.FAILURE -> {
                    hideProgressBarCustom()
                    response.errorData?.let { errorMessage ->
                        when (errorMessage) {
                            EAMXErrorResponseEnum.USER_NOT_PRIEST.messageError -> {
                                UtilAlert.Builder().setTitle("Aviso")
                                    .setMessage(getString(R.string.user_is_not_a_priest))
                                    .setListener {
                                        setResult(
                                            Activity.RESULT_CANCELED, intent.putExtra(
                                                EAMXEnumUser.USER_PHONE.name,
                                                response.successData?.fccelular
                                            )
                                        )
                                        EAMXRegisterActivity@ finish()
                                    }.build().show(supportFragmentManager, "")
                                return@let
                            }
                            else -> {
                                UtilAlert.Builder().setMessage(errorMessage).build()
                                    .show(supportFragmentManager, "")
                            }
                        }
                    }
                }
                EAMXStatusRequestEnum.NONE -> {
                    hideProgressBarCustom()
                }
            }
        }
        viewModelEAMX.openLoginFromActivity.observe(this) {
            if (!it.result) {
                setResult(Activity.RESULT_CANCELED)
            } else {
                UtilAlert.Builder()
                    .setMessage("Registro completo")
                    .setListener { _ ->
                        setResult(Activity.RESULT_OK, it.data)
                        finish()
                    }
                    .setIsCancel(false)
                    .build()
                    .show(supportFragmentManager)
            }
        }

        viewModelEAMX.validationDataActionFromActivityPr.observe(this) {
            when (it.statusValidation) {
                EAMXStatusValidation.CORRECT -> viewModelEAMX.requestPrestSignUp(it.request)
                EAMXStatusValidation.INCORRECT -> {
                    UtilAlert.Builder().setMessage("Verifique sus datos").build()
                        .show(supportFragmentManager, "")

                }
            }
        }
    }


    private fun String.validPass() =
        Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[.=+^$*.&{}()?\\[\\]!\\-@#%&/,><':;|_~`]).{8,}")
            .matcher(this).matches()

    private fun String.validNumberPhoneContent() =
        EAMXFieldValidation.validateNumberPhone(this) &&
                this.isNotEmpty() &&
                EAMXFieldValidation.validateNumberLength(this)


    private var formating = false
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initView() {
        mBinding.apply {

            etName.addTextChangedListener {
                if (etName.text.toString().isNotEmpty()) {
                    tilName.error = null
                    enableIconStart(tilName, true)
                } else {
                    tilName.error = getString(R.string.enter_your_name)
                    enableIconStart(tilName, null)
                }
            }

            etLastNameFather.addTextChangedListener {
                if (etLastNameFather.text.toString().isNotEmpty()) {
                    tilLastName.error = null
                    enableIconStart(tilLastName, true)
                } else {
                    enableIconStart(tilLastName, null)
                    tilLastName.error = getString(R.string.enter_your_paternal_last_name)
                }
            }

            etLastNameMother.addTextChangedListener {
                enableIconStart(tilLastNameMother, true)
                if (etLastNameMother.text.toString().isEmpty()) {
                    enableIconStart(tilLastNameMother, null)
                }
            }

            etNumberPhone.addTextChangedListener {
                enableIconStart(
                    tilNumberPhone,
                    etNumberPhone.text.toString().validNumberPhoneContent()
                )
                if (etNumberPhone.text.toString().isEmpty()) {
                    enableIconStart(tilNumberPhone, null)
                    tilNumberPhone.error = getString(R.string.enter_your_telephone_number)
                } else {
                    if (EAMXFieldValidation.validateNumberPhone(etNumberPhone.text.toString()) && EAMXFieldValidation.validateNumberLength(
                            etNumberPhone.text.toString()
                        )
                    ) {
                        tilNumberPhone.error = null
                    }
                    if (!EAMXFieldValidation.validateNumberPhone(etNumberPhone.text.toString())) {
                        tilNumberPhone.error = getString(R.string.wrong_phone_number)
                    }
                    if (!EAMXFieldValidation.validateNumberLength(etNumberPhone.text.toString())) {
                        tilNumberPhone.error = getString(R.string.min_phone)
                    }
                }
            }

            etEmail.addTextChangedListener {
                val text = it?.toString()
                text?.let { emailTxt ->
                    enableIconStart(tilEmail, Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches())
                    tilEmail.error = null
                    EAMXFieldValidation.validateEmail(emailTxt, tilEmail)
                    if (emailTxt.isEmpty()) {
                        enableIconStart(tilEmail, null)
                    }
                }

            }


            etPassword.addTextChangedListener {
                if (formating) return@addTextChangedListener
                val text = it?.toString()
                text?.let { passText ->
                    if (passText?.length != passText?.trim()?.length) {
                        formating = true
                        etPassword.setText(passText.trim())
                        formating = false
                    } else
//                    {
//                        enableIconStart(tilPassword, if (passText.isNullOrEmpty()) null else passText.validPass())
//                        enableIconStart(tilCodeConfirmPassword, if (passText.isNullOrEmpty()) null else passText == etConfirmPassword.text?.toString())
//                    }
                        EAMXFieldValidation.passValidation(
                            passText,
                            tilPassword,
                            etConfirmPassword.text.toString(),
                            tilCodeConfirmPassword,
                            "Ingresa una contraseña válida"
                        )
                    if (passText.isEmpty()) {
//                        enableIconStart(tilPassword, null)
                    }
                }


            }

            etConfirmPassword.addTextChangedListener {
                if (formating) return@addTextChangedListener
                val text = it?.toString()
                text?.let { confirmText ->
                    if (confirmText?.length != confirmText?.trim()?.length) {
                        formating = true
                        etConfirmPassword.setText(confirmText?.trim() ?: "")
                        formating = false
                    } else
//                    {
//                        enableIconStart(
//                            tilCodeConfirmPassword,
//                            if (confirmText.isNullOrEmpty()) null else confirmText == etPassword.text?.toString()
//                        )
//                    }
                        if (confirmText.isNotEmpty()) {
                            if (!confirmText.equals(etPassword.text.toString())) {
                                tilCodeConfirmPassword.error =
                                    "La confirmación de la contraseña debe ser igual a la contraseña"
                            } else {
                                tilCodeConfirmPassword.error = null
                            }
                        } else {
                            tilCodeConfirmPassword.error =
                                "Ingresa la confirmación de la contraseña"
                        }
                }

            }

            btnRegistrar.setOnClickListener { requestSignUp() }
            etDate.setOnClickListener { showDatePickerDialog() }
            toolbarModel.btnBack.setOnClickListener { finish() }
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

        }
            switch1.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    rName.visibility = View.GONE
                    rLasNameMother.visibility = View.GONE
                    rLasNameFather.visibility = View.GONE
                    rEmail.visibility = View.GONE
                    rPassword.visibility = View.GONE
                    rNewPassword.visibility = View.GONE
                    btnRegistrar.visibility = View.GONE
                    btnEnviar.visibility = View.VISIBLE
                    SWSacerdote.visibility = View.VISIBLE
                    switch1.thumbTintList = getColorStateList(R.color.green_retirar)

                } else {
                    rName.visibility = View.VISIBLE
                    rLasNameMother.visibility = View.VISIBLE
                    rLasNameFather.visibility = View.VISIBLE
                    rEmail.visibility = View.VISIBLE
                    rPassword.visibility = View.VISIBLE
                    rNewPassword.visibility = View.VISIBLE
                    btnRegistrar.visibility = View.VISIBLE
                    btnEnviar.visibility = View.GONE
                    laySac.visibility = View.VISIBLE
                    SWSacerdote.visibility = View.GONE
                    switch1.thumbTintList = getColorStateList(R.color.hint_color)

                    tilName.isEmpty()
                    tilLastNameMother.isEmpty()
                    tilLastName.isEmpty()
                    tilNumberPhone.isEmpty()
                    tilEmail.isEmpty()
                    tilPassword.isEmpty()
                    tilCodeConfirmPassword.isEmpty()
                }
                btnEnviar.setOnClickListener { requestPriestSignUp() }
            }
        }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun enableIconStart(input: TextInputLayout, success: Boolean?) {
        when (success) {
            true -> {
                input.endIconDrawable = getDrawable(R.drawable.ic_baseline_check_24)
                input.setEndIconTintList(ColorStateList.valueOf(getColor(R.color.success)))
            }
            false -> {
                input.endIconDrawable = getDrawable(R.drawable.ic_check_error)
                input.setEndIconTintList(ColorStateList.valueOf(getColor(R.color.error)))
            }
            null -> {
                input.endIconDrawable = null
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModelEAMX.nextToView(requestCode, resultCode, data)
    }
}