package mx.arquidiocesis.eamxcommonutils.util.validation

import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils

/**
 * @author
 * @return InputFilter
 * @see Método que solo permite ingresar letras y espacios
 */
fun EAMXfilterJustLetters(): InputFilter {
    val filter = InputFilter { source, start, end, dest, dstart, dend ->
        var keepOriginal = true
        val sb = java.lang.StringBuilder(end - start)
        for (i in start until end) {
            val c: Char = source[i]
            if (c.toInt() in 65..90 || c.toInt() in 97..122 || c.toInt() == 130 || c.toInt() in 160..163 || c.toInt() == 224 || c.toInt() == 233 || c.toInt() == 32) // put your condition here
                sb.append(c) else keepOriginal = false
        }
        if (!keepOriginal){
            if (source is Spanned){
                val sp = SpannableString(sb)
                TextUtils.copySpansFrom(source as Spanned, start, sb.length, null, sp, 0)
                return@InputFilter sp
            }else{
                return@InputFilter sb
            }
        }
        null
    }
    return filter
}

/**
 * @author
 * @return InputFilter
 * @see Método que no permite caracteres especiales
 */
fun EAMXfilterNotCharacterSpecial(): InputFilter {
    val filter = InputFilter { source, start, end, dest, dstart, dend ->
        var keepOriginal = true
        val sb = java.lang.StringBuilder(end - start)
        for (i in start until end) {
            val c: Char = source[i]
            if (isCharAllowed(c)) // put your condition here
                sb.append(c) else keepOriginal = false
        }
        if (!keepOriginal){
            if (source is Spanned){
                val sp = SpannableString(sb)
                TextUtils.copySpansFrom(source as Spanned, start, sb.length, null, sp, 0)
                return@InputFilter sp
            }else{
                return@InputFilter sb
            }
        }
        null
    }
    return filter
}

/**
 * @author
 * @return InputFilter
 * @see Método solo permite los sig caracteres especiales (@, -, _, ., alfanumericos)
 */
fun EAMXfilterNotSpecialCharacterEmail(): InputFilter {
    val filter = InputFilter { source, start, end, dest, dstart, dend ->
        var keepOriginal = true
        val sb = java.lang.StringBuilder(end - start)
        for (i in start until end) {
            val c: Char = source[i]

            if (isCharAllowed(c) || c.toInt() == 64 || c.toInt() in 45..46 || c.toInt() == 95) // put your condition here
                sb.append(c)
            else
                keepOriginal = false
        }
        if (!keepOriginal){
            if (source is Spanned){
                val sp = SpannableString(sb)
                TextUtils.copySpansFrom(source as Spanned, start, sb.length, null, sp, 0)
                return@InputFilter sp
            }else{
                return@InputFilter sb
            }
        }
        null
    }
    return filter
}
private fun isCharAllowed(c: Char): Boolean {
    return Character.isLetterOrDigit(c) || Character.isSpaceChar(c)
}

/**
 * @author
 * @return InputFilter
 * @see Método que no permite emojis pero si caracteres especiales
 */
fun EAMXfilterNotEmojis(): InputFilter {
    val filter = InputFilter { source, start, end, dest, dstart, dend ->
        var keepOriginal = true
        val sb = java.lang.StringBuilder(end - start)
        for (i in start until end) {
            val c: Char = source[i]

            if (isCharAllowed(c) || c.toInt() in 33..47 || c.toInt() in 58..64 || c.toInt() in 91..96 || c.toInt() in 123..175 || c.toInt() == 184 || c.toInt() in 189..190 || c.toInt() in 224..250 ) // put your condition here
                sb.append(c)
            else
                keepOriginal = false
        }
        if (!keepOriginal){
            if (source is Spanned){
                val sp = SpannableString(sb)
                TextUtils.copySpansFrom(source as Spanned, start, sb.length, null, sp, 0)
                return@InputFilter sp
            }else{
                return@InputFilter sb
            }
        }
        null
    }
    return filter
}