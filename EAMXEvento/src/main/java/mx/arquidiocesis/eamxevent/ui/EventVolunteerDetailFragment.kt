package mx.arquidiocesis.eamxevent.ui

import android.Manifest
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_event_detail.*
import kotlinx.android.synthetic.main.fragment_event_volunteer_detail.*
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXFieldValidation
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
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
import mx.arquidiocesis.eamxevent.adapter.GuestAdapter
import mx.arquidiocesis.eamxevent.constants.Constants
import mx.arquidiocesis.eamxevent.databinding.FragmentEventVolunteerDetailBinding
import mx.arquidiocesis.eamxevent.model.GuestModel
import mx.arquidiocesis.eamxevent.model.ViewModelEvent
import mx.arquidiocesis.eamxevent.repository.RepositoryEvent

class EventVolunteerDetailFragment : FragmentBase() {

    lateinit var binding: FragmentEventVolunteerDetailBinding
    private val TAG_LOADER: String = "EventFragment"
    private var diner_id: Int = 0
    lateinit var guestAdapter: GuestAdapter
    private var guestList: ArrayList<GuestModel> = arrayListOf()
    private var type = "VOLUNTARIOS"
    private var init = true
    private lateinit var guest: GuestModel
    private var responsable_name: String = ""
    private var direccionComedor: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    private var volunteer_id: Int = 0

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
        fun newInstance(callBack: EAMXHome): EventVolunteerDetailFragment {
            val fragment = EventVolunteerDetailFragment()
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
        savedInstanceState: Bundle?,
    ): View? {
        viewmodel = ViewModelEvent(RepositoryEvent(requireContext()))
        // Inflate thelayout for this fragment
        binding = FragmentEventVolunteerDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "Actividades_Voluntario")
            })
        }
        callBack.showToolbar(true, AppMyConstants.createVoluntario)
        initView()
        initObservers()
        etEmailV.setText(email)
        etPhonev.setText(phone.replace("+52", ""))
        requireArguments().let {
            var id = it.getString("diner_id")
            id?.let { it1 ->
                diner_id = it1.toInt()
                println(diner_id)
                getAllVolunteerbyDiner(diner_id, type)
                //getAllDiners(diner_id)
            }
            var responsable = it.getString("responsable_name")
            responsable?.let { it2 ->
                responsable_name = it2.toString()
                println(responsable_name)
                //etResponsableVoluntario.setText(responsable_name)
            }
            var direccion = it.getString("direccion")
            direccion?.let { it2 ->
                direccionComedor = it2.toString()
                println(direccionComedor)
                //etResponsableVoluntario.setText(responsable_name)
            }
        }
        guestAdapter = GuestAdapter(guestList, rv_invitado)
        rv_invitado.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        rv_invitado.adapter = guestAdapter
    }

    private fun initObservers() {
        viewModelEvent.responseAllVol.observe(viewLifecycleOwner) { item ->
            println("entre")
            hideLoader()
            if (item.size > 0) {
                if (init) {
                    item.forEach { it ->
                        if (it.FIUSERID == userId.toString()) {
                            volunteer_id = it.fIVOLUNTARIOID!!.toInt()
                            etNombreVoluntario.setText(it.fCVOLUNTARIO)
                            etEmailV.setText(it.fCCORREO)
                            etPhonev.setText(it.fCTELEFONO!!.replace("+52", ""))
                            it.fCMULTIUSER!!.forEach {
                                guestAdapter.addItem(it)
                            }
                            btnGuardarVoluntario.setText("Actualizar")
                            return@forEach
                        }
                    }
                }
                init = false
            }
        }

        viewModelEvent.showLoaderView.observe(viewLifecycleOwner) {
            showLoader()
        }
        viewModelEvent.validateForm.observe(viewLifecycleOwner) {
            if (it.containsKey(Constants.KEY_NAME)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_nameV))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_RESPONSABILITY)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_respo))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_PHONE)) {
                if (it[Constants.KEY_PHONE] == Constants.EMPTY_FIELD) {
                    UtilAlert.Builder()
                        .setTitle(getString(R.string.title_dialog_error))
                        .setMessage(getString(R.string.txt_empty_phone))
                        .setIsCancel(false)
                        .build().show(childFragmentManager, tag)
                } else if (it[Constants.KEY_PHONE] == Constants.INVALID_PHONE) {
                    UtilAlert.Builder()
                        .setTitle(getString(R.string.title_dialog_error))
                        .setMessage(getString(R.string.txt_invalid_phone))
                        .setIsCancel(false)
                        .build().show(childFragmentManager, tag)
                }
            }
            hideLoader()
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
        binding.apply {
            etNombreVoluntario.hint = "Nombre y apellidos"
            etgetAddressVoluntario.hint =
                "Actualiza tu ubicación en el mapa para obtener la dirección."

            etNombreVoluntario.addTextChangedListener {
                if (etNombreVoluntario.text.toString().isNotEmpty()) {
                    tilNombreVoluntario.error = null
                    enableIconStart(tilNombreVoluntario, true)
                } else {
                    tilNombreVoluntario.error = getString(R.string.enter_your_name)
                    enableIconStart(tilNombreVoluntario, null)
                }
            }

            etResponsableVoluntario.addTextChangedListener {
                if (etResponsableVoluntario.text.toString().isNotEmpty()) {
                    tilResponsableVoluntario.error = null
                    enableIconStart(tilResponsableVoluntario, true)
                } else {
                    tilResponsableVoluntario.error = getString(R.string.enter_your_vol)
                    enableIconStart(tilResponsableVoluntario, null)
                }
            }

            etPhonev.addTextChangedListener {
                val validatePhone = etPhonev.text.toString().validNumberPhoneVoluntario()
                enableIconStart(
                    tilPhonev,
                    validatePhone
                )
                if (etPhonev.text.toString().isEmpty()) {
                    enableIconStart(tilPhonev, null)
                    tilPhonev.isEmpty()
                    tilPhonev.error = getString(R.string.min_phone)
                } else {
                    if (EAMXFieldValidation.validateNumberPhone(etPhonev.text.toString())
                        && EAMXFieldValidation.validateNumberLength(etPhonev.text.toString())
                    ) {
                        tilPhonev.error = null
                    }
                    if (!EAMXFieldValidation.validateNumberPhone(etPhonev.text.toString())) {
                        tilPhonev.error = getString(R.string.wrong_phone_number)
                    }
                    if (!EAMXFieldValidation.validateNumberLength(etPhonev.text.toString())) {
                        tilPhonev.error = getString(R.string.min_phone)
                    }
                }
            }

            etEmailV.addTextChangedListener {
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

            etPhoneInvitado.addTextChangedListener {
                val validatePhone = etPhoneInvitado.text.toString().validNumberPhoneVoluntario()
                enableIconStart(
                    tilPhoneInvitado,
                    validatePhone
                )
                if (etPhoneInvitado.text.toString().isEmpty()) {
                    enableIconStart(tilPhoneInvitado, null)
                    tilPhoneInvitado.isEmpty()
                    tilPhoneInvitado.error = getString(R.string.min_phone)
                } else {
                    if (EAMXFieldValidation.validateNumberPhone(etPhoneInvitado.text.toString())
                        && EAMXFieldValidation.validateNumberLength(etPhoneInvitado.text.toString())
                    ) {
                        tilPhoneInvitado.error = null
                    }
                    if (!EAMXFieldValidation.validateNumberPhone(etPhoneInvitado.text.toString())) {
                        tilPhoneInvitado.error = getString(R.string.wrong_phone_number)
                    }
                    if (!EAMXFieldValidation.validateNumberLength(etPhoneInvitado.text.toString())) {
                        tilPhoneInvitado.error = getString(R.string.min_phone)
                    }
                }
            }
            etInvitado.addTextChangedListener {
                if (etInvitado.text.toString().isNotEmpty()) {
                    tilInvitado.error = null
                    enableIconStart(tilInvitado, true)
                } else {
                    tilInvitado.error = getString(R.string.enter_your_name)
                    enableIconStart(tilInvitado, null)
                }
            }
        }
        initButtons()
    }

    private fun initButtons() {
        binding.apply {
            btnAgregar.setOnClickListener {
                if (etInvitado.text.toString().isEmpty()) {
                    UtilAlert.Builder()
                        .setTitle(getString(R.string.title_dialog_error))
                        .setMessage(getString(R.string.txt_empty_nameI))
                        .setIsCancel(false)
                        .build().show(childFragmentManager, tag)
                    return@setOnClickListener
                } else if (etPhoneInvitado.text.toString().isEmpty()) {
                    UtilAlert.Builder()
                        .setTitle(getString(R.string.title_dialog_error))
                        .setMessage(getString(R.string.txt_empty_phoneI))
                        .setIsCancel(false)
                        .build().show(childFragmentManager, tag)
                    return@setOnClickListener
                } else if (!EAMXFieldValidation.validateNumberLength(etPhoneInvitado.text.toString())) {
                    UtilAlert.Builder()
                        .setTitle(getString(R.string.title_dialog_error))
                        .setMessage(getString(R.string.txt_invalid_phone))
                        .setIsCancel(false)
                        .build().show(childFragmentManager, tag)
                    return@setOnClickListener
                }
                guestAdapter.addItem(
                    GuestModel(
                        etInvitado.text.toString(),
                        "+52${etPhoneInvitado.text.toString()}"
                    )
                )
                etInvitado.setText("")
                tilInvitado.error = null
                etPhoneInvitado.setText("")
                etPhoneInvitado.error = null
            }

            val allowedChars = "0123456789"
            etPhonev.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_PLUS && event.action == KeyEvent.ACTION_UP) {
                    val currentText = etPhonev.text.toString()
                    val selectionStart = etPhonev.selectionStart
                    val selectionEnd = etPhonev.selectionEnd

                    val newText = StringBuilder(currentText).apply {
                        insert(selectionStart, "+")
                    }.toString()

                    if (newText.all { allowedChars.contains(it) }) {
                        etPhonev.setText(newText)
                        etPhonev.setSelection(selectionStart + 1)
                    }

                    true
                } else {
                    false
                }
            }

            etPhoneInvitado.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_PLUS && event.action == KeyEvent.ACTION_UP) {
                    val currentText = etPhoneInvitado.text.toString()
                    val selectionStart = etPhoneInvitado.selectionStart
                    val selectionEnd = etPhoneInvitado.selectionEnd

                    val newText = StringBuilder(currentText).apply {
                        insert(selectionStart, "+")
                    }.toString()

                    if (newText.all { allowedChars.contains(it) }) {
                        etPhoneInvitado.setText(newText)
                        etPhoneInvitado.setSelection(selectionStart + 1)
                    }

                    true
                } else {
                    false
                }
            }

            btnGuardarVoluntario.setOnClickListener { volunteerRegister() }
            //btnCancelVoluntario.setOnClickListener{activity?.onBackPressedDispatcher?.onBackPressed() }
            tvAddressVoluntario.setOnClickListener { showMap() }
        }
    }

    fun showMap() {
        if (chechPermissions()) {
            MapsFragment { rlatitude, rlongitude, raddress, municipality ->
                etgetAddress.setText(raddress)
            }.show(childFragmentManager, TAG_LOADER)
        }
    }

    private fun chechPermissions(): Boolean {
        return UtilValidPermission().validListPermissionsAndBuildRequest(
            this@EventVolunteerDetailFragment, arrayListOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_LOCATION
        )
    }

    private fun String.validNumberPhoneVoluntario() =
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

        viewModelEvent.validateFormRegisterVolunteer(
            responsable_name,
            direccionComedor,
            diner_id.toString(),
            etNombreVoluntario.text.toString(),
            etPhonev.text.toString().replace(" ",""),
            guestAdapter.guestList,
            etEmailV.text.toString().replace(" ","").lowercase(),
            volunteer_id
        )
    }

    fun getAllVolunteerbyDiner(id: Int, type: String) {
        showLoader()
        viewModelEvent.requestAllVolunteerbyDiner(id, type)
    }
}