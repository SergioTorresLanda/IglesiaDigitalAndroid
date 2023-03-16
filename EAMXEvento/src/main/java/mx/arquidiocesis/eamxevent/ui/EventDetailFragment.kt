package mx.arquidiocesis.eamxevent.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat.*
import androidx.core.view.isEmpty
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_event_detail.*
import mx.arquidiocesis.eamxcommonutils.api.core.status.EAMXStatusRequestEnum
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXFieldValidation
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.base.TimePickerFragment
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxevent.R
import mx.arquidiocesis.eamxevent.constants.Constants
import mx.arquidiocesis.eamxevent.databinding.FragmentEventDetailBinding
import mx.arquidiocesis.eamxevent.model.*
import mx.arquidiocesis.eamxevent.model.enum.Delegations
import mx.arquidiocesis.eamxevent.repository.RepositoryEvent
import mx.arquidiocesis.eamxevent.model.enum.Day as week

class EventDetailFragment : FragmentBase() {

    lateinit var binding: FragmentEventDetailBinding
    private var hourFirst = ""
    private var hourEnd = ""
    private val TAG_LOADER: String = "EventFragment"
    private var fragment = DialogFragment()
    private var delegations: Array<Delegations> = Delegations.values()
    private var zona : Int = 0

    var listDays: MutableList<Day> = mutableListOf()

    val email = eamxcu_preferences.getData(
        EAMXEnumUser.USER_EMAIL.name,
        EAMXTypeObject.STRING_OBJECT
    ) as String

    val userId = eamxcu_preferences.getData(
        EAMXEnumUser.USER_ID.name,
        EAMXTypeObject.INT_OBJECT
    ) as Int

    val phone = eamxcu_preferences.getData(
        EAMXEnumUser.USER_PHONE.name,
        EAMXTypeObject.STRING_OBJECT
    ) as String

    companion object {
        fun newInstance(callBack: EAMXHome): EventDetailFragment {
            val fragment = EventDetailFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    private val viewModelEvent: ViewModelEvent by lazy {
        getViewModel {
            ViewModelEvent(
                RepositoryEvent(requireContext())
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Inflate thelayout for this fragment
        binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listDays.add(Day(false, week.Domingo.ordinal, week.Domingo.name))
        listDays.add(Day(false, week.Lunes.ordinal, week.Lunes.name))
        listDays.add(Day(false, week.Martes.ordinal, week.Martes.name))
        listDays.add(Day(false, week.Miercoles.ordinal, week.Miercoles.name))
        listDays.add(Day(false, week.Jueves.ordinal, week.Jueves.name))
        listDays.add(Day(false, week.Viernes.ordinal, week.Viernes.name))
        listDays.add(Day(false, week.Sabado.ordinal, week.Sabado.name))

        initView()
        initObservers()

    }

    private fun initObservers() {

        etEmail.text = Editable.Factory.getInstance().newEditable(email)
        etEmail.isEnabled = false


        viewModelEvent.showLoaderView.observe(viewLifecycleOwner) {
            showLoader()
        }

        viewModelEvent.validateForm.observe(viewLifecycleOwner) {
            if (it.containsKey(Constants.KEY_NAME)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_name))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            }
            if (it.containsKey(Constants.KEY_ADDRESS)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_location))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            }

            if (it.containsKey(Constants.KEY_DAYS)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_day))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            }

            if (it.containsKey(Constants.KEY_RESPONSABILITY)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_responsability))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            }

            if (it.containsKey(Constants.KEY_HOUR_FIRST)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_hour_first))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            }

            if (it.containsKey(Constants.KEY_HOUR_END)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_hour_end))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            }
            if (it.containsKey(Constants.KEY_PHONE)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_phone))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            }
            if (it.containsKey(Constants.KEY_PHONE)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_invalid_phone))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            }

            if (it.containsKey(Constants.KEY_EMAIL))
                if (it[Constants.KEY_EMAIL] == Constants.INVALID_EMAIL) {
                    etEmail.error = getString(R.string.txt_invalidate_email)
                } else {
                    UtilAlert.Builder()
                        .setTitle(getString(R.string.title_dialog_error))
                        .setMessage(getString(R.string.txt_empty_email))
                        .setIsCancel(false)
                        .build().show(childFragmentManager, tag)
                }

            if (it.containsKey(Constants.KEY_REQUISIT)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_requi))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            }

            if (it.containsKey(Constants.KEY_ZONE)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_zone))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            }
            hideLoader()
        }

        viewModelEvent.responseGeneric.observe(viewLifecycleOwner) { response ->
            when (response.statusRequest) {
                EAMXStatusRequestEnum.LOADING -> {
                    showLoader()
                }
                EAMXStatusRequestEnum.SUCCESS -> {
                    hideLoader()
                    UtilAlert.Builder()
                        .setTitle("Éxito")
                        .setMessage("Se ha dado de alta el comedor correctamente.")
                        .setListener {
                            hideLoader()
                        }
                        .setIsCancel(false)
                        .build()
                        .show(childFragmentManager, TAG_LOADER)
                    fragment.show(childFragmentManager, "LOADER")
                }
                EAMXStatusRequestEnum.FAILURE -> {
                    hideLoader()
                    UtilAlert.Builder()
                        .setTitle("Atención")
                        .setMessage("Verifique sus datos e intentelo de nuevo.")
                        .setListener {
                            hideLoader()
                        }
                        .setIsCancel(false)
                        .build()
                        .show(childFragmentManager, TAG_LOADER)
                    fragment.show(childFragmentManager, "LOADER")
                }
                EAMXStatusRequestEnum.NONE -> {
                    hideLoader()
                }
                else -> {}
            }
        }
        viewModelEvent.errorResponse.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert.Builder()
                //setTitle(getString(R.string.title_dialog_warning))
                //.setMessage("No es posible agregar el comedor.")
                .setTitle("Éxito")
                .setMessage("Se ha dado de alta el comedor correctamente.")
                .setListener {
                    hideLoader()
                }
                .setIsCancel(false)
                .build()
                .show(childFragmentManager, TAG_LOADER)
        }

    }

    private fun initView() {
        callBack.showToolbar(true, AppMyConstants.detailEvento)

        binding.iDays.iDayMa.tvCDay.setText("Lu")
        binding.iDays.iDayMa.tvCDay.setText("Ma")
        binding.iDays.iDayMi.tvCDay.setText("Mi")
        binding.iDays.iDayJu.tvCDay.setText("Ju")
        binding.iDays.iDayVi.tvCDay.setText("Vi")
        binding.iDays.iDaySa.tvCDay.setText("Sa")
        binding.iDays.iDayDo.tvCDay.setText("Do")

        binding.apply {

            etNombreC.hint = "Nombre del comedor"
            etResponsable.hint = "Tu nombre y apellido"

            etNombreC.addTextChangedListener {
                if (etNombreC.text.toString().isNotEmpty()) {
                    tilNombreC.error = null
                    enableIconStart(tilNombreC, true)
                } else {
                    tilNombreC.error = getString(R.string.enter_diner_name)
                    enableIconStart(tilNombreC, null)
                }
            }

            tvFirstH.addTextChangedListener {
                if (tvFirstH.text.toString().isEmpty()) {
                    tvFirstH.error = null
                } else {
                    tvFirstH.error = getString(R.string.enter_diner_fh)
                }
            }

            tvEndH.addTextChangedListener {
                if (tvEndH.text.toString().isNotEmpty()) {
                    tvEndH.error = null
                } else {
                    tvEndH.error = getString(R.string.enter_diner_eh)
                }
            }

            etResponsable.addTextChangedListener {
                if (etResponsable.text.toString().isNotEmpty()) {
                    tilResponsable.error = null
                    enableIconStart(tilResponsable, true)
                } else {
                    tilResponsable.error = getString(R.string.enter_your_name)
                    enableIconStart(tilResponsable, null)
                }
            }

            etRequisitos.addTextChangedListener {
                if (etRequisitos.text.toString().isNotEmpty()) {
                    tilRequisitos.error = null
                    enableIconStart(tilRequisitos, true)
                } else {
                    tilRequisitos.error = getString(R.string.enter_your_req)
                    enableIconStart(tilRequisitos, null)
                }
            }

           etNumberPhone.addTextChangedListener {
               val validatePhone = etNumberPhone.text.toString().validNumberPhoneContent()
               enableIconStart(
                   tilNumberPhone,
                   validatePhone
               )
               if (etNumberPhone.text.toString().isEmpty()) {
                   enableIconStart(tilNumberPhone, null)
                   tilNumberPhone.isEmpty()
                   tilNumberPhone.error = getString(R.string.min_phone)
               } else {
                   if (EAMXFieldValidation.validateNumberPhone(etNumberPhone.text.toString()) && EAMXFieldValidation.validateNumberLength(
                           etNumberPhone.text.toString()
                       )
                   ) {
                       tilNumberPhone.error = null
                   }
                   if (!EAMXFieldValidation.validateNumberPhone(etNumberPhone.text.toString())) {
                       tilNumberPhone.error = getString(R.string.wrong_phone_number)
                   }
                   if (!EAMXFieldValidation.validateNumberLength(etNumberPhone.text.toString())) {
                       tilNumberPhone.error = getString(R.string.min_phone)
                   }
               }
           }


           etEmail.addTextChangedListener {
               val text = it?.toString()
               text?.let { emailTxt ->
                   enableIconStart(tilEmail, Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches())
                   tilEmail.error = null
                   EAMXFieldValidation.validateEmail(emailTxt, tilEmail)
                   if (emailTxt.isEmpty()) {
                       enableIconStart(tilEmail, null)
                   }
               }
           }
        }
        initButtons()
    }

    private fun initButtons() {
        binding.apply {

            iDays.iDayDo.tvCDay.setOnClickListener {
                listDays[0].checked = !listDays[0].checked
                if (!listDays[0].checked) {
                    binding.iDays.iDayDo.tvCDay.setTextColor(Color.BLACK)
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                    println("Object to JSON string:$jsonString")
                } else {
                    binding.iDays.iDayDo.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                    println("Object to JSON string:$jsonString")
                }
            }

            iDays.iDayLu.tvCDay.setOnClickListener {
                listDays[1].checked = !listDays[1].checked
                if (!listDays[1].checked) {
                    binding.iDays.iDayLu.tvCDay.setTextColor(Color.BLACK)
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                    println("Object to JSON string:$jsonString")
                } else {
                    binding.iDays.iDayLu.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                    println("Object to JSON string:$jsonString")
                }
            }

            iDays.iDayMa.tvCDay.setOnClickListener {
                listDays[2].checked = !listDays[2].checked
                if (!listDays[2].checked) {
                    binding.iDays.iDayMa.tvCDay.setTextColor(Color.BLACK)
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                    println("Object to JSON string:$jsonString")
                } else {
                    binding.iDays.iDayMa.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                    println("Object to JSON string:$jsonString")
                }
            }
            iDays.iDayMi.tvCDay.setOnClickListener {
                listDays[3].checked = !listDays[3].checked
                if (!listDays[3].checked) {
                    binding.iDays.iDayMi.tvCDay.setTextColor(Color.BLACK)
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                    println("Object to JSON string:$jsonString")
                } else {
                    binding.iDays.iDayMi.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                    println("Object to JSON string:$jsonString")
                }
            }

            iDays.iDayJu.tvCDay.setOnClickListener {
                listDays[4].checked = !listDays[4].checked
                if (!listDays[4].checked) {
                    binding.iDays.iDayJu.tvCDay.setTextColor(Color.BLACK)
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                    println("Object to JSON string:$jsonString")
                } else {
                    binding.iDays.iDayJu.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                    println("Object to JSON string:$jsonString")
                }
            }

            iDays.iDayVi.tvCDay.setOnClickListener {
                listDays[5].checked = !listDays[5].checked
                if (!listDays[5].checked) {
                    binding.iDays.iDayVi.tvCDay.setTextColor(Color.BLACK)
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                    println("Object to JSON string:$jsonString")
                } else {
                    binding.iDays.iDayVi.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                    println("Object to JSON string:$jsonString")
                }
            }

            iDays.iDaySa.tvCDay.setOnClickListener {
                listDays[6].checked = !listDays[6].checked
                if (!listDays[6].checked) {
                    binding.iDays.iDaySa.tvCDay.setTextColor(Color.BLACK)
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                    println("Object to JSON string:$jsonString")
                } else {
                    binding.iDays.iDaySa.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                    println("Object to JSON string:$jsonString")
                }
            }

            switch1.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    lMonto.visibility = View.VISIBLE
                    switch1.thumbTintList =
                        getColorStateList(requireContext(), R.color.green_retirar)
                } else {
                    lMonto.visibility = View.GONE
                    switch1.thumbTintList =
                        getColorStateList(requireContext(), R.color.hint_color)
                }
            }
            switch2.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    switch2.thumbTintList =
                        getColorStateList(requireContext(), R.color.green_retirar)
                } else {
                    switch2.thumbTintList =
                        getColorStateList(requireContext(), R.color.hint_color)
                }
            }
            switch3.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    switch3.thumbTintList =
                        getColorStateList(requireContext(), R.color.green_retirar)
                } else {
                    switch3.thumbTintList =
                        getColorStateList(requireContext(), R.color.hint_color)
                }
            }


            val adaptador = ArrayAdapter.createFromResource(
                requireContext(), R.array.delegations,
                android.R.layout.simple_spinner_dropdown_item
            )

            spZone.adapter = adaptador
            spZone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    zona = delegations[position].pos
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    lblSeleccion.text = "Sin selección"
                }
            }


            btnGuardar.setOnClickListener { eventRegister() }
            tvFirstH.setOnClickListener { showTimePickerFirst() }
            ivFirstH.setOnClickListener { showTimePickerFirst() }
            tvEndH.setOnClickListener { showTimePickerEnd() }
            ivEndH.setOnClickListener { showTimePickerEnd() }
        }
    }

    fun showTimePickerFirst() {
        val timePicker =
            TimePickerFragment(isMax = true) { hour, minute -> FirstSchedule(hour, minute) }
        timePicker.show(parentFragmentManager, "timePicker")
    }

    private fun FirstSchedule(hour: Int, minute: Int) {
        val hourCurrent = if (hour < 10) "0$hour" else hour
        val minuteCurrent = minute
        tvFirstH.setText("$hourCurrent:$minuteCurrent")
        hourFirst = "$hourCurrent:$minuteCurrent"
        tvFirstH.error = null
    }

    private fun showTimePickerEnd() {
        val timePicker =
            TimePickerFragment(isMax = true) { hour, minute -> EndSchedule(hour, minute) }
        timePicker.show(parentFragmentManager, "timePicker")
    }

    private fun EndSchedule(hour: Int, minute: Int) {
        val hourCurrent = if (hour < 10) "0$hour" else hour
        val minuteCurrent = minute
        tvEndH.setText("$hourCurrent:$minuteCurrent")
        hourEnd = "$hourCurrent:$minuteCurrent"
        tvEndH.error = null
    }

    private fun String.validNumberPhoneContent() =
        EAMXFieldValidation.validateNumberPhone(this) &&
                this.isNotEmpty() &&
                EAMXFieldValidation.validateNumberLength(this)

    fun enableIconStart(input: TextInputLayout, success: Boolean?) {
        when (success) {
            true -> {
                input.endIconDrawable =
                    getDrawable(requireContext(), R.drawable.ic_baseline_check_24)
                input.setEndIconTintList(
                    ColorStateList.valueOf(
                        getColor(
                            requireContext(),
                            R.color.success
                        )
                    )
                )
            }
            false -> {
                input.endIconDrawable = getDrawable(requireContext(), R.drawable.ic_check_error)
                input.setEndIconTintList(
                    ColorStateList.valueOf(
                        getColor(
                            requireContext(),
                            R.color.error
                        )
                    )
                )
            }
            null -> {
                input.endIconDrawable = null
            }
        }
    }

    private fun eventRegister() {

        val listSchedules: MutableList<Schedules> = mutableListOf(Schedules(listDays, tvFirstH.text.toString(), tvEndH.text.toString()))
        println("hola")

        viewModelEvent.validateFormRegister(
            etNombreC.text.toString().lowercase(),
            userId,
            listSchedules,
            etResponsable.text.toString().trim(),
            etEmail.text.toString().trim(),
            etNumberPhone.text.toString().trim(),
            "Periferico sur",
            "443443",
            "445553",
            if (switch1.isChecked) etMonto.length() else 0,
            etRequisitos.text.toString().lowercase(),
            if (switch2.isChecked) 1 else 0,
            ArrayList(),
            zona,
            if (switch3.isChecked) 1 else 0,
        )
    }
}

