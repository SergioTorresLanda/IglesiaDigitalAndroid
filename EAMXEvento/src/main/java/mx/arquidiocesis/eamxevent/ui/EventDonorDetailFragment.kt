package mx.arquidiocesis.eamxevent.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat.*
import androidx.core.view.isEmpty
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_event_detail.*
import kotlinx.android.synthetic.main.fragment_event_donor_detail.*
import kotlinx.android.synthetic.main.fragment_event_donor_detail.btnGuardar
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXFieldValidation
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxevent.R
import mx.arquidiocesis.eamxevent.constants.Constants
import mx.arquidiocesis.eamxevent.model.*
import mx.arquidiocesis.eamxevent.repository.RepositoryEvent
import mx.arquidiocesis.eamxcommonutils.util.*
import mx.arquidiocesis.eamxevent.databinding.FragmentEventDonorDetailBinding
import mx.arquidiocesis.eamxevent.model.enum.Type_donation

class EventDonorDetailFragment : FragmentBase() {
    lateinit var binding: FragmentEventDonorDetailBinding
    private val TAG_LOADER: String = "EventFragment"
    private var diner_id: Int = 0
    private var donor_id: Int = 0
    private var donacion: String = ""
    private var init = true
    private var type = "DONADORES"
    private var type_don: Array<Type_donation> = Type_donation.values()
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
        fun newInstance(callBack: EAMXHome): EventDonorDetailFragment {
            val fragment = EventDonorDetailFragment()
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
        binding = FragmentEventDonorDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "Actividades_Donar")
            })
        }
        callBack.showToolbar(true, AppMyConstants.createDonador)
        initView()
        initObservers()
        etEmailDonador.setText(email)
        etPhoneDonador.setText(phone)
        requireArguments().let {
            var id = it.getString("diner_id")
            id?.let { it1 ->
                diner_id = it1.toInt()
                getAllDonorbyDiner(diner_id, type)
            }
        }
    }

    private fun initObservers() {
        viewModelEvent.responseAllDon.observe(viewLifecycleOwner) { item ->
            println("entre")
            hideLoader()
            if (item.size > 0) {
                if (init) {
                    item.forEach {
                        if (it.fIUSERID == userId.toString()) {
                            donor_id = it.fIDONANTEID!!.toInt()
                            println(donor_id)
                            etNombreD.setText(it.fCNOMBRE)
                            etComentarios.setText(it.fCCOMENTARIOS)
                            type_don.forEach { it1 ->
                                if (it1.pos.toString() == it.fCTIPODONA) {
                                    spTipoDonacion.setSelection(it1.ordinal)
                                    return@forEach
                                }
                            }
                            etEmailDonador.setText(it.fCCORREO)
                            etPhoneDonador.setText(it.fCTELEFONO)
                            btnGuardar.setText("Actualizar")
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
                    .setMessage(getString(R.string.txt_empty_nameD))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_COMMENT)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_com))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_EMAIL)) {
                if (it[Constants.KEY_EMAIL] == Constants.INVALID_EMAIL) {
                    etEmailDonador.error =
                        getString(R.string.txt_invalidate_email)
                }
            } else if (it.containsKey(Constants.KEY_PHONE)) {
                if (it[Constants.KEY_PHONE] == Constants.EMPTY_FIELD) {
                    UtilAlert.Builder()
                        .setTitle(getString(R.string.title_dialog_error))
                        .setMessage(getString(R.string.txt_empty_phone))
                        .setIsCancel(false)
                        .build().show(childFragmentManager, tag)
                } else if (it[Constants.KEY_PHONE] == Constants.INVALID_PHONE){
                    UtilAlert.Builder()
                        .setTitle(getString(R.string.title_dialog_error))
                        .setMessage(getString(R.string.txt_invalid_phone_don))
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
            etNombreD.hint = "Nombre y apellido"
            //etComentarios.hint = "Escribe tus comentarios"

            etNombreD.addTextChangedListener {
                if (etNombreD.text.toString().isNotEmpty()) {
                    tilNombreD.error = null
                    enableIconStart(tilNombreD, true)
                } else {
                    tilNombreD.error = getString(R.string.enter_your_don)
                    enableIconStart(tilNombreD, null)
                }
            }

            etComentarios.addTextChangedListener {
                var count = etComentarios.text.toString().length
                tvComentariosConteo.setText("$count/250")
                if (etComentarios.text.toString().isNotEmpty()) {
                    tilComentarios.error = null
                    enableIconStart(tilComentarios, true)
                } else {
                    tilComentarios.error = getString(R.string.enter_your_com)
                    enableIconStart(tilComentarios, null)
                }
            }
            etPhoneDonador.addTextChangedListener {
                val validatePhone = etPhoneDonador.text.toString().validNumberPhone()
                enableIconStart(
                    tilPhoneDonador,
                    validatePhone
                )
                if (etPhoneDonador.text.toString().isEmpty()) {
                    enableIconStart(tilPhoneDonador, null)
                    tilPhoneDonador.isEmpty()
                    tilPhoneDonador.error = getString(R.string.min_phone_don)
                } else {
                    if (EAMXFieldValidation.validateNumberPhone(etPhoneDonador.text.toString()) && EAMXFieldValidation.validateNumberLengthCD(
                            etPhoneDonador.text.toString()
                        )
                    ) {
                        tilPhoneDonador.error = null
                    }
                    if (!EAMXFieldValidation.validateNumberPhone(etPhoneDonador.text.toString())) {
                        tilPhoneDonador.error = getString(R.string.wrong_phone_number)
                    }
                    if (!EAMXFieldValidation.validateNumberLengthCD(etPhoneDonador.text.toString())) {
                        tilPhoneDonador.error = getString(R.string.min_phone_don)
                    }
                }
            }

            etEmailDonador.addTextChangedListener {
                val text = it?.toString()
                text?.let { emailTxt ->
                    enableIconStart(
                        tilEmailDonador,
                        Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()
                    )
                    tilEmailDonador.error = null
                    EAMXFieldValidation.validateEmail(emailTxt, tilEmailDonador)
                    if (emailTxt.isEmpty()) {
                        enableIconStart(tilEmailDonador, null)
                    }
                }
            }
        }
        initButtons()
    }

    private fun initButtons() {
        binding.apply {
            val adaptador = ArrayAdapter.createFromResource(
                requireContext(), R.array.typedonations,
                android.R.layout.simple_spinner_dropdown_item
            )

            spTipoDonacion.adapter = adaptador
            spTipoDonacion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    donacion =
                        spTipoDonacion.getItemAtPosition(spTipoDonacion.selectedItemPosition) as String
                    println(donacion)
                    //donacion = textoSeleccionado
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            val allowedChars = "0123456789+"
            etPhoneDonador.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_PLUS && event.action == KeyEvent.ACTION_UP) {
                    val currentText = etPhoneDonador.text.toString()
                    val selectionStart = etPhoneDonador.selectionStart
                    val selectionEnd = etPhoneDonador.selectionEnd

                    val newText = StringBuilder(currentText).apply {
                        insert(selectionStart, "+")
                    }.toString()

                    if (newText.all { allowedChars.contains(it) }) {
                        etPhoneDonador.setText(newText)
                        etPhoneDonador.setSelection(selectionStart + 1)
                    }

                    true
                } else {
                    false
                }
            }

            btnGuardar.setOnClickListener { donorRegister() }
            //btnCancel.setOnClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
        }
    }


    private fun String.validNumberPhone() =
        EAMXFieldValidation.validateNumberPhone(this) &&
                this.isNotEmpty() &&
                EAMXFieldValidation.validateNumberLengthCD(this)

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

    private fun donorRegister() {
        viewModelEvent.validateFormRegisterDonor(
            etNombreD.text.toString(),
            etComentarios.text.toString(),
            diner_id,
            etEmailDonador.text.toString(),
            etPhoneDonador.text.toString(),
            "datos bancarios",
            donacion,
            donor_id
        )
    }

    fun getAllDonorbyDiner(id: Int, type: String) {
        showLoader()
        viewModelEvent.requestAllDonorbyDiner(id, type)
    }
}
