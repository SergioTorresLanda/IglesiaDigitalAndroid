package mx.arquidiocesis.servicios.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_schedule_hour.view.*
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert.Companion.ACTION_ACCEPT
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert.Companion.ACTION_CLOSE
import mx.arquidiocesis.eamxcommonutils.util.*
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxdonaciones.congig.WebConfig
import mx.arquidiocesis.eamxdonaciones.model.ModelWebView
import mx.arquidiocesis.eamxdonaciones.utils.toNumber
import mx.arquidiocesis.eamxpagos.config.OriginType
import mx.arquidiocesis.eamxpagos.fragment.ComponentFragment
import mx.arquidiocesis.servicios.R
import mx.arquidiocesis.servicios.adapter.ScheduleAdapter
import mx.arquidiocesis.servicios.databinding.FragmentIntentionScheduleHourBinding
import mx.arquidiocesis.servicios.model.*
import mx.arquidiocesis.servicios.repository.Repository
import mx.arquidiocesis.servicios.viewModel.ServiciosViewModel
import java.net.URLEncoder

class IntentionScheduleHourFragment : FragmentBase() {
    private lateinit var day: String
    lateinit var binding: FragmentIntentionScheduleHourBinding
    private val gson by lazy {
        Gson()
    }
    private val viewModel: ServiciosViewModel by lazy {
        getViewModel {
            ServiciosViewModel(Repository(requireContext()))
        }
    }
    lateinit var scheduleAdapter: ScheduleAdapter
    var schedules: List<Schedule> = listOf()
    lateinit var date: String
    var locationId: Int = 0
    var schedule: Schedule? = null
    private var userId: Int = 0
    var dayInt: Int = 0
    var monthInt: Int = 0
    var yearInt: Int = 0
    var itemSelected = 0;
    lateinit var intentions: List<Intention>

    //Para quien es la intension
    val options: ArrayList<String> = ArrayList()
    private var amount: String = ""
    var mentionInformation: String = ""

    companion object {
        fun newInstance(
            locationId: Int,
            date: String,
            day: String,
            dayInt: Int,
            monthInt: Int,
            yearInt: Int
        ): IntentionScheduleHourFragment {
            val fragment = IntentionScheduleHourFragment()
            fragment.date = date
            fragment.locationId = locationId
            fragment.day = day
            fragment.dayInt = dayInt
            fragment.monthInt = monthInt
            fragment.yearInt = yearInt
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntentionScheduleHourBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        options.clear()
        options.add(0, "Descripción")
        userId = eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID.name,
            EAMXTypeObject.INT_OBJECT
        ) as Int
        scheduleAdapter = ScheduleAdapter {
            schedule = schedules[it]
        }
        initObservers()
        showLoader()
        binding.rvSchedules.adapter = scheduleAdapter
        viewModel.getMassesSchedule(locationId, "MASSES")
        viewModel.getIntentions()
        binding.tvDate.text = date
        binding.btnFinish.setOnClickListener {
            if (itemSelected != 0) {
                if (binding.etDescription.text.isNotEmpty() &&
                    EAMXEditText.validaMin(binding.etDescription, 0)
                ) {
                    binding.etDescription.error = null
                    schedule?.let {
                        if (validaMonto()) {
                            mentionInformation = binding.etDescription.text.toString() + "#" +
                                    "MENTION" + "#" +
                                    locationId + "#" +
                                    "${yearInt}-${monthInt + 1}-${dayInt}" + "#" +
                                    it.hour_start + "#" +
                                    intentions[itemSelected - 1].id + "#" +
                                    binding.etName.text.toString()
                            changeFragment(setLink())
                        }
                    } ?: run {
                        UtilAlert.Builder()
                            .setIsCancel(false)
                            .setTitle("Aviso")
                            .setMessage("Selecciona un horario para tu meción.")
                            .build()
                            .show(childFragmentManager, "")
                    }
                } else {
                    UtilAlert
                        .Builder()
                        .setTitle("Aviso")
                        .setMessage("Campo Dirigido a, es requerido.")
                        .build()
                        .show(childFragmentManager, "")
                    hideLoader()
                }
            } else {
                UtilAlert
                    .Builder()
                    .setTitle("Aviso")
                    .setMessage("Debes seleccionar una intención.")
                    .build()
                    .show(childFragmentManager, "")
                hideLoader()
            }
        }
        binding.spIntenciones.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                try {
                    (p0?.getChildAt(0) as TextView).gravity = Gravity.LEFT
                    itemSelected = p2
                } catch (e: Exception) {
                    UtilAlert.Builder()
                        .setTitle("Aviso")
                        .setMessage("Tu mención no se completó, es necesario realizar el pago.")
                        .build()
                        .show(childFragmentManager, "")
                    print("Se cancelo el deposito $e")
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                print("")
            }
        }
        setSpiner()
    }

    fun initObservers() {
        viewModel.errorResponseExit.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert.Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .setListener {
                    activity?.onBackPressed()
                }
                .build().show(childFragmentManager, "")
        }
        viewModel.massesScheduleResponse.observe(viewLifecycleOwner) {
            hideLoader()
            if (it == null) return@observe
            if (!it.masses.isNullOrEmpty()) {
                it.masses.forEach {
                    if (it.day == day)
                        schedules = it.schedules
                }
                scheduleAdapter.schedules = schedules
                if (schedules.size == 1) {
                    scheduleAdapter.selectedPosition = 0
                    schedule = schedules[0]
                }
                if (schedules.size == 0) {
                    UtilAlert.Builder()
                        .setTitle("Aviso")
                        .setMessage("No se cuenta con horarios disponibles.")
                        .setIsCancel(false)
                        .setTextButtonOk(getString(R.string.aceptar))
                        .setListener {
                            when (it) {
                                ACTION_ACCEPT -> {
                                    back()
                                }
                                ACTION_CLOSE -> back()
                            }
                        }
                        .build()
                        .show(childFragmentManager, "")
                }
            } else {
                UtilAlert.Builder()
                    .setTitle("Aviso")
                    .setMessage("No se cuenta con horarios disponibles.")
                    .setIsCancel(false)
                    .setTextButtonOk(getString(R.string.aceptar))
                    .setListener {
                        when (it) {
                            ACTION_ACCEPT -> {
                                back()
                            }
                            ACTION_CLOSE -> back()
                        }
                    }
                    .build()
                    .show(childFragmentManager, "")
            }
        }
        viewModel.errorResponse.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert.Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build().show(childFragmentManager, "")
        }
        viewModel.intentionsResponse.observe(viewLifecycleOwner) {
            intentions = it
            it.forEach {
                it.name?.let { intention ->
                    options.add(intention)
                }
            }
            binding.spIntenciones.apply {
                adapter =
                    object :
                        ArrayAdapter<String>(context, R.layout.spinner_textview_align, options) {
                        override fun getDropDownView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup?
                        ): View {
                            val v = super.getDropDownView(position, convertView, parent)
                            (v as TextView).gravity = Gravity.LEFT
                            return v
                        }
                    }
            }
        }
        val nombres =
            eamxcu_preferences.getData(
                EAMXEnumUser.USER_NAME.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String
        val apellido_paterno =
            eamxcu_preferences.getData(
                EAMXEnumUser.USER_LAST_NAME.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String
        val apellido_materno =
            eamxcu_preferences.getData(
                EAMXEnumUser.USER_MIDDLE_NAME.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String
        binding.etName.setText("$nombres $apellido_paterno $apellido_materno")
    }

    fun back() {
        activity?.onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val inputMethodManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    //Llenar spinner
    @SuppressLint("SetTextI18n")
    fun setSpiner() {
        binding.apply {
            val listMonto: ArrayList<String> = ArrayList()
            listMonto.add("Selecciona monto")
            listMonto.add("$50.00")
            listMonto.add("$100.00")
            listMonto.add("$200.00")
            listMonto.add("$300.00")
            listMonto.add("$400.00")
            listMonto.add("$500.00")
            listMonto.add("$1,000.00")
            listMonto.add("Otro")
            val adapterSpinnerMonto = ArrayAdapter(
                requireContext(),
                com.example.eamxdonaciones.R.layout.custom_spinner_zip_code, listMonto
            )
            customSpinner.setAdapter(adapterSpinnerMonto)
        }
    }

    fun validaMonto(): Boolean {
        val valida = true
        binding.apply {
            if (customSpinner.spMonto.selectedItemPosition <= 0) {
                UtilAlert.Builder()
                    .setIsCancel(false)
                    .setTitle("Aviso")
                    .setMessage("Seleccione un monto.")
                    .build()
                    .show(childFragmentManager, "")
                return false
            } else if (customSpinner.spMonto.selectedItemPosition == 8 && customSpinner.etMonto.text.toString()
                    .isEmpty()
            ) {
                UtilAlert.Builder()
                    .setTitle("Aviso")
                    .setMessage("Seleccione un monto.")
                    .setIsCancel(false)
                    .build()
                    .show(childFragmentManager, "")
                return false
            }
            if (customSpinner.spMonto.selectedItemPosition == 8) {
                try {
                    if (customSpinner.etMonto.text.toString().toNumber() < 50.00) {
                        UtilAlert.Builder()
                            .setIsCancel(false)
                            .setTitle("Aviso")
                            .setMessage("¡Gracias! Desafortunadamente no podemos recibir ofrendas menores a \$50.00 pesos.")
                            .build()
                            .show(childFragmentManager, "")
                        return false
                    }
                    if (customSpinner.etMonto.text.toString().toNumber() > 10000.00) {
                        UtilAlert.Builder()
                            .setIsCancel(false)
                            .setTitle("Aviso")
                            .setMessage("No es posible recibir ofrendas superiores a \$10,000.00 pesos. Cualquier duda favor de comunicarte a contacto@miofrenda.mx.")
                            .build()
                            .show(childFragmentManager, "")
                        return false
                    }
                } catch (e: Error) {
                    UtilAlert.Builder()
                        .setIsCancel(false)
                        .setTitle("Aviso")
                        .setMessage("Seleccione un monto válido.")
                        .build()
                        .show(childFragmentManager, "")
                    return false
                }
            }
        }
        return valida
    }

    //Armar url para generar cobro e encriptar datos
    fun setLink(): String {
        val monto = if (binding.customSpinner.spMonto.selectedItemPosition == 8) {
            binding.customSpinner.etMonto.text.toString().toNumber()
        } else {
            binding.customSpinner.spMonto.selectedItem.toString().toNumber()
        }
        val userName =
            eamxcu_preferences.getData(
                EAMXEnumUser.USER_EMAIL.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String
        val phoneNumber = eamxcu_preferences.getData(
            EAMXEnumUser.USER_PHONE.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String
        val name =
            eamxcu_preferences.getData(
                EAMXEnumUser.USER_NAME.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String
        val surnames =
            eamxcu_preferences.getData(
                EAMXEnumUser.USER_LAST_NAME.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String
        val modelWV =
            ModelWebView(
                monto.toString(),
                userName,
                locationId.toString(),
                name,
                "68844",
                phoneNumber,
                surnames,
                null,
                null,
                null,
                null,
                null,
                null,
            )
        return "${WebConfig.URLDONATION}pagos/data/v2?data=" + URLEncoder.encode(
            gson.toJson(modelWV).apply {
                eamxLog(this)
            }.encryptData().apply(::eamxLog)
        )

    }

    //Poner formulario de pago
    private fun changeFragment(link: String) {
        val bundle = Bundle()
        bundle.putString(
            "concepto", binding.spIntenciones.selectedItem.toString() + " "
                    + binding.etDescription.text
        )
        bundle.putString("monto", getAmount() + " M. N.")
        bundle.putString("mentionInformation", gson.toJson(mentionInformation))
        NavigationFragment.Builder()
            .setActivity(requireActivity())
            .setView(requireView().parent as ViewGroup)
            .setBundle(bundle)
            .setFragment(
                ComponentFragment(
                    setLink(), OriginType.INTENCIONES
                )
            )
            .setAllowStack(true)
            .build().nextWithReplace()
    }

    private fun getAmount(): String? {
        if (validaMonto()) {
            if (binding.customSpinner.spMonto.selectedItemPosition == 8 && binding.customSpinner.etMonto.text.toString()
                    .isNotEmpty()
            ) {
                amount = "$" + binding.customSpinner.etMonto.text.toString() + ".00"
            } else {
                amount = binding.customSpinner.spMonto.selectedItem.toString()
            }
        }
        return amount
    }
}