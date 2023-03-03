package mx.arquidiocesis.eamxloginmodule.ui

import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXTypeValidation
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXValidationModel
import mx.arquidiocesis.eamxloginmodule.R
import mx.arquidiocesis.eamxcommonutils.util.repositoryrefreshtoken.model.EAMXUserLoginRequest

fun EAMXLoginActivity.requestSignUp() {
    lateinit var pws: String
    lateinit var email: String

    mBinding.apply {
        email = etEmail.text.toString()
        pws = etPassword.text.toString()
        val arraylistValidations: ArrayList<EAMXValidationModel> = ArrayList()
        arraylistValidations.add(EAMXValidationModel(etEmail, EAMXTypeValidation.NOT_EMPTY, true, getString(R.string.enter_your_email)))
        arraylistValidations.add(EAMXValidationModel(etPassword, EAMXTypeValidation.NOT_EMPTY, true, getString(R.string.enter_your_password)))
        val pattern = Regex("[0-9]*")
        if(etEmail.text.toString().isNotEmpty()) {
            if (etEmail.text.toString().matches(pattern)) {
                arraylistValidations.add(
                    EAMXValidationModel(
                        etEmail,
                        EAMXTypeValidation.MAX_VALUE,
                        10,
                        getString(R.string.enter_a_number_phone_valid)
                    )
                )
                arraylistValidations.add(
                    EAMXValidationModel(
                        etEmail,
                        EAMXTypeValidation.MIN_VALUE,
                        10,
                        getString(R.string.enter_a_number_phone_valid)
                    )
                )
            } else {
                arraylistValidations.add(
                    EAMXValidationModel(
                        etEmail,
                        EAMXTypeValidation.EMAIL_VALID,
                        true,
                        getString(R.string.enter_a_valid_email)
                    )
                )
            }
        }
        viewModel.requestValidationSingIn(EAMXUserLoginRequest(email, pws), arraylistValidations)
    }
}
