package mx.arquidiocesis.eamxcommonutils.util.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
//import android.view.LayoutInflater
import android.view.View
import android.widget.*
import mx.arquidiocesis.eamxcommonutils.R
//import com.example.eamxdonaciones.R
//import kotlinx.android.synthetic.main.custom_monto_spinner.view.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class CustomMontoSpinner : LinearLayout {

    lateinit var spMonto: Spinner
    lateinit var etMonto: EditText
    lateinit var llMonto: LinearLayout

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.custom_monto_spinner, this)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        View.inflate(context, R.layout.custom_monto_spinner, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        spMonto = findViewById(R.id.spMonto)
        etMonto = findViewById(R.id.etMonto)
        llMonto = findViewById(R.id.llMonto)
    }

    fun setAdapter(adapterSpinnerMonto: ArrayAdapter<String>) {
        spMonto.adapter = adapterSpinnerMonto
        spMonto.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long, ) {
                    if (position + 1 == adapterSpinnerMonto.count) {
                        llMonto.visibility = View.VISIBLE
                        etMonto.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(p0: Editable?) {
                                etMonto.removeTextChangedListener(this)

                                try {
                                    var givenstring: String = p0.toString()
                                    if (givenstring.contains(",")) {
                                        givenstring = givenstring.replace(",", "")
                                    }
                                    val doubleVal: Double = givenstring.toDouble()

                                    // https://docs.oracle.com/javase/tutorial/i18n/format/decimalFormat.html
                                    val unusualSymbols = DecimalFormatSymbols()
                                    unusualSymbols.decimalSeparator = '.'
                                    unusualSymbols.groupingSeparator = ','

                                    val formatter = DecimalFormat("#,##0.##", unusualSymbols)
                                    formatter.groupingSize = 3
                                    val formattedString = formatter.format(doubleVal)

                                    etMonto.setText(formattedString)
                                    etMonto.setSelection(etMonto.text.length)
                                    // to place the cursor at the end of text
                                } catch (nfe: NumberFormatException) {
                                    nfe.printStackTrace()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                                etMonto.addTextChangedListener(this)
                            }

                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                            }

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            }
                        })

                    } else {
                        llMonto.visibility = View.GONE
                    }
                }
            }
    }
}
