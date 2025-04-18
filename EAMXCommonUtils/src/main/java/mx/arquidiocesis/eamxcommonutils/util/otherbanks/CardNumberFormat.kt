package mx.arquidiocesis.eamxcommonutils.util.otherbanks

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher

class CardNumberFormat(private val numberDigits: Int, private val separator: String, textSizeMax: Int) : TextWatcher{
    private var current = ""
    private val textSize = textSizeMax

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable) {
        if (s.toString() != current) {
            val userInput = s.toString().replace(nonDigits, "")
            if (userInput.length <= textSize) {
                current = userInput.chunked(numberDigits).joinToString(separator)
                s.filters = arrayOfNulls<InputFilter>(0)
            }
            s.replace(0, s.length, current, 0, current.length)
        }
    }

    companion object {
        private val nonDigits = Regex("[^\\d]")
    }
}