package mx.arquidiocesis.eamxregistromodule

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertTrue(passwordValidation("Kikedev01."))
        assertTrue(passwordValidation("Qwerty123!."))
        assertTrue(passwordValidation("Kikedev0."))
        assertTrue(passwordValidation("Kikedev0="))
        assertTrue(passwordValidation("Kikedev0+"))
        assertTrue(passwordValidation("Kikedev0-"))
        assertTrue(passwordValidation("Kikedev0@"))
        assertTrue(passwordValidation("Kikedev0#"))
        assertTrue(passwordValidation("Kikedev0%"))
        assertTrue(passwordValidation("Kikedev0%"))
        assertTrue(passwordValidation("Kikedev0&"))
        assertTrue(passwordValidation("Kikedev0>"))
        assertTrue(passwordValidation("Kikedev0<"))
        assertTrue(passwordValidation("Kikedev0'"))
        assertTrue(passwordValidation("Kikedev0:"))
        assertTrue(passwordValidation("Kikedev0;"))
        assertTrue(passwordValidation("Kikede0."))
        assertTrue(passwordValidation("Kikede0$"))
        assertTrue(passwordValidation("Kikede0/"))
        assertTrue(passwordValidation("Kikede0["))
        assertTrue(passwordValidation("Kikede0]"))
        assertTrue(passwordValidation("Kikede0("))
        assertTrue(passwordValidation("Kikede0^"))
        assertTrue(passwordValidation("Kikede0*"))
        assertTrue(passwordValidation("Kikede0|"))
        assertTrue(passwordValidation("Kikede0~"))
        assertTrue(passwordValidation("Kikede0`"))
        assertTrue(passwordValidation("Kikede0'"))
        assertTrue(passwordValidation("Kikede0)"))
    }

    private fun passwordValidation(password: String): Boolean {
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

        println("Cantidad de letras mayusculas:$containToUpper")
        println("Cantidad de letras numeros:$containNumber")
        println("Cantidad de letras especiales:$containEspecial")

        return containToUpper >= 1.toByte() && containNumber >= 1.toByte() && containEspecial >= 1.toByte()
    }
}