package mx.arquidiocesis.eamxcommonutils.util

import android.widget.EditText
import java.util.*

object EAMXEditText {
    fun firstUpperCase(receivedText: String): String {
        var complete = ""

        if (receivedText.isNotEmpty()) {
            val lowercaseTxt = receivedText.toLowerCase(Locale.ROOT)
            val arryLowr = lowercaseTxt.split(" ")
            arryLowr.forEach {
                complete += firCharacterUpperCase(it) + " "
            }
        }

        return complete.trim()
    }

    fun validaMin(editText: EditText, min: Int): Boolean {
        return if (editText.text.isNullOrEmpty()) {
            false
        } else {
            val number = editText.text.toString().replace("\n", "").replace(" ", "").length
            min < number
        }
    }

    private fun firCharacterUpperCase(str: String): String {
        var strUpp = ""
        if (str.isNotEmpty()) {
            strUpp = str.trim().replaceRange(0, 1, str.trim()[0].toUpperCase().toString())
        }
        return strUpp
    }
}