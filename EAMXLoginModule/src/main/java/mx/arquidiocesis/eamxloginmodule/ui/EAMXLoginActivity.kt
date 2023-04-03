package mx.arquidiocesis.eamxloginmodule.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.eamxl_login_activity.*
import mx.arquidiocesis.eamxcommonutils.api.core.errorresponse.EAMXErrorResponseEnum
import mx.arquidiocesis.eamxcommonutils.api.core.status.EAMXStatusRequestEnum
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.application.ConstansApp
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXInternetAvailability
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXStatusValidation
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXValidationModel
import mx.arquidiocesis.eamxcommonutils.common.EAMXBaseActivity
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnums
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.*
import mx.arquidiocesis.eamxcommonutils.util.repositoryrefreshtoken.model.EAMXUserLoginRequest
import mx.arquidiocesis.eamxloginmodule.R
import mx.arquidiocesis.eamxloginmodule.databinding.EamxlLoginActivityBinding
import mx.arquidiocesis.eamxprofilemodule.repository.RepositoryProfile
import mx.arquidiocesis.eamxprofilemodule.viewmodel.EAMXViewModelProfile
import mx.arquidiocesis.eamxregistromodule.ui.confirm.EAMXConfirmCodeActivity
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.ui.EAMXManagerForgotActivity
import mx.arquidiocesis.eamxregistromodule.ui.register.EAMXRegisterActivity
import java.util.concurrent.Executor


class EAMXLoginActivity : EAMXBaseActivity() {

    lateinit var mBinding: EamxlLoginActivityBinding
    lateinit var viewModel: EAMXLoginViewModel
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var bio = false
    private var doubleBackToExitPressedOnce = false
    override fun getLayout() = R.layout.eamxl_login_activity

    override fun initBinding(view: Int): View {
        mBinding = EamxlLoginActivityBinding.inflate(layoutInflater)
        return mBinding.root
    }

    private val viewModelProfile: EAMXViewModelProfile by lazy {
        getViewModel {
            EAMXViewModelProfile(
                RepositoryProfile(this)
            )
        }
    }

    override fun setViewModel() {
        viewModel = ViewModelProvider(this).get(EAMXLoginViewModel::class.java)
    }

    override fun initDependency() {}

    override fun initObservers() {
        viewModelProfile.responseUserDetail.observe(this) { dataUser ->
            hideProgressBarCustom()
            "FINALIZO GETDETAIL ${msgGuest(isMsg = false)}".log()
            eamxcu_preferences.apply {
                dataUser.data.User.let { data ->
                    saveData(EAMXEnumUser.USER_ID.name, data.id)
                    saveData(EAMXEnumUser.USER_NAME.name, data.name)
                    saveData(EAMXEnumUser.USER_LAST_NAME.name, data.first_surname)
                    saveData(EAMXEnumUser.USER_MIDDLE_NAME.name, data.second_surname ?: "")
                    saveData(EAMXEnumUser.USER_ROLE.name, data.life_status?.name ?: "")
                    saveData(EAMXEnumUser.USER_PROFILE.name, data.profile ?: "")
                    saveData(EAMXEnumUser.BIRTHDATE.name, data.birthdate ?: "")
                    saveData(EAMXEnumUser.ORDINATION_DATE.name, data.ordination_day ?: "")
                    saveData(EAMXEnumUser.STREAM.name, data.stream ?: "")
                    saveData(EAMXEnumUser.DESCRIPTION.name, data.description ?: "")
                    saveData(EAMXEnumUser.USER_EMAIL.name, data.email)
                    saveData(EAMXEnumUser.USER_PHONE.name, data.phone_number)
                    saveData(EAMXEnumUser.USER_NEED_COMPLETE_PROFILE.name, data.life_status == null)
                }
            }
            setResult(Activity.RESULT_OK, intent.putExtra(EAMXEnums.CONFIRMATED.name, true))
            finish()
        }
        viewModel.responseGeneric.observe(this) { response ->
            when (response.statusRequest) {
                EAMXStatusRequestEnum.LOADING -> {
                    showProgressBarCustom()
                }
                EAMXStatusRequestEnum.SUCCESS -> {
                    "INICIO GETDETAIL".log()
                    eamxcu_preferences.apply {
                        if (!bio) {
                            val number = getData(
                                EAMXEnumUser.USER_PHONE.toString(),
                                EAMXTypeObject.STRING_OBJECT
                            ) as String
                            val email = getData(
                                EAMXEnumUser.USER_EMAIL.toString(),
                                EAMXTypeObject.STRING_OBJECT
                            ) as String
                            val actUse = etEmail.text.toString()
                            if (number != actUse || email != actUse) {
                                var guest_aut = getData(
                                    EAMXEnumUser.GUEST_AUTH.name,
                                    EAMXTypeObject.BOOLEAN_OBJECT
                                ) as Boolean
                                removeFile()
                                saveData(
                                    EAMXEnumUser.USER_PASSWORD.name,
                                    etPassword.text.toString()
                                )
                                saveData(EAMXEnumUser.GUEST.name, guest_aut)
                            }
                        }
                        response.successData?.UserAttributes?.apply {
                            saveData(EAMXEnumUser.USER_ID.name, id)
                        }
                        response.successData?.AuthenticationResult?.let {
                            saveData(EAMXEnumUser.TOKEN_CUSTOMER.name, it.IdToken)
                            saveData(EAMXEnumUser.TOKEN_REFRESH_CUSTOMER.name, it.RefreshToken)
                        }
                    }
                    viewModelProfile.getUserDetail()
                }
                EAMXStatusRequestEnum.FAILURE -> {
                    hideProgressBarCustom()
                    eamxLog("Error_Data_Login", response.errorData.toString())
                    eamxLog(
                        "Error_Data_Login",
                        EAMXErrorResponseEnum.USER_IS_NOT_CONFIRMED.messageError
                    )
                    eamxLog(
                        "Error_Data_Login",
                        "${response.errorData.toString() == EAMXErrorResponseEnum.USER_IS_NOT_CONFIRMED.messageError}"
                    )
                    if (EAMXInternetAvailability.isNetworkAvailable(this@EAMXLoginActivity)) {

                        when (response.errorData) {
                            EAMXErrorResponseEnum.USER_IS_NOT_CONFIRMED.messageError,
                            EAMXErrorResponseEnum.USER_IS_NOT_CONFIRMED_QA.messageError,
                            -> {
                                val userValue = if (mBinding.etEmail.text.toString().isDigitsOnly())
                                    "+52${mBinding.etEmail.text}" else mBinding.etEmail.text.toString()
                                UtilAlert.Builder()
                                    .setTitle("Atención")
                                    .setMessage(getString(R.string.your_user_already_exists_confirm_to_login))
                                    .setTextButtonOk("Solicitar código")
                                    .setListener {
                                        if (it.toString() == "ACCEPT") {
                                            startActivity(
                                                Intent(this, EAMXConfirmCodeActivity::class.java)
                                                    .putExtra(
                                                        EAMXEnumUser.USER_EMAIL.name,
                                                        userValue
                                                    )
                                                    .putExtra(
                                                        AppMyConstants.RESEND_CODE_END_POINT,
                                                        AppMyConstants.RESEND_CODE_END_POINT
                                                    )
                                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            )
                                        }
                                    }
                                    .build().show(supportFragmentManager, "")
                            }
                            else -> {
                                UtilAlert.Builder()
                                    .setTitle("Atención")
                                    .setMessage(getString(R.string.friendly_message))
                                    .build().show(supportFragmentManager, "")
                            }
                        }
                    } else {
                        UtilAlert.Builder()
                            .setTitle("Atención")
                            .setMessage(getString(R.string.password_invalid))
                            .build().show(supportFragmentManager, "")
                    }
                }
                EAMXStatusRequestEnum.NONE -> {
                    hideProgressBarCustom()
                    if (eamxcu_preferences.getData(
                            EAMXEnumUser.GUEST_AUTH.name,
                            EAMXTypeObject.BOOLEAN_OBJECT
                        ) as Boolean
                    ) {
                        mBinding.etEmail.setText("")
                        mBinding.etPassword.setText("")
                        eamxcu_preferences.saveData(EAMXEnumUser.GUEST_AUTH.name, false)
                    }
                }
            }
        }
        viewModel.openSplashFromActivity.observe(this) {
            if (!it.result) {
                setResult(Activity.RESULT_CANCELED)
                val email = it.data?.getStringExtra(EAMXEnumUser.USER_EMAIL.name)
                mBinding.etEmail.setText(email)
            } else {
                it.data?.let { intent ->
                    setResult(Activity.RESULT_OK, intent)
                }
                finish()
            }
        }
        viewModel.validationDataActionFromActivity.observe(this) {
            when (it.statusValidation) {
                EAMXStatusValidation.CORRECT -> {
                    val requestOk = if (it.request.username.isDigitsOnly()) {
                        EAMXUserLoginRequest("+52${it.request.username}", it.request.password)
                    } else {
                        it.request
                    }
                    viewModel.requestSignIn(requestOk)
                }
                EAMXStatusValidation.INCORRECT -> {
                    UtilAlert.Builder()
                        .setTitle("Atención")
                        .setMessage(it.errorMessage)
                        .build().show(supportFragmentManager, "")
                }
            }
        }
    }

    override fun initView() {
        hideLogin()
        btnRegistrar.setOnClickListener {
            startActivityForResult(
                Intent(
                    this@EAMXLoginActivity,
                    EAMXRegisterActivity::class.java
                ), EAMXEnums.CONFIRM_CODE.code
            )
        }
        btnLogin.setOnClickListener {
            showLogin()
        }
        btnContinuarSinRegistro.setOnClickListener {
            ingresoGUEST()
        }
        mBinding.apply {
            mBinding.etEmail.setText("")
            mBinding.etPassword.setText("")
            if ((eamxcu_preferences.getData(
                    EAMXEnumUser.USER_PASSWORD.toString(),
                    EAMXTypeObject.STRING_OBJECT
                ) as String).trim().isEmpty()
            ) {
                //highlightButton(btnRegistrar)
                ingresoGUEST()
            } else {
                highlightButton(btnLogin)
                tvBiometric.visibility = if (!msgGuest(isMsg = false)) View.VISIBLE else View.GONE
                hideLogin()
            }
            btnIngresar.setOnClickListener {
                tvBiometric.visibility = if (!msgGuest(isMsg = false)) View.VISIBLE else View.GONE
                ingresar()
                EAMXFirebaseManager(applicationContext).setLogEvent("screen_view", Bundle().apply {
                    putString("screen_class", "Login_Login")
                })
            }
            tvForgotPassword.setOnClickListener {
                mBinding.etEmail.setText("")
                mBinding.etPassword.setText("")
                startActivityForResult(
                    Intent(
                        this@EAMXLoginActivity,
                        EAMXManagerForgotActivity::class.java
                    ), EAMXEnums.CONFIRM_CODE.code
                )
            }
            toolbarModelLogin.btnBack.setOnClickListener { finish() }
        }
    }

    private fun ingresoGUEST() {
        eamxcu_preferences.saveData(EAMXEnumUser.GUEST_AUTH.name, true)
        // Access a Cloud Firestore instance from your Activity
        val db = Firebase.firestore
        db.collection(ConstansApp.usrDummy())
            .whereEqualTo("activo", true)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    ("${document.id} => ${document.data["usuario"]}").log()
                    mBinding.apply {
                        etEmail.setText(document.data["usuario"].toString())
                        etPassword.setText(document.data["contrasena"].toString())
                        ingresar()
                    }
                }
            }
            .addOnFailureListener { exception ->
                ("Error getting documents." + exception).log()
            }
    }

    private fun ingresar() {
        if (EAMXInternetAvailability.isNetworkAvailable(this@EAMXLoginActivity)) {
            requestSignUp()
        } else {
            if (msgGuest(isMsg = false)) {
                etEmail.setText("")
                etPassword.setText("")
            }
            UtilAlert.Builder()
                .setTitle("Atención")
                .setMessage(getString(R.string.no_internet_connection))
                .build().show(supportFragmentManager, "")
        }
    }

    fun biometric() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                tvBiometric.visibility = View.VISIBLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                tvBiometric.visibility = View.GONE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                tvBiometric.visibility = View.GONE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                tvBiometric.visibility = View.GONE
            }
        }
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence,
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext, "$errString",
                        Toast.LENGTH_SHORT
                    ).show()
                    tvBiometric.visibility = View.GONE
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult,
                ) {
                    super.onAuthenticationSucceeded(result)
                    val pass = eamxcu_preferences.getData(
                        EAMXEnumUser.USER_PASSWORD.toString(),
                        EAMXTypeObject.STRING_OBJECT
                    ) as String
                    val number = eamxcu_preferences.getData(
                        EAMXEnumUser.USER_PHONE.toString(),
                        EAMXTypeObject.STRING_OBJECT
                    ) as String
                    val arraylistValidations: ArrayList<EAMXValidationModel> = ArrayList()
                    bio = true
                    viewModel.requestValidationSingIn(
                        EAMXUserLoginRequest(number, pass),
                        arraylistValidations
                    )
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext, "Autenticación fallida",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Inicio de sesión biométrico ")
            .setSubtitle("Inicie sesión con su credencial biométrica")
            .setNegativeButtonText("Usar contraseña de cuenta")
            .build()
    }


    fun hideLogin() {
        mBinding.apply {
            linearLayout.visibility = View.GONE
            linearLayout4.visibility = View.GONE
            tvForgotPassword.visibility = View.GONE
            btnIngresar.visibility = View.GONE
            btnLogin.visibility = View.VISIBLE
            btnRegistrar.visibility = View.VISIBLE
            btnContinuarSinRegistro.visibility = View.VISIBLE
            lnlNoAccount.visibility = View.VISIBLE
            linearLayoutDetails.visibility = View.VISIBLE
            toolbarModelLogin.btnBack.visibility = View.GONE
            textView5.setText(R.string.welcome)
            textViewIngresar.visibility = View.VISIBLE
            textView15.visibility = View.GONE
            tvBiometric.visibility = View.GONE
        }
    }

    fun showLogin() {
        eamxcu_preferences.saveData(EAMXEnumUser.GUEST_AUTH.name, false)
        val pass = eamxcu_preferences.getData(
            EAMXEnumUser.USER_PASSWORD.toString(),
            EAMXTypeObject.STRING_OBJECT
        ) as String
        val guest = msgGuest(isMsg = false)
        if (pass.isNotEmpty() && !guest) {
            biometric()
            tvBiometric.setOnClickListener {
                biometricPrompt.authenticate(promptInfo)
            }
        }
        mBinding.apply {
            textView5.visibility = View.VISIBLE
            textView15.visibility = View.VISIBLE
            linearLayout.visibility = View.VISIBLE
            linearLayout4.visibility = View.VISIBLE
            tvForgotPassword.visibility = View.VISIBLE
            btnIngresar.visibility = View.VISIBLE
            linearLayoutDetails.visibility = View.GONE
            btnLogin.visibility = View.GONE
            lnlNoAccount.visibility = View.GONE
            toolbarModelLogin.btnBack.visibility = View.VISIBLE
            toolbarModelLogin.btnBack.setOnClickListener { hideLogin() }
            textViewIngresar.visibility = View.GONE
            btnRegistrar.visibility = View.GONE
            btnContinuarSinRegistro.visibility = View.GONE
            textView5.setText(R.string.sign_in_login)
            textView15.setText(R.string.nice_to_see_you_again)
            tvBiometric.visibility =
                if (pass.isEmpty()) View.GONE else if (!guest) View.VISIBLE else View.GONE
        }
    }

    fun highlightButton(button: Button) {
        button.apply {
            button.setBackgroundColor(Color.parseColor("#002166"))
            button.setTextColor(Color.parseColor("#FFFFFFFF"))
        }
    }

    override fun onBackPressed() {
        hideLogin()
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, R.string.back_pressed, Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

    override fun onStop() {
        super.onStop()
        //msgGuest(isMsg = false) &&
        if (!(eamxcu_preferences.getData(
                EAMXEnumUser.SESSION.name,
                EAMXTypeObject.BOOLEAN_OBJECT
            ) as Boolean)
        ) {
            eamxcu_preferences.removeFile()
            return
        }
    }
}