package mx.arquidiocesis.servicios.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXMessageError
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.servicios.R
import mx.arquidiocesis.servicios.adapter.AdapterCustomSpinner
import mx.arquidiocesis.servicios.adapter.AdminHistoryAdapter
import mx.arquidiocesis.servicios.databinding.FragmentServicesAdminHistoryBinding
import mx.arquidiocesis.servicios.model.admin.view.AdminHistoryModel
import mx.arquidiocesis.servicios.model.StatusServices
import mx.arquidiocesis.servicios.model.admin.view.AdminServiceModel
import mx.arquidiocesis.servicios.repository.AdminServicesRepository
import mx.arquidiocesis.servicios.viewModel.AdminServicesViewModel

class FragmentServicesAdminHistory : FragmentBase() {

    companion object {
        @JvmStatic
        fun newInstance(callBack: EAMXHome, listener: (AdminServiceModel) -> Unit) =
            FragmentServicesAdminHistory().apply {
                this.callBack = callBack
                this.listener = listener
            }
    }

    private lateinit var listener: (AdminServiceModel) -> Unit
    private lateinit var binding: FragmentServicesAdminHistoryBinding
    private lateinit var adapterHistory: AdminHistoryAdapter

    val viewModel: AdminServicesViewModel by lazy {
        getViewModel {
            AdminServicesViewModel(AdminServicesRepository(requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServicesAdminHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObservers()
    }

    private fun initObservers() {
        viewModel.responseStatusP.observe(viewLifecycleOwner, loadStatus())
        viewModel.responseHistoryP.observe(viewLifecycleOwner, loadInfoServices())
        viewModel.responseDeleteServiceP.observe(viewLifecycleOwner, handleDeleteServices())
        viewModel.errorP.observe(viewLifecycleOwner, error())
    }

    private fun initView() {
        showLoader()
        viewModel.getStatusServices()
        viewModel.getHistory()
    }

    private fun loadStatus(): (List<StatusServices>) -> Unit = { items ->

        binding.spStatus.apply {
            adapter = AdapterCustomSpinner(
                activity?.baseContext!!,
                android.R.layout.simple_spinner_dropdown_item,
                items
            )
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val item = binding.spStatus.selectedItem as StatusServices
                    if (position > 0) {
                        showLoader()
                        viewModel.getHistory(item.id)
                    }
                }
            }

        }
    }

    private fun handleTouch(): (AdminServiceModel) -> Unit = {
        listener(it)
    }

    private fun handleDelete(): (Pair<Int, Int>) -> Unit = {
        when (it.first) {
            -1 -> {
                showLoader()
                viewModel.getHistory()
            }
            else -> {
                viewModel.executeDeleteService(it.first, it.second)
            }
        }
    }

    private fun loadInfoServices(): (List<AdminHistoryModel>) -> Unit = { data ->
        hideLoader()

        adapterHistory = AdminHistoryAdapter(
            data.toMutableList(),
            handleTouch(),
            handleDelete(),
            childFragmentManager
        )

        binding.apply {
            rvHistory.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = adapterHistory
            }
        }
    }

    private fun handleDeleteServices(): (Boolean) -> Unit = { success ->
        if (success) {
            showLoader()
            viewModel.getHistory()
        }
    }

    private fun error(): (EAMXMessageError) -> Unit = { error ->
        hideLoader()
        if (::adapterHistory.isInitialized) {
            adapterHistory.clearList()
        }
        UtilAlert.Builder()
            .setTitle(getString(R.string.title_dialog_warning))
            .setMessage(error.message ?: "")
            .setListener {
                activity?.onBackPressed()
            }
            .build()
            .show(childFragmentManager, "")
    }

}