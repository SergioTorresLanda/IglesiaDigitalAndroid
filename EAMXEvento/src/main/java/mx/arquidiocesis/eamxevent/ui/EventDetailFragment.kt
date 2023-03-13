package mx.arquidiocesis.eamxevent.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxevent.R
import mx.arquidiocesis.eamxevent.databinding.FragmentEventDetailBinding
import android.content.res.ColorStateList
import android.util.Patterns
import androidx.core.content.ContextCompat.*
import androidx.core.view.isEmpty
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_event_detail.*
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXFieldValidation
import mx.arquidiocesis.eamxcommonutils.base.DatePickerFragment
import mx.arquidiocesis.eamxcommonutils.base.TimePickerFragment

class EventDetailFragment : FragmentBase() {

    lateinit var binding: FragmentEventDetailBinding
    private var hourFirst = ""
    private var hourEnd = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance(callBack: EAMXHome): EventDetailFragment {
            var fragment = EventDetailFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callBack.showToolbar(true, AppMyConstants.detailEvento)

        binding.apply {
            iDays.iDayMa.tvCDay.setText("Lu")
            iDays.iDayMa.tvCDay.setText("Ma")
            iDays.iDayMi.tvCDay.setText("Mi")
            iDays.iDayJu.tvCDay.setText("Ju")
            iDays.iDayVi.tvCDay.setText("Vi")
            iDays.iDaySa.tvCDay.setText("Sa")
            iDays.iDayDo.tvCDay.setText("Do")


            binding.iDays.iDayLu.tvCDay.setOnClickListener {
                //binding.iDays.iDayLu.tvCDay.setBackgroundColor(Color.BLUE)
                binding.iDays.iDayLu.tvCDay.setTextColor(Color.rgb(0,191,255))
            }
            binding.iDays.iDayMa.tvCDay.setOnClickListener {
                //binding.iDays.iDayLu.tvCDay.setBackgroundColor(Color.BLUE)
                binding.iDays.iDayMa.tvCDay.setTextColor(Color.rgb(0,191,255))
            }
            binding.iDays.iDayMi.tvCDay.setOnClickListener {
                //binding.iDays.iDayLu.tvCDay.setBackgroundColor(Color.BLUE)
                binding.iDays.iDayMi.tvCDay.setTextColor(Color.rgb(0,191,255))
            }
            binding.iDays.iDayJu.tvCDay.setOnClickListener {
                //binding.iDays.iDayLu.tvCDay.setBackgroundColor(Color.BLUE)
                binding.iDays.iDayJu.tvCDay.setTextColor(Color.rgb(0,191,255))
            }
            binding.iDays.iDayVi.tvCDay.setOnClickListener {
                //binding.iDays.iDayLu.tvCDay.setBackgroundColor(Color.BLUE)
                binding.iDays.iDayVi.tvCDay.setTextColor(Color.rgb(0,191,255))
            }
            binding.iDays.iDaySa.tvCDay.setOnClickListener {
                //binding.iDays.iDayLu.tvCDay.setBackgroundColor(Color.BLUE)
                binding.iDays.iDaySa.tvCDay.setTextColor(Color.rgb(0,191,255))
            }
            binding.iDays.iDayDo.tvCDay.setOnClickListener {
                //binding.iDays.iDayLu.tvCDay.setBackgroundColor(Color.BLUE)
                binding.iDays.iDayDo.tvCDay.setTextColor(Color.rgb(0,191,255))
            }
            switch1.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked){
                    lMonto.visibility = View.VISIBLE
                    switch1.thumbTintList = getColorStateList(requireContext(), R.color.green_retirar)
                }else{
                    lMonto.visibility = View.GONE
                    switch1.thumbTintList = getColorStateList(requireContext(),R.color.hint_color)
                }
            }
            switch2.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked){
                    switch2.thumbTintList = getColorStateList(requireContext(), R.color.green_retirar)
                }else{
                    switch2.thumbTintList = getColorStateList(requireContext(),R.color.hint_color)
                }
            }
            switch3.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked){
                    switch3.thumbTintList = getColorStateList(requireContext(), R.color.green_retirar)
                }else{
                    switch3.thumbTintList = getColorStateList(requireContext(),R.color.hint_color)
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
                val validatePhone =  etNumberPhone.text.toString().validNumberPhoneContent()
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

            btnGuardar.setOnClickListener{

            }

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
                input.endIconDrawable = getDrawable(requireContext(), R.drawable.ic_baseline_check_24)
                input.setEndIconTintList(ColorStateList.valueOf(getColor(requireContext(), R.color.success)))
            }
            false -> {
                input.endIconDrawable = getDrawable(requireContext(), R.drawable.ic_check_error)
                input.setEndIconTintList(ColorStateList.valueOf(getColor(requireContext(), R.color.error)))
            }
            null -> {
                input.endIconDrawable = null
            }
        }
    }
}