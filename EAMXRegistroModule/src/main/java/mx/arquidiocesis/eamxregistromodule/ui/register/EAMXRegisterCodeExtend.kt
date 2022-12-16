package mx.arquidiocesis.eamxregistromodule.ui.register

import android.util.Patterns
import kotlinx.android.synthetic.main.eamxr_register_activity.*
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXFieldValidation
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXTypeValidation
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXValidationModel
import mx.arquidiocesis.eamxcommonutils.base.DatePickerFragment
import mx.arquidiocesis.eamxcommonutils.util.EAMXEditText
import mx.arquidiocesis.eamxregistromodule.R
import mx.arquidiocesis.eamxregistromodule.ui.register.model.EAMXSignUpRequest
import java.util.Date

fun EAMXRegisterActivity.requestSignUp() {
    lateinit var name: String
    lateinit var lastName: String
    lateinit var middleName: String
    lateinit var cumple: String
    lateinit var email: String
    lateinit var numberPhone: String
    lateinit var phone: String
    lateinit var pws: String
    mBinding.apply {
        name = EAMXEditText.firstUpperCase(etName.text.toString().trimEnd())
        lastName = EAMXEditText.firstUpperCase(etLastNameFather.text.toString().trimEnd())
        middleName = EAMXEditText.firstUpperCase(etLastNameMother.text.toString().trimEnd())
        cumple = etDate.text.toString()
        email = etEmail.text.toString()
        numberPhone = etNumberPhone.text.toString()
        phone = "+52$numberPhone"
        pws = etPassword.text.toString()
        val arraylistValidations: ArrayList<EAMXValidationModel> = ArrayList()


        (if(etName.text.toString().isEmpty()) getString(R.string.enter_your_name) else null).also { tilName.error = it }
        (if(etLastNameFather.text.toString().isEmpty()) getString(R.string.enter_your_paternal_last_name) else null).also { tilLastName.error = it }

        if(etNumberPhone.text.toString().isEmpty()){
            tilNumberPhone.error = getString(R.string.enter_your_telephone_number)
        }else {
            if(!EAMXFieldValidation.validateNumberLength(etNumberPhone.text.toString())){
                tilNumberPhone.error = getString(R.string.min_phone)
            }

            if(!EAMXFieldValidation.validateNumberPhone(etNumberPhone.text.toString())){
                tilNumberPhone.error = getString(R.string.wrong_phone_number)
            }

        }
      //  (if(etDate.text.toString().isEmpty()) getString(R.string.insert_hbd) else null).also { tilDate.error = it }
        if(etEmail.text.toString().isEmpty()) {
            tilEmail.error = "Ingresa tu correo electrónico"
        }else{
            (if(!Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()) "Inserta un correo electrónico válido" else null).also { tilEmail.error = it }
        }

        if(etPassword.text.toString().isEmpty()){
            tilPassword.error = getString(R.string.enter_your_password)
        }else{
            (if(etPassword.text.toString().isNotEmpty() && etPassword.text.toString().length < 8) "La contraseña debe ser de mínimo 8 caracteres" else null).also { tilPassword.error = it }
            (if(!passwordValidation(etPassword.text.toString())) "Ingresa una contraseña válida" else null).also { tilPassword.error = it }

            (if(etPassword.text.toString() != etConfirmPassword.text.toString()) "Ingresa la confirmación de tu contraseña" else null).also { tilCodeConfirmPassword.error = it }
        }
        if(etConfirmPassword.text.toString().isEmpty()){
            tilCodeConfirmPassword.error = "Ingresa la confirmación de la contraseña"
        }


        arraylistValidations.apply {
            add(
                EAMXValidationModel(
                    etName,
                    EAMXTypeValidation.NOT_EMPTY,
                    true,
                    getString(R.string.enter_your_name)
                )
            )
            add(
                EAMXValidationModel(
                    etLastNameFather,
                    EAMXTypeValidation.NOT_EMPTY,
                    true,
                    getString(R.string.enter_your_paternal_last_name)
                )
            )

            add(
                EAMXValidationModel(
                    etNumberPhone,
                    EAMXTypeValidation.NOT_EMPTY,
                    true,
                    getString(R.string.enter_your_telephone_number)
                )
            )
            add(
                EAMXValidationModel(
                    etNumberPhone,
                    EAMXTypeValidation.EQUALS_FIELD,
                    "0000000000",
                    getString(R.string.wrong_phone_number)
                )
            )
            add(
                EAMXValidationModel(
                    etNumberPhone,
                    EAMXTypeValidation.EQUALS_FIELD,
                    "1111111111",
                    getString(R.string.wrong_phone_number)
                )
            )
            add(
                EAMXValidationModel(
                    etNumberPhone,
                    EAMXTypeValidation.EQUALS_FIELD,
                    "2222222222",
                    getString(R.string.wrong_phone_number)
                )
            )
            add(
                EAMXValidationModel(
                    etNumberPhone,
                    EAMXTypeValidation.EQUALS_FIELD,
                    "3333333333",
                    getString(R.string.wrong_phone_number)
                )
            )
            add(
                EAMXValidationModel(
                    etNumberPhone,
                    EAMXTypeValidation.EQUALS_FIELD,
                    "4444444444",
                    getString(R.string.wrong_phone_number)
                )
            )
            add(
                EAMXValidationModel(
                    etNumberPhone,
                    EAMXTypeValidation.EQUALS_FIELD,
                    "5555555555",
                    getString(R.string.wrong_phone_number)
                )
            )
            add(
                EAMXValidationModel(
                    etNumberPhone,
                    EAMXTypeValidation.EQUALS_FIELD,
                    "6666666666",
                    getString(R.string.wrong_phone_number)
                )
            )
            add(
                EAMXValidationModel(
                    etNumberPhone,
                    EAMXTypeValidation.EQUALS_FIELD,
                    "9999999999",
                    getString(R.string.wrong_phone_number)
                )
            )
            add(
                EAMXValidationModel(
                    etNumberPhone,
                    EAMXTypeValidation.EQUALS_FIELD,
                    "7777777777",
                    getString(R.string.wrong_phone_number)
                )
            )
            add(
                EAMXValidationModel(
                    etNumberPhone,
                    EAMXTypeValidation.EQUALS_FIELD,
                    "8888888888",
                    getString(R.string.wrong_phone_number)
                )
            )
            add(
                EAMXValidationModel(
                    etNumberPhone,
                    EAMXTypeValidation.MIN_VALUE,
                    10,
                    getString(R.string.min_phone)
                )
            )


            if (etEmail.text.toString().isEmpty()) {
                add(
                    EAMXValidationModel(
                        etEmail,
                        EAMXTypeValidation.NOT_EMPTY,
                        true,
                        "Ingresa tu correo electrónico"
                    )
                )
            } else {
                add(
                    EAMXValidationModel(
                        etEmail,
                        EAMXTypeValidation.EMAIL_VALID,
                        true,
                        "Ingresa un correo electrónico válido"
                    )
                )
            }


            if (etPassword.text.toString().isEmpty()) {
                add(
                    EAMXValidationModel(
                        etPassword,
                        EAMXTypeValidation.NOT_EMPTY,
                        true,
                        getString(R.string.enter_your_password)
                    )
                )
            }

            if (etPassword.text.toString().length < 8) {
                add(
                    EAMXValidationModel(
                        etPassword,
                        EAMXTypeValidation.MIN_VALUE,
                        8,
                        "La contraseña debe ser de mínimo 8 caracteres"
                    )
                )
            } else if (!passwordValidation(etPassword.text.toString())) {
                add(
                    EAMXValidationModel(
                        etPassword,
                        EAMXTypeValidation.MIN_VALUE,
                        8,
                        "La contraseña debe ser de mínimo 8 caracteres"
                    )
                )

                add(
                    EAMXValidationModel(
                        etPassword,
                        EAMXTypeValidation.MAX_VALUE,
                        16,
                        "La contraseña debe ser de maximo 16 caracteres"
                    )
                )
            }

            if (etPassword.text.toString() != etConfirmPassword.text.toString()) {
                add(
                    EAMXValidationModel(
                        etConfirmPassword,
                        EAMXTypeValidation.EQUALS,
                        etPassword,
                        "Ingresa la confirmación de tu contraseña"
                    )
                )
            }
        }
        viewModelEAMX.requestValidationRegister(
            EAMXSignUpRequest(
                email,
                pws,
                email,
                phone,
                name,
                lastName,
                middleName,
                getString(R.string.faithful),
            ), arraylistValidations
        )
    }
}

fun EAMXRegisterActivity.showDatePickerDialog() {
    val currentDate = Date()
    val maxDate = Date((currentDate.year - 8), currentDate.month, currentDate.date)
    val datePicker =
        DatePickerFragment(maxDate = maxDate) { day, month, year ->
            onDaySelected(
                day,
                month,
                year,
            )
        }
    datePicker.show(supportFragmentManager, "datePicker")
}

fun EAMXRegisterActivity.onDaySelected(day: Int, month: Int, year: Int) {
    val dayCurrent = if (day < 10) "0$day" else day
    val monthCurrent = month + 1
    val montCurrent = if (month < 9) "0$monthCurrent" else monthCurrent
    mBinding.etDate.setText("$dayCurrent/$montCurrent/$year")
    mBinding.etNumberPhone.clearFocus()
    enableIconStart(mBinding.tilDate,true)
    mBinding.tilDate.error = null
}



fun passwordValidation(password: String): Boolean {
    var clave: Char
    var containNumber: Byte = 0
    var containToUpper: Byte = 0
    var containEspecial: Byte = 0

    for (element in password) {
        clave = element
        val passValue = clave.toString()
        when {
            passValue.matches("[A-Z]".toRegex()) -> {
                containToUpper++
            }
            passValue.matches("[0-9]".toRegex()) -> {
                containNumber++
            }
            passValue.matches("[=+^\$*.\\[\\]\\{}()?\\\"!-?@#%&/\\,><':;|_~`]".toRegex()) -> {
                containEspecial++
            }
        }
    }

    return containToUpper >= 1.toByte() && containNumber >= 1.toByte() && containEspecial >= 1.toByte()
}
