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
    const val KEY_NAME_GUEST = "NAME_GUEST"
    const val KEY_RESPONSABILITY = "KEY_RESPONSABILITY"
    const val KEY_PHONE = "KEY_PHONE"
    const val KEY_PHONE_GUEST = "KEY_PHONE_GUEST"
    const val KEY_HOUR_FIRST = "KEY_HOUR_FIRST"
    const val KEY_HOUR_END = "KEY_HOUR_END"
    const val KEY_EMAIL = "EMAIL"
    const val STATUS_OK = "ok"
    const val KEY_REQUISIT = "KEY_REQUISIT"
    const val KEY_ADDRESS = "KEY_ADDRESS"
    const val KEY_ADDRESSOTHER = "KEY_ADDRESSOTHER"
    const val KEY_NAMEOTHER = "KEY_NAMEOTHER"
    const val KEY_DESCOTHER = "KEY_DESCOTHER"
    const val KEY_TYPEOTHER = "KEY_TYPEOTHER"
    const val KEY_DAYSOTHER = "KEY_DAYSOTHER"
    const val KEY_DAYS = "KEY_DAYS"
    const val KEY_ZONE = "KEY_ZONE"
    const val KEY_DONANTE = "KEY_DONANTE"
    const val KEY_VOLUNTIES = "KEY_VOLUNTIES"
    const val KEY_MOUNT = "KEY_MOUNT"
    const val KEY_LATITUDE = "KEY_LATITUDE"
    const val KEY_LONGITUDE = "KEY_LONGITUDE"
    const val KEY_STATUS = "KEY_STATUS"
    const val KEY_USER_ID = "KEY_USER_ID"

    //DESPENSAS
    const val KEY_RECEIVED = "KEY_RECEIVED"
    const val KEY_DATE_FIRST_RECEIVED = "KEY_DATE_FIRST_RECEIVED"
    const val KEY_DATE_END_RECEIVED = "KEY_DATE_END_RECEIVED"
    const val KEY_HOUR_FIRST_RECEIVED = "KEY_HOUR_FIRST_RECEIVED"
    const val KEY_HOUR_END_RECEIVED = "KEY_HOUR_END_RECEIVED"
    const val KEY_DAYS_RECEIVED = "KEY_DAYS_RECEIVED"

    const val KEY_ARMED = "KEY_ARMED"
    const val KEY_DATE_FIRST_ARMED = "KEY_DATE_FIRST_ARMED"
    const val KEY_DATE_END_ARMED = "KEY_DATE_END_ARMED"
    const val KEY_HOUR_FIRST_ARMED = "KEY_HOUR_FIRST_ARMED"
    const val KEY_HOUR_END_ARMED = "KEY_HOUR_END_ARMED"
    const val KEY_DAYS_ARMED = "KEY_DAYS_ARMED"

    const val KEY_DELIVERY = "KEY_DELIVERY"
    const val KEY_DATE_FIRST_DELIVERY = "KEY_DATE_FIRST_DELIVERY"
    const val KEY_DATE_END_DELIVERY = "KEY_DATE_END_DELIVERY"
    const val KEY_HOUR_FIRST_DELIVERY = "KEY_HOUR_FIRST_DELIVERY"
    const val KEY_HOUR_END_DELIVERY = "KEY_HOUR_END_DELIVERY"
    const val KEY_DAYS_DELIVERY = "KEY_DAYS_DELIVERY"
    const val KEY_ADDRESS_DELIVERY = "KEY_ADDRESS_DELIVERY"
    const val KEY_LATITUDE_DELIVERY = "KEY_LATITUDE_DELIVERY"
    const val KEY_LONGITUDE_DELIVERY = "KEY_LONGITUDE_DELIVERY"

    //DONANTES
    const val KEY_COMMENT = "KEY_ZONE"
    const val KEY_BANK = "KEY_DONANTE"
    const val KEY_TYPEDON = "KEY_VOLUNTIES"

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