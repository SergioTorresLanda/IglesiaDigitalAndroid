package mx.arquidiocesis.sos.ui

import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.upax.eamxsos.R
import com.upax.eamxsos.databinding.DialogItemBinding
import mx.arquidiocesis.eamxcommonutils.base.DatePickerFragment
import mx.arquidiocesis.eamxcommonutils.util.EAMXEditText.validaMin
import mx.arquidiocesis.sos.model.DialogModel
import mx.arquidiocesis.sos.model.ServiceBoxModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SOSDialogFragment private constructor(
    private var title: String?,
    private var subTitleUno: String?,
    private var subTitleDos: String?,
    private var textButtonOk: String?,
    private var options: List<ServiceBoxModel>?,
    private var textButtonCancel: String?,
    private var tipo: Int?,
    private var isCancelCustom: Boolean?,
    private val listener: (DialogModel) -> Unit = {}
) : DialogFragment() {

    class Builder() {
        private var title: String? = null
        private var subTitleUno: String? = null
        private var subTitleDos: String? = null

        private var textButtonOk: String? = null
        private var options: List<ServiceBoxModel>? = null
        private var textButtonCancel: String? = null
        private var isCancel: Boolean? = null
        private var type: Int? = null
        private var listener: (DialogModel) -> Unit = {}


        fun setOptions(options: List<ServiceBoxModel>): Builder {
            this.options = options
            return this
        }

        fun setTipo(tipo: Int): Builder {
            this.type = tipo
            return this
        }

        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setSubTitleUno(title: String): Builder {
            this.subTitleUno = title
            return this
        }

        fun setSubTitleDos(title: String): Builder {
            this.subTitleDos = title
            return this
        }

        fun setIsCancel(isCancel: Boolean): Builder {
            this.isCancel = isCancel
            return this
        }

        fun setTextButtonOk(text: String): Builder {
            this.textButtonOk = text
            return this
        }

        fun setTextButtonCancel(text: String): Builder {
            this.textButtonCancel = text
            return this
        }

        fun setListener(listener: (DialogModel) -> Unit): Builder {
            this.listener = listener
            return this
        }


        fun build(): DialogFragment {
            return SOSDialogFragment(
                title,
                subTitleUno,
                subTitleDos,
                textButtonOk,
                options,
                textButtonCancel,
                type,
                isCancel,
                listener
            )
        }
    }

    private lateinit var binding: DialogItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        binding = DialogItemBinding.inflate(inflater, container, false)

        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildAlertSimple(binding)

    }

    private fun mensaje() {
        Toast.makeText(requireContext(), "Agrega todas las opciones", Toast.LENGTH_SHORT).show()

    }


    private fun buildAlertSimple(binding: DialogItemBinding) {

        binding.apply {

            llAlertSimple.visibility = View.VISIBLE

            when (tipo) {
                1 -> {
                    llUno.visibility = View.GONE
                    btnAccept.setOnClickListener {
                        if (validaMin(etDos, 0)) {
                            listener(DialogModel("", etDos.text.toString(), 0))
                            dismiss()
                        } else {
                            mensaje()
                        }
                    }

                }
                2 -> {

                    btnAccept.setOnClickListener {
                        if (validaMin(etUno, 0) && validaMin(etDos, 0)) {
                            listener(DialogModel(etUno.text.toString(), etDos.text.toString(), 0))
                            dismiss()
                        } else {
                            mensaje()

                        }
                    }
                }
                3 -> {
                    llUno.visibility = View.GONE
                    rbCalificacion.visibility = View.VISIBLE
                    btnAccept.setOnClickListener {
                        if (validaMin(etDos, 0)) {
                            listener(
                                DialogModel(
                                    "",
                                    etDos.text.toString(),
                                    rbCalificacion.rating.toInt()
                                )
                            )
                            dismiss()
                        } else {
                            mensaje()
                        }
                    }

                }
                4 -> {
                    llUno.visibility = View.GONE
                    llTres.visibility = View.VISIBLE
                    etDos.isFocusable = false
                    ivLogo.visibility = View.VISIBLE
                    etDos.setOnClickListener {
                        val datePicker =
                            DatePickerFragment (isMin = true){ day, month, year ->
                                val cal = Calendar.getInstance()
                                val timeSetListener =
                                    TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                                        cal.set(Calendar.HOUR_OF_DAY, hour)
                                        cal.set(Calendar.MINUTE, minute)
                                        val fechaDia =
                                            "${year}-${month+1}-${day} " + SimpleDateFormat("HH:mm").format(
                                                cal.time
                                            )


                                        etDos.setText(fechaDia)
                                    }
                                TimePickerDialog(
                                    requireContext(),
                                    timeSetListener,
                                    cal.get(Calendar.HOUR_OF_DAY),
                                    cal.get(Calendar.MINUTE),
                                    true
                                ).show()

                            }

                        datePicker.show(childFragmentManager, "datePicker")

                    }
                    val list: ArrayList<String> = ArrayList()
                    list.add("Selecciona un padre")
                    for (codigo in options!!) {
                        list.add(codigo.name)
                    }

                    val adapterSpinner = ArrayAdapter(
                        requireContext(),
                        R.layout.custom_spinner_sos, list
                    )

                    spTres.adapter = adapterSpinner
                    btnAccept.setOnClickListener {
                        if (etDos.text.isNotEmpty() && spTres.selectedItemPosition > 0) {

                            listener(
                                DialogModel(
                                    "",
                                    etDos.text.toString(),
                                    options!!.get(spTres.selectedItemPosition - 1).id
                                )
                            )
                            dismiss()
                        } else {
                            mensaje()

                        }
                    }
                }

            }

            if (title == null) {
                tvTitle.visibility = View.GONE
            } else {
                tvTitle.text = title
            }
            if (subTitleUno != null) {

                tvUno.text = subTitleUno
            }
            if (subTitleDos != null) {

                tvDos.text = subTitleDos
            }
            textButtonOk?.let {
                btnAccept.text = it
            }

            if (textButtonCancel != null) {
                btnCancel.text = textButtonCancel
                btnCancel.visibility = View.VISIBLE
            } else {
                btnCancel.visibility = View.GONE
            }

            isCancelCustom?.let {
                isCancelable = it
            }

            ivClose.setOnClickListener {
                dismiss()
            }

            btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }
}