package mx.arquidiocesis.eamxcommonutils.base

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Message
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import mx.arquidiocesis.eamxcommonutils.R
import java.util.*

class DatePickerFragment(
    val isMax: Boolean = false,
    val isMin: Boolean = false,
    val maxDate: Date? = null,
    val listener: (day: Int, month: Int, year: Int) -> Unit
) :
    DialogFragment(), DatePickerDialog.OnDateSetListener {
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        listener(day, month, year)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = activity as Context
        val locale = Locale("es", "MX")
        Locale.setDefault(locale)
        val configuration: Configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        context.createConfigurationContext(configuration)
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dialog = DatePickerDialog(context, R.style.MyCalendarDatePickerDialog,this, year, month, day)
        dialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Aceptar", dialog)
        dialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancelar", dialog)
        if (isMax) {
            dialog.datePicker.maxDate = Date().time
        }
        if (isMin) {
            dialog.datePicker.minDate = Date().time
        }

        if (maxDate != null) {
            dialog.datePicker.maxDate = maxDate.time
        }
        return dialog
    }
}