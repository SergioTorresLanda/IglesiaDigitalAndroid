package mx.arquidiocesis.eamxcommonutils.util

import android.annotation.SuppressLint
import android.content.Context
import mx.arquidiocesis.eamxcommonutils.R
import java.text.SimpleDateFormat
import java.util.*

open class EAMXFormatDate(val context: Context) {

    fun main() {
        println(diference(Date().time))
        println(diference((Date().time) - 86400000))
        println(diference((Date().time) - (86400000 * 2)))
        println(diference((Date().time) - (86400000 * 3)))
        println(diference((Date().time) - (86400000 * 4)))
    }

    fun diference(fecha: Long): String {
        val now = Date()
        val date = Date(fecha)
        //Calculando diferencia y descartando milisegundos
        val segundos = (now.time - date.time) / 1000
        val minutos = segundos / 60
        val horas = minutos / 60
        val dias = horas / 24

        return if (dias > 1) {
            if (dias == 2L) {
//                "${day(date)} ${time(date)}"
                "${day(date)}"
            } else {
//                "${dayMonth(date)} ${time(date)}"
                "${dayMonth(date)}"
            }
        } else if (dias == 1L) {
            context.getString(R.string.yesterday, "${time(date)}")
        } else {
            isToday(date, now)
        }
    }

    fun diferencia(fecha: Long): String {
        val now = Date()
        val date = Date(fecha)

        val sdf = SimpleDateFormat("D")
        val today = sdf.format(now).toInt()
        val yesterday = sdf.format(date).toInt()

        return when (today - yesterday) {
            0 -> {
                context.getString(R.string.today, time(date))
            }
            1 -> {
                context.getString(R.string.yesterday, time(date))
            }
            2 -> {
                day(date)
            }
            else -> dayMonth(date)
        }.capitalize()
    }

    fun day(date: Date): String {
        val sdf = SimpleDateFormat("EEEE", Locale.forLanguageTag("es-MX"))
        return sdf.format(date)
    }

    fun time(date: Date): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.forLanguageTag("es-MX"))
        return sdf.format(date)
    }

    fun dayMonth(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy", Locale.forLanguageTag("es-MX"))
        val year_diff = dateFormat.format(Date()).toInt() - dateFormat.format(date).toInt()
        val sdf = SimpleDateFormat(
            if (year_diff == 0) "dd MMMM" else "dd MMMM yyyy",
            Locale.forLanguageTag("es-MX")
        )
        return sdf.format(date)
    }

    fun isToday(date: Date, date2: Date): String {
        val sdf = SimpleDateFormat("u", Locale.forLanguageTag("es-MX"))
        val today = sdf.format(date)
        val yesterday = sdf.format(date2)

        return context.getString(
            if (today == yesterday) R.string.today else R.string.yesterday,
            "${time(date)}"
        )
    }
}

fun dateFormatString(
    format: String = "yyyy-MM-dd",
    calendar: Calendar = Calendar.getInstance(),
): String =
    SimpleDateFormat(format, Locale("es", "MX")).format(calendar.time)

fun String.toCalendar(pattern: String = "yyyy-MM-dd"): Calendar = Calendar.getInstance().apply {
    time = SimpleDateFormat(pattern, Locale("es", "MX")).parse(this@toCalendar)
}