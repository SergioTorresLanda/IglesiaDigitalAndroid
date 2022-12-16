package mx.arquidiocesis.eamxcommonutils.util

import java.text.SimpleDateFormat
import java.util.*


class EAMXObtenerDiasPrueba {
    fun main(){
        val result = diference((Date().time)-187000000 )
        println(result)
    }

    fun diference(fecha: Long) : String{
        val now = Date()
        val date = Date(fecha)
        val time = ""
        //Calculando diferencia y descartando milisegundos
        val segundos = ( now.time - date.time ) /1000
        val minutos = segundos / 60
        val horas = minutos / 60
        val dias = horas / 24

        if(dias > 1) {
            return if(dias == 2L) {
                dayTime(date)
            }else{
                "12 Abril"
            }
        }
        if (dias == 1L)
            return "Ayer $time"
        if(horas > 1)
            return "Hace $horas horas"
        if (horas == 1L)
            return "Hace 1 hora"
        if(minutos > 1)
            return "Hace $minutos minutos"
        if (minutos == 1L)
            return "Hace 1 minuto"
        return if(segundos > 1)
            "Hace $segundos segundos"
        else
            "Hace 1 segundo"
    }
    fun getDays(fechaConsulta: Long) : Int{
        val now = Date().time
        val date = Date(fechaConsulta).time
        //Diferencia en dias
        return ((now - date) /24 /60 /60 / 1000).toInt()
    }
    fun dayTime(date: Date): String {
        val sdf = SimpleDateFormat("EEEE h:m a", Locale.forLanguageTag("es-MX"))
        return sdf.format(date)
    }
}