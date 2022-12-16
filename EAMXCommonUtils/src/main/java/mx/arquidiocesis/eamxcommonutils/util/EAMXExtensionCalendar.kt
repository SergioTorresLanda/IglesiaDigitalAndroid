package mx.arquidiocesis.eamxcommonutils.util

import java.text.SimpleDateFormat
import java.util.*

fun Calendar.dateInFormatDayNameDDMonthName(day: Int, month: Int, year: Int) : String {
    val formatOut = "EEEE dd MMMM"
    val formatCalendar = "yyyy-MM-dd"
    val local = Locale("es", "MX")
    set(Calendar.DATE, day)
    set(Calendar.MONTH, month)
    set(Calendar.YEAR, year)
    val simpleFormat = SimpleDateFormat(formatCalendar, local)
    simpleFormat.applyPattern(formatOut)
    val dateArray = simpleFormat.format(time).split(" ").toMutableList()
    dateArray[0] = dateArray[0].replaceRange(0..0, dateArray[0].substring(0,1).toUpperCase(local))
    dateArray[2] = dateArray[2].replaceRange(0..0, dateArray[2].substring(0,1).toUpperCase(local))
    return "${dateArray[0]} ${dateArray[1]} ${dateArray[2]}"
}

fun Calendar.insertDateMiddleDashAndYyyMMddCurrent(day: Int, month: Int, year: Int) : String {
    set(Calendar.DATE, day)
    set(Calendar.MONTH, month)
    set(Calendar.YEAR, year)
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale("es","MX"))
    return formatter.format(this.time).toString()
}

fun Calendar.dateInFormatDayNameDDMonthNameCurrent() : String {
    val formatOut = "EEEE dd MMMM"
    val formatCalendar = "yyyy-MM-dd"
    val locate =  Locale("es", "MX")

    val simpleFormat = SimpleDateFormat(formatCalendar, locate)
    simpleFormat.applyPattern(formatOut)
    val dateArray = simpleFormat.format(time).split(" ").toMutableList()
    dateArray[0] = dateArray[0].replaceRange(0..0, dateArray[0].substring(0,1).toUpperCase(locate))
    dateArray[2] = dateArray[2].replaceRange(0..0, dateArray[2].substring(0,1).toUpperCase(locate))
    return "${dateArray[0]} ${dateArray[1]} ${dateArray[2]}"
}

fun Calendar.dateMiddleDashAndYyyMMddCurrent() : String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale("es","MX"))
    return formatter.format(this.time).toString()
}


