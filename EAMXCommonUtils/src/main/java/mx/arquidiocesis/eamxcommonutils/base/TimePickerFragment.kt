package mx.arquidiocesis.eamxcommonutils.base

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import mx.arquidiocesis.eamxcommonutils.R
import java.util.*

class TimePickerFragment(
    val isMax: Boolean = false,
    val isMin: Boolean = false,
    val maxDate: Date? = null,
    val listener: (hourOfDay: Int, minute: Int) -> Unit
) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        listener(hourOfDay, minute)
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
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val dialog = TimePickerDialog(context, R.style.MyCalendarDatePickerDialog,this, hour, minute, true)
        dialog.setButton(TimePickerDialog.BUTTON_POSITIVE, "Aceptar", dialog)
        dialog.setButton(TimePickerDialog.BUTTON_NEGATIVE, "Cancelar", dialog)

        return dialog
    }


}