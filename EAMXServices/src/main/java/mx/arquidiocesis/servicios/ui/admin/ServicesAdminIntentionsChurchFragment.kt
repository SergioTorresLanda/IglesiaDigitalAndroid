package mx.arquidiocesis.servicios.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.DatePickerFragment
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXMessageError
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.*
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.servicios.R
import mx.arquidiocesis.servicios.adapter.InteractionAdapter
import mx.arquidiocesis.servicios.databinding.FragmentServicesAdminIntentionsChurchBinding
import mx.arquidiocesis.servicios.model.admin.view.AdminIntentionGeneralModel
import mx.arquidiocesis.servicios.repository.AdminServicesIntentionsRepository
import mx.arquidiocesis.servicios.ui.admin.ServicesAdminIntentionDetailFragment.Companion.INTERACTION_DATE
import mx.arquidiocesis.servicios.ui.admin.ServicesAdminIntentionDetailFragment.Companion.INTERACTION_HOUR
import mx.arquidiocesis.servicios.ui.admin.ServicesAdminIntentionDetailFragment.Companion.INTERACTION_TYPE
import mx.arquidiocesis.servicios.ui.admin.ServicesAdminIntentionDetailFragment.Companion.TYPE_CHURCH
import mx.arquidiocesis.servicios.viewModel.AdminServicesIntentionsViewModel
import java.util.*

class ServicesAdminIntentionsChurchFragment : FragmentBase() {

    companion object {
        @JvmStatic
        fun newInstance(callBack: EAMXHome) =
            ServicesAdminIntentionsChurchFragment().apply {
                this.callBack = callBack
            }
    }

    private lateinit var dateString: String
    private lateinit var binding: FragmentServicesAdminIntentionsChurchBinding
    val viewModel: AdminServicesIntentionsViewModel by lazy {
        getViewModel {
            AdminServicesIntentionsViewModel(AdminServicesIntentionsRepository(requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServicesAdminIntentionsChurchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObservers()
        initData()
    }

    private fun initView() {
        callBack.showToolbar(true, AppMyConstants.intentions)
        binding.apply {
            ivFlecha.setOnClickListener { v -> launchCalendar(v) }
            tvDate.setOnClickListener { v -> launchCalendar(v) }
        }
    }

    private fun launchCalendar(v: View?) {
        val datePicker = DatePickerFragment { day, month, year ->
            onDateSelected(day, month, year)
        }
        datePicker.show(childFragmentManager, "datePicker")
    }

    private fun onDateSelected(day: Int, month: Int, year: Int) {
        dateString = Calendar.getInstance().dateInFormatDayNameDDMonthName(day, month, year)
        binding.apply {
            tvDate.text = dateString
        }
        showLoader()
        viewModel.getIntentions(
            Calendar.getInstance().insertDateMiddleDashAndYyyMMddCurrent(day, month, year)
        )
    }

    private fun initObservers() {
        viewModel.responseIntentionsP.observe(viewLifecycleOwner, handleIntentions())
        viewModel.errorP.observe(viewLifecycleOwner, handleError())
    }

    private fun initData() {
        dateString = Calendar.getInstance().dateInFormatDayNameDDMonthNameCurrent()
        binding.tvDate.text = dateString
        showLoader()
        viewModel.getIntentions(Calendar.getInstance().dateMiddleDashAndYyyMMddCurrent())
    }

    private fun handleIntentions(): (List<AdminIntentionGeneralModel>) -> Unit = {
        hideLoader()
        val adapterInteraction = InteractionAdapter(it) {
            val bundle = Bundle()
            bundle.apply {
                putString(INTERACTION_HOUR, it.hour)
                putString(INTERACTION_DATE, it.date)
                putString(INTERACTION_TYPE, TYPE_CHURCH)
            }
            NavigationFragment.Builder()
                .setActivity(requireActivity())
                .setView(requireView().parent as ViewGroup)
                .setBundle(bundle)
                .setFragment(ServicesAdminIntentionDetailFragment.newInstance(callBack))
                .setAllowStack(true)
                .build().nextWithReplace()
        }

        binding.apply {
            rvIntentions.apply {
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                adapter = adapterInteraction
            }
        }
    }

    private fun handleError(): (EAMXMessageError) -> Unit = { error ->
        hideLoader()
        UtilAlert.Builder()
            .setTitle(getString(R.string.title_dialog_warning))
            .setMessage(error.message ?: "")
            .setIsCancel(false)
            .setListener {
                if (error.back) {
                    activity?.onBackPressed()
                }
            }
            .build()
            .show(childFragmentManager, "")
    }
}