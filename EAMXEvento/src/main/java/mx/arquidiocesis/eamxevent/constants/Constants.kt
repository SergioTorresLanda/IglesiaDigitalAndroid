package mx.arquidiocesis.eamxevent.constants

import mx.arquidiocesis.eamxevent.model.enum.Zone
import java.util.regex.Pattern

object Constants {

    const val LOADER = "LOADER"
    const val EMPTY_FIELD = "EMPTY_FIELD"
    const val INVALID_EMAIL = "INVALID_EMAIL"
    const val INVALID_PHONE= "INVALID_PHONE"
    const val HOUR_MACK = "00:00"
    const val KEY_NAME = "NAME"
    const val KEY_RESPONSABILITY = "KEY_RESPONSABILITY"
    const val KEY_PHONE = "KEY_PHONE"
    const val KEY_HOUR_FIRST = "KEY_HOUR_FIRST"
    const val KEY_HOUR_END = "KEY_HOUR_END"
    const val KEY_EMAIL = "EMAIL"
    const val STATUS_OK = "ok"
    const val KEY_REQUISIT = "KEY_REQUISIT"
    const val KEY_ADDRESS = "KEY_ADDRESS"
    const val KEY_DAYS = "KEY_DAYS"
    const val KEY_ZONE = "KEY_ZONE"
    const val KEY_DONANTE = "KEY_DONANTE"
    const val KEY_VOLUNTIES = "KEY_VOLUNTIES"
    const val KEY_MOUNT = "KEY_MOUNT"
    const val KEY_LATITUDE = "KEY_LATITUDE"
    const val KEY_LONGITUDE = "KEY_LONGITUDE"
    const val KEY_STATUS = "KEY_STATUS"
    const val KEY_USER_ID = "KEY_USER_ID"

    val EMAIL_ADDRESS: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    const val position = 0
}