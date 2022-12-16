package mx.arquidiocesis.eamxgeneric.fragments.otherbanks

import android.app.DatePickerDialog
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXTypeValidation
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXValidationModel
import mx.arquidiocesis.eamxcommonutils.util.toast
import mx.arquidiocesis.eamxgeneric.R
import java.util.*
import kotlin.collections.ArrayList

fun EAMXOtherBanksFragment.initListener() {

    lateinit var cardHolder: String
    lateinit var cardNumber: String
    var selected = false
    lateinit var cardDate: String
    lateinit var cvv: String
    lateinit var quantity: String
    val arrayListValidations: ArrayList<EAMXValidationModel> = ArrayList()

    mBinding.apply {

//        callBack.showToolbar(true, "")
        callBack.showToolbar(true, AppMyConstants.ofrendaTarjeta)

        cardHolder = edtCardholder.text.toString()
        cardNumber = edtCardNumber.addTextChangedListener(CardNumberFormat(4, "-", 16)).toString()
        cardDate   = etExpireDate.addTextChangedListener(CardNumberFormat(2, "/", 4)).toString()
        cvv        = etCVV.text.toString()
        etExpireDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val textLength = p0.toString()

                if (textLength.length < 5)
                    etExpireDate.setTextColor(ContextCompat.getColor(root.context, R.color.red))
                else {
                    val month = textLength.substring(0, 2)
                    val year = textLength.substring(3)
                    val monthDate = month.toInt()
                    val yearDate = year.toInt()

                    if (monthDate in 1..12 && yearDate >= 21)
                        etExpireDate.setTextColor(ContextCompat.getColor(root.context, R.color.colorGray))
                }
            }
        })

        ivCalendarIcon.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(root.context, DatePickerDialog.OnDateSetListener { _, year, month, _ ->
                        val selectedMonth = month + 1
                        val date = if (selectedMonth < 10)
                            "0" + selectedMonth.toString() + year.toString().substring(2)
                        else
                            selectedMonth.toString() + year.toString().substring(2)
                        etExpireDate.setText(date).toString()
                    },
                    year,
                    month,
                    day
            )
            c.add(Calendar.YEAR, 0)
            dpd.datePicker.minDate = c.timeInMillis
            dpd.show()
        }

        svCreditOrDevit.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selected = true
                tvCredit.setTextColor(ContextCompat.getColor(root.context, R.color.primaryColorDark))
                tvDebit.setTextColor(ContextCompat.getColor(root.context, R.color.colorGray))
                toast(getString(R.string.credit))
            } else {
                selected = false
                tvCredit.setTextColor(ContextCompat.getColor(root.context, R.color.colorGray))
                tvDebit.setTextColor(ContextCompat.getColor(root.context, R.color.primaryColorDark))
                toast(getString(R.string.debit))
            }
        }

        btnConfirmDonate.setOnClickListener {
            arrayListValidations.add(EAMXValidationModel(edtCardholder, EAMXTypeValidation.NOT_EMPTY, true, "Ingrese el nombre del títular"))
            arrayListValidations.add(EAMXValidationModel(edtCardNumber, EAMXTypeValidation.NOT_EMPTY, true, "Ingrese el número de su tarjeta"))
            arrayListValidations.add(EAMXValidationModel(etExpireDate, EAMXTypeValidation.NOT_EMPTY, true, "Ingrese la fecha de su tarjeta"))
            arrayListValidations.add(EAMXValidationModel(etCVV, EAMXTypeValidation.NOT_EMPTY, true, "Ingrese el cvv de su tarjeta"))

            btnConfirmDonate.isEnabled = true
            btnConfirmDonate.setBackgroundColor(ContextCompat.getColor(root.context, R.color.primaryColor))

            toast("clic")
        }
    }
}

