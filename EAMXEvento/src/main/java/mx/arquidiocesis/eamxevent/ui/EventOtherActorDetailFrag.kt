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
import kotlinx.android.synthetic.main.fragment_event_actor_detail.*
import kotlinx.android.synthetic.main.fragment_event_other_detail.etResponsable
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
import mx.arquidiocesis.eamxevent.databinding.FragmentEventActorDetailBinding
import mx.arquidiocesis.eamxevent.model.enum.Type_donation

class EventOtherActorDetailFrag : FragmentBase() {
    lateinit var binding: FragmentEventActorDetailBinding
    private val TAG_LOADER: String = "OtherActorFragment"
    private var eventId: Int = 0
    private var tipoActor: Int = 0
    private var actor_id: Int = 0
    private var donacion: String = ""
    private var init = true
    private var type_don: Array<Type_donation> = Type_donation.values()
    val email = eamxcu_preferences.getData(EAMXEnumUser.USER_EMAIL.name, EAMXTypeObject.STRING_OBJECT) as String
    val userId = eamxcu_preferences.getData(EAMXEnumUser.USER_ID.name, EAMXTypeObject.INT_OBJECT) as Int
    val phone = eamxcu_preferences.getData(EAMXEnumUser.USER_PHONE.name, EAMXTypeObject.STRING_OBJECT) as String
    val namex = eamxcu_preferences.getData(EAMXEnumUser.USER_NAME.name, EAMXTypeObject.STRING_OBJECT) as String

    lateinit var viewmodel: ViewModelEvent

    companion object {
        fun newInstance(callBack: EAMXHome): EventOtherActorDetailFrag {
            val fragment = EventOtherActorDetailFrag()
            fragment.callBack = callBack
            return fragment
        }
    }

    private val viewModelEvent: ViewModelEvent by lazy {
        getViewModel { ViewModelEvent(RepositoryEvent(requireContext())) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        viewmodel = ViewModelEvent(RepositoryEvent(requireContext()))
        // Inflate thelayout for this fragment
        binding = FragmentEventActorDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "Actividades_CrearActor")
            })
        }

        //callBack.showToolbar(true, AppMyConstants.createDonador)
        initView()
        initObservers()
        etNombreDD.setText(namex)
        etEmailDonador.setText(email)
        etPhoneDonador.setText(phone.replace("+52", ""))
        etNombreDD.isEnabled=false
        etEmailDonador.isEnabled=false
        etPhoneDonador.isEnabled=false

        requireArguments().let {
            val id = it.getInt("eventoId")
            id.let { it1 ->
                eventId = it1
                getAllActors()
                //getAllActorsByEvent(eventId, tipoActor)
            }
            val tA = it.getInt("tipoActor")
            tA.let { it2 ->
                tipoActor = it2
            }
            when (tipoActor){
                1 -> {
                    callBack.showToolbar(true, AppMyConstants.createDonador)
                    tvNombreD.text="Nombre del donador"
                    labelComentarios.text="¿Qué vas a donar?"
                }
                2 -> {
                    callBack.showToolbar(true, AppMyConstants.createVoluntario)
                    tvNombreD.text="Nombre del voluntario"
                    labelComentarios.text="¿Cómo quieres apoyar?"
                }

                3 -> {
                    callBack.showToolbar(true, AppMyConstants.createParticipante)
                    tvNombreD.text="Nombre del participante"
                    labelComentarios.text="Comentarios y/o dudas"
                }
                else -> println("TIPO actor NULL")
            }
        }
    }

    private fun initObservers() {
        viewModelEvent.responseAllActors.observe(viewLifecycleOwner) { item ->
            hideLoader()
            if (item.isNotEmpty()) {
                println("item isnotempty")
                if (init) {
                    println("init")
                    item.forEach {
                        if (it.user_id == userId) {//es el usuario
                            println("userId equal")
                            if (eventId == it.evento_id){//es la actividad
                                println("::: eventId equal ")
                                println(eventId)
                                if (tipoActor == it.tipo_actor){//es el tipo de participacion
                                    println("::: tipoactor equal ")
                                    actor_id = it.actor_id!!
                                //etNombreDD.setText(it.nombre)
                                etComentarios.setText(it.comentarios)
                                //etEmailDonador.setText(it.correo)
                                //etPhoneDonador.setText(it.telefono!!.replace("+52", ""))
                                btnGuardar.text = "Actualizar"
                                return@forEach
                                }else{
                                    println("::: tipoactor no equal ")
                                    println(tipoActor)
                                    println(it.tipo_actor)
                                }
                            }else{
                                println("::: eventId no equal")
                                println(eventId)
                                println(it.evento_id)
                            }
                        }else{
                            println("::: userId no equal")
                            println(userId)
                            println(it.user_id)
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
                } else if (it[Constants.KEY_PHONE] == Constants.INVALID_PHONE) {
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
            etNombreDD.hint = "Nombre y apellido"
            //etComentarios.hint = "Escribe tus comentarios"
            etNombreDD.addTextChangedListener {
                if (etNombreDD.text.toString().isNotEmpty()) {
                    tilNombreD.error = null
                    enableIconStart(tilNombreD, true)
                } else {
                    tilNombreD.error = getString(R.string.enter_your_name2)
                    enableIconStart(tilNombreD, null)
                }
            }

            etComentarios.addTextChangedListener {
                val count = etComentarios.text.toString().length
                tvComentariosConteo.text = "$count/250"
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
                    tilPhoneDonador.error = getString(R.string.min_phone)
                } else {
                    if (EAMXFieldValidation.validateNumberPhone(etPhoneDonador.text.toString()) && EAMXFieldValidation.validateNumberLength(
                            etPhoneDonador.text.toString()
                        )
                    ) {
                        tilPhoneDonador.error = null
                    }
                    if (!EAMXFieldValidation.validateNumberPhone(etPhoneDonador.text.toString())) {
                        tilPhoneDonador.error = getString(R.string.wrong_phone_number)
                    }
                    if (!EAMXFieldValidation.validateNumberLength(etPhoneDonador.text.toString())) {
                        tilPhoneDonador.error = getString(R.string.min_phone)
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

            val allowedChars = "0123456789"
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

            btnGuardar.setOnClickListener { actorRegister() }
            //btnCancel.setOnClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
        }
    }

    private fun String.validNumberPhone() =
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

    private fun actorRegister() {
        viewModelEvent.postOthersActors(OtherActor(
            nombre = etNombreDD.text.toString(),
            correo = etEmailDonador.text.toString().replace(" ","").lowercase(),
            telefono = etPhoneDonador.text.toString().replace(" ",""),
            status = 1,
            comentarios = etComentarios.text.toString(),
            user_id = userId,
            evento_id = eventId,
            tipo_actor = tipoActor,
            actor_id = actor_id)
        )
    }

    fun getAllActors(){
        showLoader()
        viewModelEvent.requestAllActors()
    }
}
