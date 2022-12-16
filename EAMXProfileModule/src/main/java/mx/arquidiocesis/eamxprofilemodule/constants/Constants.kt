package mx.arquidiocesis.eamxprofilemodule.constants

import java.util.regex.Pattern

object Constants {
    const val LOADER = "lOADER"
    const val EMPTY_FIELD = "EMPTY_FIELD"
    const val INVALID_EMAIL = "INVALID_EMAIL"
    const val DATE_MACK = "00/00/0000"
    const val KEY_NAME = "NAME"
    const val KEY_FIRST_SURNAME = "FIRST_SURNAME"
    const val KEY_SECOND_SURNAME = "SECOND_SURNAME"
    const val KEY_DESCRIPTON = "DESCRIPTION"
    const val KEY_BIRTHDATE = "BIRTHDATE"
    const val KEY_ORDINATION = "ORDINATION"
    const val KEY_EMAIL = "EMAIL"
    const val KEY_ACTIVITY = "ACTIVITY"
    const val KEY_CHANNEL_STREAM = "CHANNEL_STREAM"
    const val EMPTY_STRING = " "
    const val STATUS_OK = "ok"

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