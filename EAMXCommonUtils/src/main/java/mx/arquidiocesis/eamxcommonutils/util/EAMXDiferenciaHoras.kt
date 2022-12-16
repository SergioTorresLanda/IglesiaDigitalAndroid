package mx.arquidiocesis.eamxcommonutils.util

import java.text.SimpleDateFormat
import java.util.*


class EAMXDiferenciaHoras {
    fun main(date: String): String {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val diference = (Date().time-df.parse(date).time)
        val segundos: Long = diference / 1000
        val minutos = segundos / 60
        val horas = minutos / 60
        val dias = horas / 24
        val semanas = dias / 7
        val meses = dias / 30
        val años = dias / 365
        return when {
            años>1 -> {
                "hace ${años} años"
            }
            años>0 -> {
                "hace un año"
            }
            meses>1 -> {
                "hace ${meses} meses"
            }
            meses>0 -> {
                "hace un mes"
            }
            semanas>1 -> {
                "hace ${semanas} semanas"
            }
            semanas>0 -> {
                "hace una semana"
            }
            dias>1 -> {
                "hace ${dias} dias"
            }
            dias>0 -> {
                "hace un dia"
            }
            else -> {
                "hoy"
            }
        }
    }
}