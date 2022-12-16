package mx.arquidiocesis.misiglesias.utils

import android.app.AlertDialog
import android.content.Context
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.misiglesias.model.Day
import mx.arquidiocesis.misiglesias.model.HoraryModelItem


class PublicFunctions {

    fun selectFirstHour(context: Context, horarios: MutableLiveData<String>) {

        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .build()

        picker.show((context as AppCompatActivity).supportFragmentManager, "tag");


        picker.addOnPositiveButtonClickListener {

            horarios.value = "${input(picker.hour)}:${input(picker.minute)}"


        }
        picker.addOnDismissListener {

        }

    }

    fun maxminEdit(value: EditText, maxLength: Int) {
        val fArray = arrayOfNulls<InputFilter>(1)
        fArray[0] = LengthFilter(maxLength)
        value.filters = fArray
    }

    fun validarVacios(editText: EditText): Boolean {
        return if (editText.text.isNullOrEmpty()) {
            editText.error = "Campo nesesario"
            true
        } else {
            false
        }
    }

    fun validarVacios(editText: SearchView): Boolean {
        return editText.query.isNullOrEmpty()
    }

    fun getHoraryList(): List<HoraryModelItem> {
        val listDay = getDays()
        val horaryModelItem = HoraryModelItem(listDay, "00:00", "00:00")
        return listOf(horaryModelItem)
    }


    fun getDays(): MutableList<Day> {
        return mutableListOf(
            Day(false, 0, "Domingo"),
            Day(false, 1, "Lunes"),
            Day(false, 2, "Martes"),
            Day(false, 3, "Miércoles"),
            Day(false, 4, "Jueves"),
            Day(false, 5, "Viernes"),
            Day(false, 6, "Sábado")
        )

    }

    fun getUserId(): Int {
        var userId = eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID.name,
            EAMXTypeObject.INT_OBJECT
        ) as Int
        return userId!!
    }

    fun redondearStar(num: Float): Float {
        var array = num.toString().split(".")
        var entero = array[0].toFloat()
        var decimal = array[1].toInt()
        if (decimal < 25) {
            return entero
        } else if (decimal < 75) {
            return entero + 0.5f
        } else {
            return entero + 1.0f
        }

    }

    fun obtenerDias(dias: List<Day>): String {
        var diastring = ""
        var primerString = ""
        var ultimoString = ""
        var idSeguido = 0
        var primero = true
        var seguido = false
        var domingo = false
        var segundo = false
        dias.forEach {
            if (it.checked) {
                if (primero) {
                    primero = false
                    idSeguido = it.id
                    if (idSeguido == 1) {
                        domingo = true
                        segundo = true
                    } else {
                        primerString = it.name
                        diastring = it.name
                    }
                } else {
                    if (segundo) {
                        idSeguido = it.id
                        primerString = it.name
                        segundo = false
                        diastring = it.name

                    } else {
                        diastring = "$diastring - ${it.name}"
                        if (it.id == idSeguido + 1) {
                            seguido = true
                            idSeguido = it.id
                            ultimoString = it.name
                        } else {
                            seguido = false
                        }
                    }


                }

            }
        }
        if (domingo) {
            if (seguido && ultimoString == "Sábado") {
                ultimoString = "Domingo"
            } else {
                diastring = "$diastring - Domingo"
            }
        }


        return if (seguido) {
            "$primerString a $ultimoString"
        } else {
            diastring
        }
    }

    fun selectDayRange(context: Context, tvDia: TextView, days: MutableList<String>, dias: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Selecciona los dias")


        var selecion: Int = -1
        builder.setSingleChoiceItems(days.toTypedArray(), selecion) { dialog, which ->
            selecion = which
        }

        builder.setPositiveButton("Aceptar") { dialog, which ->

            val dia = "${dias} ${days[selecion]}"
            days.remove(days[selecion])
            if (days.size != 0) {
                nuevoDia(context, tvDia, dia, days)
            } else {
                tvDia.text = dias
            }

            //selectFirstHour(context, tvDia, "${tvDia.text} ${days[selecion]}")
        }
        builder.setNegativeButton("Cancelar") { dialog, which ->
            tvDia.text = dias
        }

        val dialog = builder.create()
        dialog.show()
    }

    fun selectDayRange(context: Context, days: MutableLiveData<MutableList<Day>>) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Selecciona los dias")
        var dias = mutableListOf<String>()
        var checkLits = mutableListOf<Boolean>()
        days.value!!.forEach {
            checkLits.add(it.checked)
            dias.add(it.name)
        }

        builder.setMultiChoiceItems(
            dias.toTypedArray(),
            checkLits.toBooleanArray()
        ) { dialog, which, isChecked ->
            checkLits.set(which, isChecked)
        }

        builder.setPositiveButton("Aceptar") { dialog, which ->
            var i = 0
            var dias = mutableListOf<Day>()
            checkLits.forEach {
                when (i++) {
                    0 -> {
                        dias.add(Day(it, i, "Domingo"))
                    }
                    1 -> {
                        dias.add(Day(it, i, "Lunes"))
                    }
                    2 -> {
                        dias.add(Day(it, i, "Martes"))
                    }
                    3 -> {
                        dias.add(Day(it, i, "Miércoles"))
                    }
                    4 -> {
                        dias.add(Day(it, i, "Jueves"))
                    }
                    5 -> {
                        dias.add(Day(it, i, "Viernes"))
                    }
                    6 -> {
                        dias.add(Day(it, i, "Sábado"))
                    }

                }
            }
            days.value = dias
            //selectFirstHour(context, tvDia, "${tvDia.text} ${days[selecion]}")
        }
        builder.setNegativeButton("Cancelar") { dialog, which ->
        }

        val dialog = builder.create()
        dialog.show()
    }

    fun nuevoDia(context: Context, tv: TextView, dias: String, days: MutableList<String>) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Desea agregar otro dia")

        builder.setPositiveButton("Aceptar") { dialog, which ->

            selectDayRange(context, tv, days, dias)
        }
        builder.setNegativeButton("Cancelar") { dialog, which ->
            tv.text = dias

        }
        val dialog = builder.create()
        dialog.show()
    }


    fun input(input: Int): String {
        return if (input >= 10) {
            input.toString()
        } else {
            "0$input"
        }
    }
}
