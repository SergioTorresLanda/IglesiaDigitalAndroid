package mx.arquidiocesis.eamxcommonutils.util.validation

import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import com.google.android.material.textfield.TextInputEditText

fun TextInputEditText.addFilterNotCharacterSpecial(exceptionType: Any? = null) {
    filters += filterNotCharacterSpecial(exceptionType)
}

fun filterNotCharacterSpecial(exceptionType: Any? = null): InputFilter {
    val filter = InputFilter { source, start, end, dest, dstart, dend ->

        var keepOriginal = true
        val sb = java.lang.StringBuilder(end - start)
        for (i in start until end) {
            val c: Char = source[i]

            if (exceptionType != null) {
                when (exceptionType) {
                    is EAMXFilterException -> {
                        when (exceptionType) {
                            EAMXFilterException.EMAIL -> {
                                if (isCharAllowed(c, exceptionType.exceptionsCharacters)) {
                                    sb.append(c)
                                } else {
                                    keepOriginal = false
                                }
                            }
                        }
                    }
                    is ArrayList<*> -> {
                        if (isCharAllowed(c, exceptionType as ArrayList<Char>)) {
                            sb.append(c)
                        } else {
                            keepOriginal = false
                        }
                    }
                    else -> {
                        throw IllegalArgumentException("El tipo de exception es invalido, tiene que ser tipo ArrayList<Char>")
                    }
                }
            } else {
                if (isCharAllowed(c)) {
                    sb.append(c)
                } else {
                    keepOriginal = false
                }
            }

        }

        if (!keepOriginal) {

            if (source is Spanned) {
                val sp = SpannableString(sb)
                TextUtils.copySpansFrom(source, start, sb.length, null, sp, 0)
                return@InputFilter sp
            } else {
                return@InputFilter sb
            }

        }

        null
    }
    return filter
}

private fun isCharAllowed(c: Char, charactersAccepted: ArrayList<Char>? = null): Boolean {
    return isCharacterAccepted(c, charactersAccepted)
            || Character.isLetterOrDigit(c) || Character.isSpaceChar(c)
}

private fun isCharAllowed(c: Char): Boolean {
    return Character.isLetterOrDigit(c) || Character.isSpaceChar(c)
}

private fun isCharacterAccepted(c: Char, charactersAccepted: ArrayList<Char>? = null): Boolean {
    var result = false
    charactersAccepted?.let {
        if (it.contains(c)) {
            result = true
        }
    }
    return result
}
