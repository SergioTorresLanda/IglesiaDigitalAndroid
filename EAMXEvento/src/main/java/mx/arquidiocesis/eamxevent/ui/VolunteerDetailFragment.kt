package mx.arquidiocesis.eamxevent.ui

import android.Manifest
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_event_detail.*
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXFieldValidation
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.base.TimePickerFragment
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.multimedia.MapsFragment
import mx.arquidiocesis.eamxcommonutils.multimedia.PERMISSION_LOCATION
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.permission.UtilValidPermission
import mx.arquidiocesis.eamxevent.R
import mx.arquidiocesis.eamxevent.constants.Constants
import mx.arquidiocesis.eamxevent.databinding.FragmentDonorBinding
import mx.arquidiocesis.eamxevent.databinding.FragmentDonorDetailBinding
import mx.arquidiocesis.eamxevent.databinding.FragmentVolunteerDetailBinding
import mx.arquidiocesis.eamxevent.model.Day
import mx.arquidiocesis.eamxevent.model.ViewModelDonor
import mx.arquidiocesis.eamxevent.model.ViewModelEvent
import mx.arquidiocesis.eamxevent.model.enum.Delegations
import mx.arquidiocesis.eamxevent.repository.RepositoryEvent

class VolunteerDetailFragment : FragmentBase() {

    lateinit var binding: FragmentVolunteerDetailBinding
    private val TAG_LOADER: String = "EventFragment"
    private var delegations: Array<Delegations> = Delegations.values()
    private var hourFirst = ""
    private var hourEnd = ""
    private var zona: Int = 0
    private var diner_id: Int = 0
    var latitude: Double = 0.0
    var longitude: Double = 0.0

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

    lateinit var viewmodel: ViewModelEvent

    companion object {
        fun newInstance(callBack: EAMXHome): VolunteerDetailFragment {
            val fragment = VolunteerDetailFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    private val viewModelDonor: ViewModelDonor by lazy {
        getViewModel {
            ViewModelDonor(
                RepositoryEvent(requireContext())
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        viewmodel = ViewModelEvent(RepositoryEvent(requireContext()))
        // Inflate thelayout for this fragment
        binding = FragmentVolunteerDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "Actividades_CrearComedor")
            })
        }
        listDays.add(Day(false, mx.arquidiocesis.eamxevent.model.enum.Day.Domingo.ordinal, mx.arquidiocesis.eamxevent.model.enum.Day.Domingo.name))
        listDays.add(Day(false, mx.arquidiocesis.eamxevent.model.enum.Day.Lunes.ordinal, mx.arquidiocesis.eamxevent.model.enum.Day.Lunes.name))
        listDays.add(Day(false, mx.arquidiocesis.eamxevent.model.enum.Day.Martes.ordinal, mx.arquidiocesis.eamxevent.model.enum.Day.Martes.name))
        listDays.add(Day(false, mx.arquidiocesis.eamxevent.model.enum.Day.Miercoles.ordinal, mx.arquidiocesis.eamxevent.model.enum.Day.Miercoles.name))
        listDays.add(Day(false, mx.arquidiocesis.eamxevent.model.enum.Day.Jueves.ordinal, mx.arquidiocesis.eamxevent.model.enum.Day.Jueves.name))
        listDays.add(Day(false, mx.arquidiocesis.eamxevent.model.enum.Day.Viernes.ordinal, mx.arquidiocesis.eamxevent.model.enum.Day.Viernes.name))
        listDays.add(Day(false, mx.arquidiocesis.eamxevent.model.enum.Day.Sabado.ordinal, mx.arquidiocesis.eamxevent.model.enum.Day.Sabado.name))

        initView()
        initObservers()
        etEmail.setText(email)
        etNumberPhone.setText(phone.replace("+52", ""))
        requireArguments().let {
            var id = it.getString("diner_id")
            if (id != "") {
                callBack.showToolbar(true, AppMyConstants.updateEvento)
                id?.let { it1 ->
                    diner_id = it1.toInt()
                    getAllDiners(diner_id)
                }
            } else {
                callBack.showToolbar(true, AppMyConstants.detailEvento)
            }
        }
    }

    private fun initObservers() {
        viewmodel.responseAllDin.observe(viewLifecycleOwner) { item ->
            hideLoader()
            if (item.size > 0) {
                etNombreC.setText(item[0].fCNOMBRECOM)
                etgetAddress.setText(item[0].fCDIRECCION)
                delegations.forEach {
                    if (it.pos.toString() == item[0].fIZONA) {
                        spZone.setSelection(it.ordinal)
                        return@forEach
                    }
                }
                if (!(item[0].fNLATITUD).isNullOrEmpty()) {
                    latitude = item[0].fNLATITUD!!.toDouble()
                }
                if (!(item[0].fNLONGITUD).isNullOrEmpty()) {
                    longitude = item[0].fNLONGITUD!!.toDouble()
                }
                listDays[0].checked = item[0].fCHORARIOS!![0].days!![0].checked
                if (!listDays[0].checked) {
                    binding.iDays.iDayDo.tvCDay.setTextColor(Color.BLACK)
                } else {
                    binding.iDays.iDayDo.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
                listDays[1].checked = item[0].fCHORARIOS!![0].days!![1].checked
                if (!listDays[1].checked) {
                    binding.iDays.iDayLu.tvCDay.setTextColor(Color.BLACK)
                } else {
                    binding.iDays.iDayLu.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
                listDays[2].checked = item[0].fCHORARIOS!![0].days!![2].checked
                if (!listDays[2].checked) {
                    binding.iDays.iDayMa.tvCDay.setTextColor(Color.BLACK)
                } else {
                    binding.iDays.iDayMa.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
                listDays[3].checked = item[0].fCHORARIOS!![0].days!![3].checked
                if (!listDays[3].checked) {
                    binding.iDays.iDayMi.tvCDay.setTextColor(Color.BLACK)
                } else {
                    binding.iDays.iDayMi.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
                listDays[4].checked = item[0].fCHORARIOS!![0].days!![4].checked
                if (!listDays[4].checked) {
                    binding.iDays.iDayJu.tvCDay.setTextColor(Color.BLACK)
                } else {
                    binding.iDays.iDayJu.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
                listDays[5].checked = item[0].fCHORARIOS!![0].days!![5].checked
                if (!listDays[5].checked) {
                    binding.iDays.iDayVi.tvCDay.setTextColor(Color.BLACK)
                } else {
                    binding.iDays.iDayVi.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
                listDays[6].checked = item[0].fCHORARIOS!![0].days!![6].checked
                if (!listDays[6].checked) {
                    binding.iDays.iDaySa.tvCDay.setTextColor(Color.BLACK)
                } else {
                    binding.iDays.iDaySa.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
                switch1.isChecked = item[0].fCCOBRO != "0"
                etMonto.setText(item[0].fCCOBRO)

                val hora_first = item[0].fCHORARIOS!![0].hour_start!!.split(":")
                FirstSchedule(hora_first[0].toInt(), hora_first[1].toInt())
                val hora_end = item[0].fCHORARIOS!![0].hour_end!!.split(":")
                EndSchedule(hora_end[0].toInt(), hora_end[1].toInt())
                etResponsable.setText(item[0].fCRESPONSABLE)
                etNumberPhone.setText(item[0].fCTELEFONO!!.replace("+52", ""))
                etRequisitos.setText(item[0].fCREQUISITOS)
                switch2.isChecked = item[0].fCVOLUNTARIOS == "1"
                switch3.isChecked = item[0].fCSTATUS == "1"
            }
        }
        viewModelDonor.showLoaderView.observe(viewLifecycleOwner) {
            showLoader()
        }
        viewModelDonor.validateForm.observe(viewLifecycleOwner) {
            if (it.containsKey(Constants.KEY_NAME)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_name))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_ADDRESS) || it.containsKey(Constants.KEY_LONGITUDE) || it.containsKey(
                    Constants.KEY_LATITUDE
                )
            ) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_location))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_ZONE)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_zone))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_DAYS)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_day))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_HOUR_FIRST)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_hour_first))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_HOUR_END)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_hour_end))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_RESPONSABILITY)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_responsability))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_EMAIL)) {
                if (it[Constants.KEY_EMAIL] == Constants.INVALID_EMAIL) {
                    etEmail.error =
                        getString(R.string.txt_invalidate_email)
                } else {
                    UtilAlert.Builder()
                        .setTitle(getString(R.string.title_dialog_error))
                        .setMessage(getString(R.string.txt_empty_email))
                        .setIsCancel(false)
                        .build().show(childFragmentManager, tag)
                }
            } else if (it.containsKey(Constants.KEY_PHONE)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_phone))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_PHONE)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_invalid_phone))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_REQUISIT)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_requi))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            }
            hideLoader()
        }
        viewModelDonor.saveResponse.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert.Builder()
                .setTitle("Éxito")
                .setMessage(it)
                .setListener {
                    activity?.onBackPressedDispatcher?.onBackPressed()
                }
                .setIsCancel(false)
                .build()
                .show(childFragmentManager, TAG_LOADER)
        }
        viewModelDonor.errorResponse.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(it)
                .setListener {
                    hideLoader()
                }
                .setIsCancel(false)
                .build()
                .show(childFragmentManager, TAG_LOADER)
        }
    }

    private fun initView() {

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
            etgetAddress.hint =
                "Actualiza la ubicación de tu comedor en el mapa para obtener la dirección"
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
                } else {
                    binding.iDays.iDayDo.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                }
            }

            iDays.iDayLu.tvCDay.setOnClickListener {
                listDays[1].checked = !listDays[1].checked
                if (!listDays[1].checked) {
                    binding.iDays.iDayLu.tvCDay.setTextColor(Color.BLACK)
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                } else {
                    binding.iDays.iDayLu.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                }
            }

            iDays.iDayMa.tvCDay.setOnClickListener {
                listDays[2].checked = !listDays[2].checked
                if (!listDays[2].checked) {
                    binding.iDays.iDayMa.tvCDay.setTextColor(Color.BLACK)
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                } else {
                    binding.iDays.iDayMa.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                }
            }
            iDays.iDayMi.tvCDay.setOnClickListener {
                listDays[3].checked = !listDays[3].checked
                if (!listDays[3].checked) {
                    binding.iDays.iDayMi.tvCDay.setTextColor(Color.BLACK)
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                } else {
                    binding.iDays.iDayMi.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                }
            }

            iDays.iDayJu.tvCDay.setOnClickListener {
                listDays[4].checked = !listDays[4].checked
                if (!listDays[4].checked) {
                    binding.iDays.iDayJu.tvCDay.setTextColor(Color.BLACK)
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                } else {
                    binding.iDays.iDayJu.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                }
            }

            iDays.iDayVi.tvCDay.setOnClickListener {
                listDays[5].checked = !listDays[5].checked
                if (!listDays[5].checked) {
                    binding.iDays.iDayVi.tvCDay.setTextColor(Color.BLACK)
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                } else {
                    binding.iDays.iDayVi.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                }
            }

            iDays.iDaySa.tvCDay.setOnClickListener {
                listDays[6].checked = !listDays[6].checked
                if (!listDays[6].checked) {
                    binding.iDays.iDaySa.tvCDay.setTextColor(Color.BLACK)
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                } else {
                    binding.iDays.iDaySa.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                }
            }

            switch1.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    lMonto.visibility = View.VISIBLE
                    switch1.thumbTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.green_retirar)
                } else {
                    etMonto.setText("0")
                    lMonto.visibility = View.GONE
                    switch1.thumbTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.hint_color)
                }
            }


            switch2.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    switch2.thumbTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.green_retirar)
                } else {
                    switch2.thumbTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.hint_color)
                }
            }

            switch3.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    switch3.thumbTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.green_retirar)
                    tvDisponible.setText("Disponible")
                } else {
                    switch3.thumbTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.hint_color)
                    tvDisponible.setText("No Disponible")
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
                    id: Long,
                ) {
                    zona = delegations[position].pos
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    lblSeleccion.text = "Sin selección"
                }
            }



            btnGuardar.setOnClickListener { volunteerRegister() }
            tvAddress.setOnClickListener { showMap() }
            tvFirstH.setOnClickListener { showTimePickerFirst() }
            ivFirstH.setOnClickListener { showTimePickerFirst() }
            tvEndH.setOnClickListener { showTimePickerEnd() }
            ivEndH.setOnClickListener { showTimePickerEnd() }
        }
    }

    fun showMap() {
        if (chechPermissions()) {
            MapsFragment(latitude, longitude) { rlatitude, rlongitude, raddress, municipality ->
                etgetAddress.setText(raddress)
                latitude = rlatitude
                longitude = rlongitude
                println(latitude.toString() + "to" + longitude.toString())
                delegations.forEach {
                    if (it.pos == municipality) {
                        spZone.setSelection(it.ordinal)
                        return@forEach
                    }
                }
            }.show(childFragmentManager, TAG_LOADER)
        }
    }

    private fun chechPermissions(): Boolean {
        return UtilValidPermission().validListPermissionsAndBuildRequest(
            this@VolunteerDetailFragment, arrayListOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_LOCATION
        )
    }

    fun showTimePickerFirst() {
        val timePicker =
            TimePickerFragment(isMax = true) { hour, minute -> FirstSchedule(hour, minute) }
        timePicker.show(parentFragmentManager, "timePicker")
    }

    private fun FirstSchedule(hour: Int, minute: Int) {
        val hourCurrent = if (hour < 10) "0$hour" else hour
        val minuteCurrent = if (minute < 10) "0$minute" else minute
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
        val minuteCurrent = if (minute < 10) "0$minute" else minute
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
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_check_24)
                input.setEndIconTintList(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.success
                        )
                    )
                )
            }
            false -> {
                input.endIconDrawable =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_error)
                input.setEndIconTintList(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
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

    private fun volunteerRegister() {
        // val listSchedules: MutableList<Schedules> =
        //   mutableListOf(Schedules(listDays, tvFirstH.text.toString(), tvEndH.text.toString()))

        viewModelDonor.validateFormRegisterVolunteer(
            "Periferico sur",
            "",
            "yane@gmail.com",
            "21",
            "Yaneli",
            "+527641027834",
            "Alfredo"
        )
    }

    fun getAllDiners(id: Int) {
        showLoader()
        viewmodel.requestAllDiner(id)
    }
}