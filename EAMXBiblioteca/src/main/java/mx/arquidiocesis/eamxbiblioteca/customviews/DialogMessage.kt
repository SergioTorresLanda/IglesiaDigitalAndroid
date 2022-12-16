package mx.arquidiocesis.eamxbiblioteca.customviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxbiblioteca.R
import mx.arquidiocesis.eamxbiblioteca.databinding.FragmentBottomSheetDialogBinding

class DialogMessage : BottomSheetDialogFragment() {

    lateinit var binding: FragmentBottomSheetDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.apply {
            val order = getString("order")
            val age = getString("age")
            val topic = getBoolean("topic")

            binding.apply {
                when (order) {
                    "ASC" -> {
                        rbRecienteAntiguio.isChecked = true
                    }
                    "DESC" -> {
                        rbAntiguoReciente.isChecked = true
                    }
                    "TOP" -> {
                        rbMayorMenor.isChecked = true
                    }
                    "LOW" -> {
                        rbMenorMayor.isChecked = true
                    }
                }

                when (age) {
                    "CHILDREN" -> {
                        rbRangoUno.isChecked = true
                    }
                    "YOUTH" -> {
                        rbRangoDos.isChecked = true
                    }
                    "ADULTS" -> {
                        rbRangoTres.isChecked = true
                    }
                }

                rbTheme.isChecked = topic
            }
        }

        binding.apply {
            tvLimpiarFiltro.setOnClickListener {
                rbRecienteAntiguio.isChecked = false
                rbAntiguoReciente.isChecked = false
                rbMayorMenor.isChecked = false
                rbMenorMayor.isChecked = false
                rbRangoUno.isChecked = false
                rbRangoDos.isChecked = false
                rbRangoTres.isChecked = false
                rbTheme.isChecked = false
                limpiarFiltro.value = true
                dialog?.dismiss()
            }

            rbRecienteAntiguio.setOnClickListener {
                rbAntiguoReciente.isChecked = false
                rbMayorMenor.isChecked = false
                rbMenorMayor.isChecked = false
                recienteAntiguio.value = rbRecienteAntiguio.isChecked
                dialog?.dismiss()
            }

            rbAntiguoReciente.setOnClickListener {
                rbRecienteAntiguio.isChecked = false
                rbMayorMenor.isChecked = false
                rbMenorMayor.isChecked = false
                antiguioReciente.value = rbAntiguoReciente.isChecked
                dialog?.dismiss()
            }
            rbMayorMenor.setOnClickListener {
                rbRecienteAntiguio.isChecked = false
                rbAntiguoReciente.isChecked = false
                rbMenorMayor.isChecked = false
                mayorMenor.value = rbMayorMenor.isChecked
                dialog?.dismiss()
            }
            rbMenorMayor.setOnClickListener {
                rbRecienteAntiguio.isChecked = false
                rbAntiguoReciente.isChecked = false
                rbMayorMenor.isChecked = false
                menorMayor.value = rbMenorMayor.isChecked
                dialog?.dismiss()
            }

            rbRangoUno.setOnClickListener {
                rbRangoDos.isChecked = false
                rbRangoTres.isChecked = false
                rangoUno.value = rbRangoUno.isChecked
                dialog?.dismiss()
            }
            rbRangoDos.setOnClickListener {
                rbRangoUno.isChecked = false
                rbRangoTres.isChecked = false
                rangoDos.value = rbRangoDos.isChecked
                dialog?.dismiss()
            }
            rbRangoTres.setOnClickListener {
                rbRangoUno.isChecked = false
                rbRangoDos.isChecked = false
                rangoTres.value = rbRangoTres.isChecked
                dialog?.dismiss()
            }

            rbTheme.setOnClickListener {
                themeFilter.value = rbTheme.isChecked
                dialog?.dismiss()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {
        return BottomSheetDialog(requireContext(), R.style.SheetDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance(order: String, age: String, topic: Boolean): DialogMessage =
            DialogMessage().apply {
                arguments = Bundle().apply {
                    putString("order", order)
                    putString("age", age)
                    putBoolean("topic", topic)
                }
            }

        var limpiarFiltro = MutableLiveData<Boolean>()
        var recienteAntiguio = MutableLiveData<Boolean>()
        var antiguioReciente = MutableLiveData<Boolean>()
        var mayorMenor = MutableLiveData<Boolean>()
        var menorMayor = MutableLiveData<Boolean>()
        var rangoUno = MutableLiveData<Boolean>()
        var rangoDos = MutableLiveData<Boolean>()
        var rangoTres = MutableLiveData<Boolean>()
        var themeFilter = MutableLiveData<Boolean>()
    }
}