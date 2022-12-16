package mx.arquidiocesis.registrosacerdote.constants

import java.util.regex.Pattern

object Constants {
    const val LOADER = "LOADER"
    const val EMPTY_FIELD = "EMPTY_FIELD"
    const val INVALID_EMAIL = "INVALID_EMAIL"
    const val DATE_MACK = "dd/mm/aaaa"
    const val KEY_NAME = "NAME"
    const val KEY_FIRST_SURNAME = "FIRST_SURNAME"
    const val KEY_SECOND_SURNAME = "SECOND_SURNAME"
    const val KEY_BIRTHDATE = "BIRTHDATE"
    const val KEY_ORDINATION = "ORDINATION"
    const val KEY_EMAIL = "EMAIL"
    const val STATUS_OK = "ok"
    const val KEY_CONGREGATION = "CONGREGATION"
    const val KEY_ACTIVITY = "KEY_ACTIVITY"

    val EMAIL_ADDRESS: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    const val OTHER_ACTIVITIES = 7
}