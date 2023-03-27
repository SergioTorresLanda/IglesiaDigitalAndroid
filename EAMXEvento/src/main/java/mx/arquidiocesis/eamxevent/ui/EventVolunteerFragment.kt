package mx.arquidiocesis.eamxevent.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_event_detail.*
import kotlinx.android.synthetic.main.fragment_event_volunteer.*
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXFieldValidation
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxevent.R
import mx.arquidiocesis.eamxevent.constants.Constants
import mx.arquidiocesis.eamxevent.databinding.FragmentEventVolunteerBinding
import mx.arquidiocesis.eamxevent.model.ViewModelEvent
import mx.arquidiocesis.eamxevent.repository.RepositoryEvent

class EventVolunteerFragment : FragmentBase() {

    lateinit var binding: FragmentEventVolunteerBinding
    private val TAG_LOADER: String = "EventFragment"
    private var id_diner: Int = 0

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
        fun newInstance(callBack: EAMXHome): EventVolunteerFragment {
            val fragment = EventVolunteerFragment()
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
        binding = FragmentEventVolunteerBinding.inflate(inflater, container, false)
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
            var id = it.getString("id_diner")
            id?.let { it1 ->
                id_diner = it1.toInt()
                println(id_diner)
                //getAllDiners(diner_id)
            }
        }
    }

    private fun initObservers() {
        viewmodel.responseAllDon.observe(viewLifecycleOwner) { item ->
            hideLoader()
            if (item.size > 0) {
                item.forEach {
                    if (!it.fIUSERID.isNullOrEmpty()) {
                        etNombreVoluntario.setText(it.fCNOMBRE)
                        etEmailV.setText(it.fCCORREO)
                            etPhonev.setText(it.fCTELEFONO!!.replace("+52", ""))
                            return@forEach
                        }
                    }
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
            } else if (it.containsKey(Constants.KEY_TYPEDON)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_type))
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
            etNombreVoluntario.hint = "Nombre del voluntario"

            etNombreVoluntario.addTextChangedListener {
                if (etNombreVoluntario.text.toString().isNotEmpty()) {
                    tilNombreVoluntario.error = null
                    enableIconStart(tilNombreVoluntario, true)
                } else {
                    tilNombreVoluntario.error = getString(R.string.enter_diner_name)
                    enableIconStart(tilNombreVoluntario, null)
                }
            }

            etPhonev.addTextChangedListener {
                val validatePhone = etPhonev.text.toString().validNumberPhoneContent()
                enableIconStart(
                    tilPhonev,
                    validatePhone
                )
                if (etPhonev.text.toString().isEmpty()) {
                    enableIconStart(tilPhonev, null)
                    tilPhonev.isEmpty()
                    tilPhonev.error = getString(R.string.min_phone)
                } else {
                    if (EAMXFieldValidation.validateNumberPhone(etPhonev.text.toString()) && EAMXFieldValidation.validateNumberLength(
                            etPhonev.text.toString()
                        )
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
        }
        initButtons()
    }

    private fun initButtons() {
        binding.apply {


            btnGuardar.setOnClickListener { volunteerRegister() }
        }
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
        viewModelEvent.validateFormRegisterVolunteer(
            etNombreVoluntario.text.toString(),
            etResponsableVoluntario.text.toString(),
            id_diner.toString(),
            etEmailV.text.toString(),
            etPhonev.text.toString(),
            etgetAddressVoluntario.text.toString(),
            arrayListOf()
        )
    }

    fun getAllDiners(id: Int) {
        showLoader()
        viewmodel.requestAllDiner(id)
    }
}