package mx.arquidiocesis.eamxevent.ui

import android.Manifest
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import mx.arquidiocesis.eamxevent.model.enum.Day as week
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_event_detail.*
import kotlinx.android.synthetic.main.fragment_event_pantries_detail.*
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXFieldValidation
import mx.arquidiocesis.eamxcommonutils.base.DatePickerFragment
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
import mx.arquidiocesis.eamxevent.model.Process
import mx.arquidiocesis.eamxevent.databinding.FragmentEventPantriesDetailBinding
import mx.arquidiocesis.eamxevent.model.Day
import mx.arquidiocesis.eamxevent.model.Schedules
import mx.arquidiocesis.eamxevent.model.ViewModelEvent
import mx.arquidiocesis.eamxevent.model.enum.Delegations
import mx.arquidiocesis.eamxevent.repository.RepositoryEvent

class EventPantriesDetailFragment : FragmentBase() {

    val userId = eamxcu_preferences.getData(
        EAMXEnumUser.USER_ID.name,
        EAMXTypeObject.INT_OBJECT
    ) as Int
    var listDaysArmed: MutableList<Day> = mutableListOf()
    var listDaysReceived: MutableList<Day> = mutableListOf()
    var listDaysDelivery: MutableList<Day> = mutableListOf()
    lateinit var binding: FragmentEventPantriesDetailBinding
    private var delegations: Array<Delegations> = Delegations.values()
    private var zona: Int = 0
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private var latitud_entrega: Double = 0.0
    private var longitud_entrega: Double = 0.0
    private val TAG_LOADER: String = "EventPantriesDetailFragment"
    private var hourFirst = ""
    private var hourEnd = ""
    private var dateFirst = ""
    private var dateEnd = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEventPantriesDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    private val viewModelEvent: ViewModelEvent by lazy {
        getViewModel {
            ViewModelEvent(
                RepositoryEvent(requireContext())
            )
        }
    }
    companion object {
        fun newInstance(callBack: EAMXHome): EventPantriesDetailFragment {
            val fragment = EventPantriesDetailFragment()
            fragment.callBack = callBack
            return fragment
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "Actividades_CrearDespensa")
            })
        }
        callBack.showToolbar(true, AppMyConstants.detailPantries)
        listDaysArmed.add(Day(false, week.Domingo.ordinal, week.Domingo.day))
        listDaysArmed.add(Day(false, week.Lunes.ordinal, week.Lunes.day))
        listDaysArmed.add(Day(false, week.Martes.ordinal, week.Martes.day))
        listDaysArmed.add(Day(false, week.Miercoles.ordinal, week.Miercoles.day))
        listDaysArmed.add(Day(false, week.Jueves.ordinal, week.Jueves.day))
        listDaysArmed.add(Day(false, week.Viernes.ordinal, week.Viernes.day))
        listDaysArmed.add(Day(false, week.Sabado.ordinal, week.Sabado.day))

        listDaysReceived.add(Day(false, week.Domingo.ordinal, week.Domingo.day))
        listDaysReceived.add(Day(false, week.Lunes.ordinal, week.Lunes.day))
        listDaysReceived.add(Day(false, week.Martes.ordinal, week.Martes.day))
        listDaysReceived.add(Day(false, week.Miercoles.ordinal, week.Miercoles.day))
        listDaysReceived.add(Day(false, week.Jueves.ordinal, week.Jueves.day))
        listDaysReceived.add(Day(false, week.Viernes.ordinal, week.Viernes.day))
        listDaysReceived.add(Day(false, week.Sabado.ordinal, week.Sabado.day))

        listDaysDelivery.add(Day(false, week.Domingo.ordinal, week.Domingo.day))
        listDaysDelivery.add(Day(false, week.Lunes.ordinal, week.Lunes.day))
        listDaysDelivery.add(Day(false, week.Martes.ordinal, week.Martes.day))
        listDaysDelivery.add(Day(false, week.Miercoles.ordinal, week.Miercoles.day))
        listDaysDelivery.add(Day(false, week.Jueves.ordinal, week.Jueves.day))
        listDaysDelivery.add(Day(false, week.Viernes.ordinal, week.Viernes.day))
        listDaysDelivery.add(Day(false, week.Sabado.ordinal, week.Sabado.day))

        binding.switchPantries.isChecked = true
        binding.switchDonorPantries.isChecked = true
        binding.switchArmadoPantries.isChecked = true
        binding.switchDeliveryPantries.isChecked = true
        initObservers()
        initView()
    }

    private fun initObservers(){
        viewModelEvent.showLoaderView.observe(viewLifecycleOwner) {
            showLoader()
        }
        viewModelEvent.validateForm.observe(viewLifecycleOwner) {
            if (it.containsKey(Constants.KEY_RESPONSABILITY)) {
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
            } else if (it.containsKey(Constants.KEY_ADDRESS) || it.containsKey(Constants.KEY_LONGITUDE) || it.containsKey(
                    Constants.KEY_LATITUDE
                )
            ) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_pantry))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_ZONE)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_zone))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            //Proceso recepción
            } else if (it.containsKey(Constants.KEY_RECEIVED)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_received))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_HOUR_FIRST_RECEIVED)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_hour_first_received))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_HOUR_END_RECEIVED)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_hour_end_received))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_DAYS_RECEIVED)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_dayReceived))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            //Proceso armado
            } else if (it.containsKey(Constants.KEY_ARMED)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_armed))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_HOUR_FIRST_ARMED)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_hour_first_armed))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_HOUR_END_ARMED)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_hour_end_armed))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_DAYS_ARMED)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_dayArmed))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            //Proceso de entrega
            } else if (it.containsKey(Constants.KEY_DELIVERY)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_delivery))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_HOUR_FIRST_DELIVERY)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_hour_first_delivery))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_HOUR_END_DELIVERY)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_hour_end_delivery))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_DAYS_DELIVERY)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_dayDelivery))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
                //Dirección de entrega
            } else if (it.containsKey(Constants.KEY_ADDRESS_DELIVERY) || it.containsKey(Constants.KEY_LONGITUDE_DELIVERY) || it.containsKey(
                    Constants.KEY_LATITUDE_DELIVERY
                )
            ) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_pantry_delivery))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
                hideLoader()
            }
        }
        viewModelEvent.saveResponse.observe(viewLifecycleOwner) {
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
        viewModelEvent.errorResponse.observe(viewLifecycleOwner) {
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

        binding.iDaysReception.iDayMa.tvCDay.setText("Lu")
        binding.iDaysReception.iDayMa.tvCDay.setText("Ma")
        binding.iDaysReception.iDayMi.tvCDay.setText("Mi")
        binding.iDaysReception.iDayJu.tvCDay.setText("Ju")
        binding.iDaysReception.iDayVi.tvCDay.setText("Vi")
        binding.iDaysReception.iDaySa.tvCDay.setText("Sa")
        binding.iDaysReception.iDayDo.tvCDay.setText("Do")

        binding.iDaysArmado.iDayMa.tvCDay.setText("Lu")
        binding.iDaysArmado.iDayMa.tvCDay.setText("Ma")
        binding.iDaysArmado.iDayMi.tvCDay.setText("Mi")
        binding.iDaysArmado.iDayJu.tvCDay.setText("Ju")
        binding.iDaysArmado.iDayVi.tvCDay.setText("Vi")
        binding.iDaysArmado.iDaySa.tvCDay.setText("Sa")
        binding.iDaysArmado.iDayDo.tvCDay.setText("Do")

        binding.iDaysDelivery.iDayMa.tvCDay.setText("Lu")
        binding.iDaysDelivery.iDayMa.tvCDay.setText("Ma")
        binding.iDaysDelivery.iDayMi.tvCDay.setText("Mi")
        binding.iDaysDelivery.iDayJu.tvCDay.setText("Ju")
        binding.iDaysDelivery.iDayVi.tvCDay.setText("Vi")
        binding.iDaysDelivery.iDaySa.tvCDay.setText("Sa")
        binding.iDaysDelivery.iDayDo.tvCDay.setText("Do")

        binding.apply {

            etResponPantries.hint = "Nombre y apellidos"
            etgetAddressPantries.hint =
                "Actualiza la ubicación en el mapa para obtener la dirección."
            etgetAddressDelivery.hint =
                "Actualiza la ubicación en el mapa para obtener la dirección."

            etResponPantries.addTextChangedListener {
                if (etResponPantries.text.toString().isNotEmpty()) {
                    tilResponPantries.error = null
                    enableIconStart(tilResponPantries, true)
                } else {
                    tilResponPantries.error = getString(R.string.enter_your_name)
                    enableIconStart(tilResponPantries, null)
                }
            }
            etEmailPantries.addTextChangedListener {
                val text = it?.toString()
                text?.let { emailTxt ->
                    enableIconStart(tilEmailPantries, Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches())
                    tilEmailPantries.error = null
                    EAMXFieldValidation.validateEmail(emailTxt, tilEmailPantries)
                    if (emailTxt.isEmpty()) {
                        enableIconStart(tilEmailPantries, null)
                    }
                }
            }
            etPhonePantries.addTextChangedListener {
                val validatePhone = etPhonePantries.text.toString().validNumberPhoneContent()
                enableIconStart(
                    tilPhonePantries,
                    validatePhone
                )
                if (etPhonePantries.text.toString().isEmpty()) {
                    enableIconStart(tilPhonePantries, null)
                    tilPhonePantries.isEmpty()
                    tilPhonePantries.error = getString(R.string.min_phone)
                } else {
                    if (EAMXFieldValidation.validateNumberPhone(etPhonePantries.text.toString()) && EAMXFieldValidation.validateNumberLength(
                            etPhonePantries.text.toString()
                        )
                    ) {
                        tilPhonePantries.error = null
                    }
                    if (!EAMXFieldValidation.validateNumberPhone(etPhonePantries.text.toString())) {
                        tilPhonePantries.error = getString(R.string.wrong_phone_number)
                    }
                    if (!EAMXFieldValidation.validateNumberLength(etPhonePantries.text.toString())) {
                        tilPhonePantries.error = getString(R.string.min_phone)
                    }
                }
            }
            etDescriptionPantries.addTextChangedListener {
                var count = etDescriptionPantries.text.toString().length
                tvDescriptionConteoPantries.setText("$count/250")
                if (etDescriptionPantries.text.toString().isNotEmpty()) {
                    tilDescriptionPantries.error = null
                    enableIconStart(tilDescriptionPantries, true)
                } else {
                    tilDescriptionPantries.error = getString(R.string.enter_your_des)
                    enableIconStart(tilDescriptionPantries, null)
                }
            }
            etRequisPantries.addTextChangedListener {
                var count = etRequisPantries.text.toString().length
                tvRequisConteoPantries.setText("$count/250")
                if (etRequisPantries.text.toString().isNotEmpty()) {
                    tilRequisPantries.error = null
                    enableIconStart(tilRequisPantries, true)
                } else {
                    tilRequisPantries.error = getString(R.string.enter_your_req)
                    enableIconStart(tilRequisPantries, null)
                }
            }
        }
        initButtons()
    }

    private fun initButtons(){

        binding.apply {

            //Días recepción de despensas
            iDaysReception.iDayDo.tvCDay.setOnClickListener {
                listDaysReceived[0].checked = !listDaysReceived[0].checked
                if (!listDaysReceived[0].checked) {
                    iDaysReception.iDayDo.tvCDay.setTextColor(Color.BLACK)
                } else {
                    iDaysReception.iDayDo.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
            }
            iDaysReception.iDayLu.tvCDay.setOnClickListener {
                listDaysReceived[1].checked = !listDaysReceived[1].checked
                if (!listDaysReceived[1].checked) {
                    iDaysReception.iDayLu.tvCDay.setTextColor(Color.BLACK)
                } else {
                    iDaysReception.iDayLu.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
            }
            iDaysReception.iDayMa.tvCDay.setOnClickListener {
                listDaysReceived[2].checked = !listDaysReceived[2].checked
                if (!listDaysReceived[2].checked) {
                    iDaysReception.iDayMa.tvCDay.setTextColor(Color.BLACK)
                } else {
                    iDaysReception.iDayMa.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
            }
            iDaysReception.iDayMi.tvCDay.setOnClickListener {
                listDaysReceived[3].checked = !listDaysReceived[3].checked
                if (!listDaysReceived[3].checked) {
                    iDaysReception.iDayMi.tvCDay.setTextColor(Color.BLACK)
                } else {
                    iDaysReception.iDayMi.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
            }
            iDaysReception.iDayJu.tvCDay.setOnClickListener {
                listDaysReceived[4].checked = !listDaysReceived[4].checked
                if (!listDaysReceived[4].checked) {
                    iDaysReception.iDayJu.tvCDay.setTextColor(Color.BLACK)
                } else {
                    iDaysReception.iDayJu.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
            }
            iDaysReception.iDayVi.tvCDay.setOnClickListener {
                listDaysReceived[5].checked = !listDaysReceived[5].checked
                if (!listDaysReceived[5].checked) {
                    iDaysReception.iDayVi.tvCDay.setTextColor(Color.BLACK)
                } else {
                    iDaysReception.iDayVi.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
            }
            iDaysReception.iDaySa.tvCDay.setOnClickListener {
                listDaysReceived[6].checked = !listDaysReceived[6].checked
                if (!listDaysReceived[6].checked) {
                    iDaysReception.iDaySa.tvCDay.setTextColor(Color.BLACK)
                } else {
                    iDaysReception.iDaySa.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
            }
            //Días armado de despensas
            iDaysArmado.iDayDo.tvCDay.setOnClickListener {
                listDaysArmed[0].checked = !listDaysArmed[0].checked
                if (!listDaysArmed[0].checked) {
                    iDaysArmado.iDayDo.tvCDay.setTextColor(Color.BLACK)
                } else {
                    iDaysArmado.iDayDo.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
            }
            iDaysArmado.iDayLu.tvCDay.setOnClickListener {
                listDaysArmed[1].checked = !listDaysArmed[1].checked
                if (!listDaysArmed[1].checked) {
                    iDaysArmado.iDayLu.tvCDay.setTextColor(Color.BLACK)
                } else {
                    iDaysArmado.iDayLu.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
            }
            iDaysArmado.iDayMa.tvCDay.setOnClickListener {
                listDaysArmed[2].checked = !listDaysArmed[2].checked
                if (!listDaysArmed[2].checked) {
                    iDaysArmado.iDayMa.tvCDay.setTextColor(Color.BLACK)
                } else {
                    iDaysArmado.iDayMa.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
            }
            iDaysArmado.iDayMi.tvCDay.setOnClickListener {
                listDaysArmed[3].checked = !listDaysArmed[3].checked
                if (!listDaysArmed[3].checked) {
                    iDaysArmado.iDayMi.tvCDay.setTextColor(Color.BLACK)
                } else {
                    iDaysArmado.iDayMi.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
            }
            iDaysArmado.iDayJu.tvCDay.setOnClickListener {
                listDaysArmed[4].checked = !listDaysArmed[4].checked
                if (!listDaysArmed[4].checked) {
                    iDaysArmado.iDayJu.tvCDay.setTextColor(Color.BLACK)
                } else {
                    iDaysArmado.iDayJu.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
            }
            iDaysArmado.iDayVi.tvCDay.setOnClickListener {
                listDaysArmed[5].checked = !listDaysArmed[5].checked
                if (!listDaysArmed[5].checked) {
                    iDaysArmado.iDayVi.tvCDay.setTextColor(Color.BLACK)
                } else {
                    iDaysArmado.iDayVi.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
            }
            iDaysArmado.iDaySa.tvCDay.setOnClickListener {
                listDaysArmed[6].checked = !listDaysArmed[6].checked
                if (!listDaysArmed[6].checked) {
                    iDaysArmado.iDaySa.tvCDay.setTextColor(Color.BLACK)
                } else {
                    iDaysArmado.iDaySa.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
            }
            //Días entrega de despensas
            iDaysDelivery.iDayDo.tvCDay.setOnClickListener {
                listDaysDelivery[0].checked = !listDaysDelivery[0].checked
                if (!listDaysDelivery[0].checked) {
                    iDaysDelivery.iDayDo.tvCDay.setTextColor(Color.BLACK)
                } else {
                    iDaysDelivery.iDayDo.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
            }
            iDaysDelivery.iDayLu.tvCDay.setOnClickListener {
                listDaysDelivery[1].checked = !listDaysDelivery[1].checked
                if (!listDaysDelivery[1].checked) {
                    iDaysDelivery.iDayLu.tvCDay.setTextColor(Color.BLACK)
                } else {
                    iDaysDelivery.iDayLu.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
            }
            iDaysDelivery.iDayMa.tvCDay.setOnClickListener {
                listDaysDelivery[2].checked = !listDaysDelivery[2].checked
                if (!listDaysDelivery[2].checked) {
                    iDaysDelivery.iDayMa.tvCDay.setTextColor(Color.BLACK)
                } else {
                    iDaysDelivery.iDayMa.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
            }
            iDaysDelivery.iDayMi.tvCDay.setOnClickListener {
                listDaysDelivery[3].checked = !listDaysDelivery[3].checked
                if (!listDaysDelivery[3].checked) {
                    iDaysDelivery.iDayMi.tvCDay.setTextColor(Color.BLACK)
                } else {
                    iDaysDelivery.iDayMi.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
            }
            iDaysDelivery.iDayJu.tvCDay.setOnClickListener {
                listDaysDelivery[4].checked = !listDaysDelivery[4].checked
                if (!listDaysDelivery[4].checked) {
                    iDaysDelivery.iDayJu.tvCDay.setTextColor(Color.BLACK)
                } else {
                    iDaysDelivery.iDayJu.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
            }
            iDaysDelivery.iDayVi.tvCDay.setOnClickListener {
                listDaysDelivery[5].checked = !listDaysDelivery[5].checked
                if (!listDaysDelivery[5].checked) {
                    iDaysDelivery.iDayVi.tvCDay.setTextColor(Color.BLACK)
                } else {
                    iDaysDelivery.iDayVi.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
            }
            iDaysDelivery.iDaySa.tvCDay.setOnClickListener {
                listDaysDelivery[6].checked = !listDaysDelivery[6].checked
                if (!listDaysDelivery[6].checked) {
                    iDaysDelivery.iDaySa.tvCDay.setTextColor(Color.BLACK)
                } else {
                    iDaysDelivery.iDaySa.tvCDay.setTextColor(Color.rgb(0, 191, 255))
                }
            }
            //Switch: estatus de despensa
            switchPantries.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    switchPantries.thumbTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.green_retirar)
                    tvEstatusPantries.setText("Habilitado")
                } else {
                    switchPantries.thumbTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.hint_color)
                    tvEstatusPantries.setText("Inhabilitado")
                }
            }
            //Switch: requiere donador
            switchDonorPantries.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    switchDonorPantries.thumbTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.green_retirar)
                } else {
                    lRequisPantries.visibility = View.GONE
                    switchDonorPantries.thumbTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.hint_color)
                }
            }
            //Switch: requiere armado de despensa
            switchArmadoPantries.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    switchArmadoPantries.thumbTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.green_retirar)
                } else {
                    llArmadoPantries.visibility = View.GONE
                    labelDateArmado.visibility = View.GONE
                    llDateArmado.visibility = View.GONE
                    labelDaysArmado.visibility = View.GONE
                    labelTimeArmado.visibility = View.GONE
                    llTimeArmado.visibility = View.GONE
                    switchArmadoPantries.thumbTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.hint_color)
                }
            }
            //Switch: requiere entrega de despensa
            switchDeliveryPantries.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    switchDeliveryPantries.thumbTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.green_retirar)
                } else {
                    llDeliveryPantries.visibility = View.GONE
                    labelDateDelivery.visibility = View.GONE
                    llDateDelivery.visibility = View.GONE
                    labelDaysDelivery.visibility = View.GONE
                    labelTimeDelivery.visibility = View.GONE
                    llTimeDelivery.visibility = View.GONE
                    llAddressDelivery.visibility = View.GONE
                    switchDeliveryPantries.thumbTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.hint_color)
                }
            }

            val adaptador = ArrayAdapter.createFromResource(
                requireContext(), R.array.delegations,
                android.R.layout.simple_spinner_dropdown_item
            )
            spZonePantries.adapter = adaptador
            spZonePantries.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    zona = delegations[position].pos
                    println(zona)
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    lblSeleccion.text = "Sin selección"
                }
            }

            btnGuardarPantry.setOnClickListener { pantryRegister() }
            //Dirección de despensa
            tvAddressPantries.setOnClickListener { showMapPantries() }
            //Dirección entrega de despensa
            tvAddressDelivery.setOnClickListener { showMapDelivery() }
            //Horario recepción de despensa
            tvFirstTimeReception.setOnClickListener { showTimePickerFirstReception() }
            tvEndTimeReception.setOnClickListener { showTimePickerEndReception() }
            //Horario armado de despensa
            tvFirstTimeArmado.setOnClickListener { showTimePickerFirstArmado() }
            tvEndTimeArmado.setOnClickListener { showTimePickerEndArmado() }
            //Horario entrega de despensa
            tvFirstTimeDelivery.setOnClickListener { showTimePickerFirstDelivery() }
            tvEndTimeDelivery.setOnClickListener { showTimePickerEndDelivery() }
            //Fecha recepción de despensa
            tvFirstDateReception.setOnClickListener { showDateFirstReception() }
            tvEndDateReception.setOnClickListener { showDateEndReception() }
            //Fecha armado de despensa
            tvFirstDateArmado.setOnClickListener { showDateFirstArmado() }
            tvEndDateArmado.setOnClickListener { showDateEndArmado() }
            //Fecha entrega de despensa
            tvFirstDateDelivery.setOnClickListener { showDateFirstDelivery() }
            tvEndDateDelivery.setOnClickListener { showDateEndDelivery() }
        }
    }
    fun showMapPantries() {
        if (chechPermissions()) {
            MapsFragment(latitud, longitud) { rlatitude, rlongitude, raddress, municipality ->
                etgetAddressPantries.setText(raddress)
                latitud = rlatitude
                longitud = rlongitude
                println(latitud.toString() + "to" + longitud.toString())
            }.show(childFragmentManager, TAG_LOADER)
        }
    }
    fun showMapDelivery() {
        if (chechPermissions()) {
            MapsFragment(latitud_entrega, longitud_entrega) { rlatitude, rlongitude, raddress, municipality ->
                etgetAddressDelivery.setText(raddress)
                latitud_entrega = rlatitude
                longitud_entrega = rlongitude
                println(latitud_entrega.toString() + "to" + longitud_entrega.toString())
            }.show(childFragmentManager, TAG_LOADER)
        }
    }
    private fun chechPermissions(): Boolean {
        return UtilValidPermission().validListPermissionsAndBuildRequest(
            this@EventPantriesDetailFragment, arrayListOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_LOCATION
        )
    }
    private fun showTimePickerFirstReception() {
        val timePicker =
            TimePickerFragment(isMax = true) { hour, minute -> FirstScheduleReception(hour, minute) }
        timePicker.show(parentFragmentManager, "timePicker")
    }
    private fun FirstScheduleReception(hour: Int, minute: Int) {
        val hourCurrent = if (hour < 10) "0$hour" else hour
        val minuteCurrent = if (minute < 10) "0$minute" else minute
        tvFirstTimeReception.setText("$hourCurrent:$minuteCurrent")
        hourFirst = "$hourCurrent:$minuteCurrent"
        tvFirstTimeReception.error = null
    }
    private fun showTimePickerEndReception() {
        val timePicker =
            TimePickerFragment(isMax = true) { hour, minute -> EndScheduleReception(hour, minute) }
        timePicker.show(parentFragmentManager, "timePicker")
    }
    private fun EndScheduleReception(hour: Int, minute: Int) {
        val hourCurrent = if (hour < 10) "0$hour" else hour
        val minuteCurrent = if (minute < 10) "0$minute" else minute
        tvEndTimeReception.setText("$hourCurrent:$minuteCurrent")
        hourEnd = "$hourCurrent:$minuteCurrent"
        tvEndTimeReception.error = null
    }
    private fun showTimePickerFirstArmado() {
        val timePicker =
            TimePickerFragment(isMax = true) { hour, minute -> FirstScheduleArmado(hour, minute) }
        timePicker.show(parentFragmentManager, "timePicker")
    }
    private fun FirstScheduleArmado(hour: Int, minute: Int) {
        val hourCurrent = if (hour < 10) "0$hour" else hour
        val minuteCurrent = if (minute < 10) "0$minute" else minute
        tvFirstTimeArmado.setText("$hourCurrent:$minuteCurrent")
        hourFirst = "$hourCurrent:$minuteCurrent"
        tvFirstTimeArmado.error = null
    }
    private fun showTimePickerEndArmado() {
        val timePicker =
            TimePickerFragment(isMax = true) { hour, minute -> EndScheduleArmado(hour, minute) }
        timePicker.show(parentFragmentManager, "timePicker")
    }
    private fun EndScheduleArmado(hour: Int, minute: Int) {
        val hourCurrent = if (hour < 10) "0$hour" else hour
        val minuteCurrent = if (minute < 10) "0$minute" else minute
        tvEndTimeArmado.setText("$hourCurrent:$minuteCurrent")
        hourEnd = "$hourCurrent:$minuteCurrent"
        tvEndTimeArmado.error = null
    }
    private fun showTimePickerFirstDelivery() {
        val timePicker =
            TimePickerFragment(isMax = true) { hour, minute -> FirstScheduleDelivery(hour, minute) }
        timePicker.show(parentFragmentManager, "timePicker")
    }
    private fun FirstScheduleDelivery(hour: Int, minute: Int) {
        val hourCurrent = if (hour < 10) "0$hour" else hour
        val minuteCurrent = if (minute < 10) "0$minute" else minute
        tvFirstTimeDelivery.setText("$hourCurrent:$minuteCurrent")
        hourFirst = "$hourCurrent:$minuteCurrent"
        tvFirstTimeDelivery.error = null
    }
    private fun showTimePickerEndDelivery() {
        val timePicker =
            TimePickerFragment(isMax = true) { hour, minute -> EndScheduleDelivery(hour, minute) }
        timePicker.show(parentFragmentManager, "timePicker")
    }
    private fun EndScheduleDelivery(hour: Int, minute: Int) {
        val hourCurrent = if (hour < 10) "0$hour" else hour
        val minuteCurrent = if (minute < 10) "0$minute" else minute
        tvEndTimeDelivery.setText("$hourCurrent:$minuteCurrent")
        hourEnd = "$hourCurrent:$minuteCurrent"
        tvEndTimeDelivery.error = null
    }
    private fun showDateFirstReception() {
        val datePicker =
            DatePickerFragment { day, month, year -> onDateFirstReception(day, month, year) }
        datePicker.show(parentFragmentManager, "datePicker")
    }
    private fun onDateFirstReception(day: Int, month: Int, year: Int) {
        val dayCurrent = if (day < 10) "0$day" else day
        val monthCurrent = month + 1
        val montCurrent = if (month < 9) "0$monthCurrent" else monthCurrent
        tvFirstDateReception.setText("$dayCurrent/$montCurrent/$year")
        dateFirst = "$year-$montCurrent-$dayCurrent"
        tvFirstDateReception.error = null
    }
    private fun showDateEndReception() {
        val datePicker =
            DatePickerFragment { day, month, year -> onDateEndReception(day, month, year) }
        datePicker.show(parentFragmentManager, "datePicker")
    }
    private fun onDateEndReception(day: Int, month: Int, year: Int) {
        val dayCurrent = if (day < 10) "0$day" else day
        val monthCurrent = month + 1
        val montCurrent = if (month < 9) "0$monthCurrent" else monthCurrent
        tvEndDateReception.setText("$dayCurrent/$montCurrent/$year")
        dateEnd = "$year-$montCurrent-$dayCurrent"
        tvEndDateReception.error = null
    }
    private fun showDateFirstArmado() {
        val datePicker =
            DatePickerFragment { day, month, year -> onDateFirstArmado(day, month, year) }
        datePicker.show(parentFragmentManager, "datePicker")
    }
    private fun onDateFirstArmado(day: Int, month: Int, year: Int) {
        val dayCurrent = if (day < 10) "0$day" else day
        val monthCurrent = month + 1
        val montCurrent = if (month < 9) "0$monthCurrent" else monthCurrent
        tvFirstDateArmado.setText("$dayCurrent/$montCurrent/$year")
        dateFirst = "$year-$montCurrent-$dayCurrent"
        tvFirstDateArmado.error = null
    }
    private fun showDateEndArmado() {
        val datePicker =
            DatePickerFragment { day, month, year -> onDateEndArmado(day, month, year) }
        datePicker.show(parentFragmentManager, "datePicker")
    }
    private fun onDateEndArmado(day: Int, month: Int, year: Int) {
        val dayCurrent = if (day < 10) "0$day" else day
        val monthCurrent = month + 1
        val montCurrent = if (month < 9) "0$monthCurrent" else monthCurrent
        tvEndDateArmado.setText("$dayCurrent/$montCurrent/$year")
        dateEnd = "$year-$montCurrent-$dayCurrent"
        tvEndDateArmado.error = null
    }
    private fun showDateFirstDelivery() {
        val datePicker =
            DatePickerFragment { day, month, year -> onDateFirstDelivery(day, month, year) }
        datePicker.show(parentFragmentManager, "datePicker")
    }
    private fun onDateFirstDelivery(day: Int, month: Int, year: Int) {
        val dayCurrent = if (day < 10) "0$day" else day
        val monthCurrent = month + 1
        val montCurrent = if (month < 9) "0$monthCurrent" else monthCurrent
        tvFirstDateDelivery.setText("$dayCurrent/$montCurrent/$year")
        dateFirst = "$year-$montCurrent-$dayCurrent"
        tvFirstDateDelivery.error = null
    }
    private fun showDateEndDelivery() {
        val datePicker =
            DatePickerFragment { day, month, year -> onDateEndDelivery(day, month, year) }
        datePicker.show(parentFragmentManager, "datePicker")
    }
    private fun onDateEndDelivery(day: Int, month: Int, year: Int) {
        val dayCurrent = if (day < 10) "0$day" else day
        val monthCurrent = month + 1
        val montCurrent = if (month < 9) "0$monthCurrent" else monthCurrent
        tvEndDateDelivery.setText("$dayCurrent/$montCurrent/$year")
        dateEnd = "$year-$montCurrent-$dayCurrent"
        tvEndDateDelivery.error = null
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
    private fun pantryRegister() {
        val listSchedules: MutableList<Schedules> =
            mutableListOf(Schedules(listDaysReceived, tvFirstTimeReception.text.toString(), tvEndTimeReception.text.toString()),
                Schedules(listDaysArmed, tvFirstTimeReception.text.toString(), tvEndTimeReception.text.toString()),
                Schedules(listDaysDelivery, tvFirstTimeReception.text.toString(), tvEndTimeReception.text.toString())
            )
        val listReceived: MutableList<Process> =
            mutableListOf(Process(tvFirstDateReception.text.toString(), tvEndDateReception.text.toString(), tvFirstTimeReception.text.toString(), tvEndTimeReception.text.toString()))
        val listArmed: MutableList<Process> =
            mutableListOf(Process(tvFirstDateArmado.text.toString(), tvEndDateArmado.text.toString(), tvFirstTimeArmado.text.toString(), tvEndTimeArmado.text.toString()))
        val listDelivery: MutableList<Process> =
            mutableListOf(Process(tvFirstDateDelivery.text.toString(), tvEndDateDelivery.text.toString(), tvFirstTimeDelivery.text.toString(), tvEndTimeDelivery.text.toString()))

        viewModelEvent.validateFormRegisterPantry(
            listSchedules,
            etResponPantries.text.toString().trim(),
            etEmailPantries.text.toString().replace(" ","").lowercase(),
            etPhonePantries.text.toString().trim(),
            etgetAddressPantries.text.toString().trim(),
            if (longitud == 0.00) 0.00F else longitud.toFloat(),
            if (latitud == 0.00) 0.00F else latitud.toFloat(),
            zona,
            if (switchPantries.isChecked) 1 else 0,
            if (switchArmadoPantries.isChecked) 1 else 0,
            if (switchDeliveryPantries.isChecked) 1 else 0,
            if (switchDonorPantries.isChecked) 1 else 0,
            null,
            //received process
            listReceived,
            //armed process
            listArmed,
            //delivery process
            listDelivery,
            etDescriptionPantries.text.toString(),
            etgetAddressDelivery.text.toString().trim(),
            etRequisPantries.text.toString(),
            if (longitud_entrega == 0.00) 0.00F else longitud_entrega.toFloat(),
            if (latitud_entrega == 0.00) 0.00F else latitud_entrega.toFloat()
        )
    }
}