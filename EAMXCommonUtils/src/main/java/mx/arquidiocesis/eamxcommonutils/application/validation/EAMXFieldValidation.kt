package mx.arquidiocesis.eamxcommonutils.application.validation

import android.content.Context
import android.util.Log
import android.util.Patterns
import com.google.android.material.textfield.TextInputLayout
import mx.arquidiocesis.eamxcommonutils.R

class EAMXFieldValidation {
    companion object {
        fun validateNumberPhone(number: String): Boolean {
            val regex = "d{10}$"
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher("")
            matcher.matches()

            return !number.equals("0000000000") &&
                    !number.equals("1111111111") &&
                    !number.equals("2222222222") &&
                    !number.equals("3333333333") &&
                    !number.equals("4444444444") &&
                    !number.equals("5555555555") &&
                    !number.equals("6666666666") &&
                    !number.equals("7777777777") &&
                    !number.equals("8888888888") &&
                    !number.equals("9999999999")
        }

        fun validateNumberLength(number: String): Boolean {
            return number.length == 10
        }

        fun passValidation(
            string: String,
            til: TextInputLayout,
            confirmString: String,
            tilConfirm: TextInputLayout,
            errorMessage: String
        ) {
            if (string.isEmpty()) {
                til.error = errorMessage

            } else {

                if (string.isNotEmpty() && string.length < 8) {
                    til.error = "La contraseña debe contener mínimo 8 caracteres"
                } else {
                    if (!passwordValidation(string)) {
                        Log.d("PassChanged", "passvalidation : ${getPasswordValidationMessage(string)}")
                        til.error = getPasswordValidationMessage(string)
                    } else {
                        til.error = null
                    }
                }

//                (if (string.isNotEmpty() && string.length < 8) "La contraseña debe ser de mínimo 8 caracteres" else null).also { til.error = it }
//                (if (!passwordValidation(string)) "Ingresa una contraseña válida" else null).also { til.error = it }
                if (confirmString == string) {
                    tilConfirm.error = null
                }

            }
        }

        fun validateEmail(text: String, til: TextInputLayout) {
            if (text.isEmpty()) {
                til.error = "Ingresa tu correo electrónico"
            } else {
                if (text.length < 10) {
                    til.error = "El correo debe ser de mínimo 10 caracteres"
                } else {
                    (if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()) "Inserta un correo electrónico válido" else null).also { til.error = it }
                }
            }
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

        fun getPasswordValidationMessage(password: String): String {
            var clave: Char
            var containNumber: Byte = 0
            var containToUpper: Byte = 0
            var containEspecial: Byte = 0
            var containLower: Byte = 0
            var message = ""

            for (element in password) {
                clave = element
                val passValue = clave.toString()
                when {
                    passValue.matches("[A-Z]".toRegex()) -> {
                        containToUpper++
                    }
                    passValue.matches("[a-z]".toRegex()) -> {
                        containLower++
                    }
                    passValue.matches("[0-9]".toRegex()) -> {
                        containNumber++
                    }
//                    passValue.matches("[=+^\$*.\\[\\]\\{}()?\\\"!-?@#%&/\\,><':;|_~`]".toRegex()) -> {
                    passValue.matches("[\\^\\$\\*\\.\\[\\]\\{\\}\\(\\)\\?\"\\!@#\\%&\\/\\\\,><':;|_~`=+-]".toRegex()) -> {
                        containEspecial++
                    }
                }
            }
            if (containLower < 1.toByte()) {
                return "Ingresa una minúscula"
            }
            if (containToUpper < 1.toByte()) {
                return "Ingresa una mayúscula"
            }
            if (containNumber < 1.toByte()) {
                return "Ingresa un número"
            }
            if (containEspecial < 1.toByte()) {
                return "La contraseña debe contener al menos un carácter especial (^ \$ * . [ ] { } ( ) ? \" ! @ # % & / \\ , > < ' : ; | _ ~ ` = + -)"
            }
            return message

        }

        fun validateRegex(){
            // characteres * . [ ] { } ( ) ? " ! - @ # % & / , > < ' : ; | _ ~ ` \
            val regex = "[=+^\$*.\\[\\]\\{}()?\\\"!-?@#%&/\\,><':;|_~`]"

        }

    }
}