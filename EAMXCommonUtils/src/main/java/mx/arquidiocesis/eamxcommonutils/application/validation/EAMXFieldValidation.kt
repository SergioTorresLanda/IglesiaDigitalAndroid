package mx.arquidiocesis.eamxcommonutils.application.validation

import android.content.Context
import android.util.Patterns
import com.google.android.material.textfield.TextInputLayout
import mx.arquidiocesis.eamxcommonutils.R

class EAMXFieldValidation {
    companion object {
        fun validateNumberPhone(number: String): Boolean{
            return  !number.equals("0000000000") &&
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

        fun validateNumberLength(number: String): Boolean{
            return number.length == 10
        }

        fun passValidation(string: String,
                           til: TextInputLayout,
                           confirmString: String,
                           tilConfirm: TextInputLayout,
        errorMessage: String){
            if(string.isEmpty()){
                til.error = errorMessage

            }else{
                (if(string.isNotEmpty() && string.length < 8) "La contraseña debe ser de mínimo 8 caracteres" else null).also { til.error = it }
                (if(!passwordValidation(string)) "Ingresa una contraseña válida" else null).also { til.error = it }
                if(confirmString == string){
                    tilConfirm.error = null
                }

            }
        }

        fun validateEmail(text: String, til: TextInputLayout){
            if(text.isEmpty()) {
                til.error = "Ingresa tu correo electrónico"
            }else{
                if(text.length < 10){
                    til.error = "El correo debe ser de mínimo 10 caracteres"
                }else {
                    (if(!Patterns.EMAIL_ADDRESS.matcher(text).matches()) "Inserta un correo electrónico válido" else null).also { til.error = it }
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

    }
}