package com.wallia.eamxcomunidades.utils

import android.app.AlertDialog
import android.content.Context
import android.text.InputFilter
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.wallia.eamxcomunidades.model.church.Day
import com.wallia.eamxcomunidades.model.church.HoraryModelItem
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences

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

            horarios.value = " ${picker.hour}:${input(picker.minute)}"

        }
        picker.addOnDismissListener {

        }

    }

    fun maxminEdit(value: EditText,maxLength:Int) {
        val fArray = arrayOfNulls<InputFilter>(1)
        fArray[0] = InputFilter.LengthFilter(maxLength)
        value.filters=fArray
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

    fun getDays(daysSelected : List<Int?>? = null, dayInListExcluyed: Boolean = false): MutableList<Day> {
        return daysSelected?.let{
            val list = getAllDays()
            if(!dayInListExcluyed) {
                list.filter { item -> it.find { itemSelected -> item.id == itemSelected } != null }
                    .forEach { it.checked = true }
            }else{
                val listToDelete = mutableListOf<Day>()
                it.forEach { item ->
                    list.forEachIndexed { index, day ->
                        if(day.id == item){
                            listToDelete.add(day)
                        }
                    }
                }
                listToDelete.forEach { indexToDelete ->
                    list.remove(indexToDelete)
                }
            }
            list
        } ?: getAllDays()
    }

    fun getAllDays() : MutableList<Day>{
        return  mutableListOf(
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
        val userId = (eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID.name,
            EAMXTypeObject.INT_OBJECT
        ) as Int)
        return userId
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
        val days = StringBuffer()
        val checkedDays = dias.filter { item -> item.checked }
        checkedDays.let { list ->
            if(list.isNotEmpty()){
                val isContinue = list.filterIndexed{index, day ->
                    if(index < list.size -1 ) {
                        val itemNext = list[index + 1]
                        val result = itemNext.id - day.id
                        result == 1
                    }else{
                        true
                    }
                }.size == list.size
                return if(isContinue){
                    val firstDay = list.first()
                    val lastDay = list.last()
                    "${firstDay.name} a ${lastDay.name}"
                }else{
                    list.forEach {
                        if(days.isEmpty()){
                            days.append(it.name)
                        }else{
                            days.append(", ${it.name}")
                        }
                    }
                    days.toString()
                }
            }
        }
        return ""
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

    fun getUserName(): String {
        val userName = eamxcu_preferences.getData(
            EAMXEnumUser.USER_NAME.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String

        val userMidleName = eamxcu_preferences.getData(
            EAMXEnumUser.USER_MIDDLE_NAME.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String

        val userLAstName = eamxcu_preferences.getData(
            EAMXEnumUser.USER_LAST_NAME.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String


        return "$userName $userLAstName $userMidleName"
    }

    fun selectDayRange(context: Context, days: MutableLiveData<MutableList<Day>>, useIndex : Boolean = true) {
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
            val dias = mutableListOf<Day>()
            if(useIndex) {
                checkLits.forEachIndexed { index, value ->
                    when (index) {
                        0 -> {
                            dias.add(Day(value, index, "Domingo"))
                        }
                        1 -> {
                            dias.add(Day(value, index, "Lunes"))
                        }
                        2 -> {
                            dias.add(Day(value, index, "Martes"))
                        }
                        3 -> {
                            dias.add(Day(value, index, "Miércoles"))
                        }
                        4 -> {
                            dias.add(Day(value, index, "Jueves"))
                        }
                        5 -> {
                            dias.add(Day(value, index, "Viernes"))
                        }
                        6 -> {
                            dias.add(Day(value, index, "Sábado"))
                        }
                    }
                }
            }else{
                checkLits.forEachIndexed{index, value ->
                    if(value){
                        val daySelected = days.value!![index]
                        daySelected.checked = true
                        dias.add(daySelected)
                    }
                }
            }
            days.value = dias
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