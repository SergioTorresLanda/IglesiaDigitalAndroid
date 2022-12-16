package mx.arquidiocesis.servicios.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXMessageError
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.toast
import mx.arquidiocesis.servicios.R
import mx.arquidiocesis.servicios.adapter.AdminServicesAdapter
import mx.arquidiocesis.servicios.databinding.FragmentServicesAdminServicesBinding
import mx.arquidiocesis.servicios.model.admin.view.AdminServiceModel
import mx.arquidiocesis.servicios.repository.AdminServicesRepository
import mx.arquidiocesis.servicios.viewModel.AdminServicesViewModel

class ServiceAdminServicesFragment : FragmentBase() {

    companion object {
        const val SERVICE_IDENTIFIER = "Service_Identifier"
        @JvmStatic
        fun newInstance(callBack: EAMXHome, listener : (AdminServiceModel)-> Unit) =
            ServiceAdminServicesFragment().apply {
                this.callBack = callBack
                this.listener = listener
            }
    }
    private lateinit var listener: (AdminServiceModel) -> Unit
    private lateinit var binding: FragmentServicesAdminServicesBinding
    private var adapterServices : AdminServicesAdapter? = null

    val viewModel : AdminServicesViewModel by lazy {
        getViewModel {
            AdminServicesViewModel(AdminServicesRepository(requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServicesAdminServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initData()
    }

    private fun initObservers() {
        viewModel.responseServicesP.observe(viewLifecycleOwner, handleLoadInfoServices())
        viewModel.errorP.observe(viewLifecycleOwner,  handleError())
    }

    private fun initData() {
        showLoader()
        viewModel.getServices()
    }

    private fun handleLoadInfoServices(): (List<AdminServiceModel>) -> Unit  = { data ->
        hideLoader()
        adapterServices = AdminServicesAdapter(data.toMutableList()){
            listener(it)
        }

        binding.apply{
            rvAdminServices.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = adapterServices
            }
        }
    }

    private fun handleError(): (EAMXMessageError) -> Unit = { error ->
        hideLoader()
        adapterServices?.let {
            it.clear()
        }
        if(error.back) {
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(error.message ?: "")
                .setListener {
                    activity?.onBackPressed()
                }
                .build()
                .show(childFragmentManager, "")
        }else{
            toast(error.message ?: "")
        }
    }
}