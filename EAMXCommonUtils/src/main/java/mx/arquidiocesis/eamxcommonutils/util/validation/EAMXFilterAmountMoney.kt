package mx.arquidiocesis.eamxcommonutils.util.validation

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Matcher
import java.util.regex.Pattern

class EAMXFilterAmountMoney(val digitsBeforePoint: Int, val digitsAfterPoint: Int) : InputFilter{

    val pattern = Pattern.compile("[0-9]{0," + (digitsBeforePoint - 1) + "}+((\\.[0-9]{0," + (digitsAfterPoint - 1) + "})?)||(\\.)?");

    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        val matcher: Matcher = pattern.matcher(dest)
        return if (!matcher.matches()) "" else null
    }
}