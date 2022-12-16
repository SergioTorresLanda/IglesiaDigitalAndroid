package mx.arquidiocesis.servicios.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXMessageError
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert.Companion.ACTION_ACCEPT
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert.Companion.ACTION_CANCEL
import mx.arquidiocesis.eamxcommonutils.util.*
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxmaps.PublicMaps
import mx.arquidiocesis.servicios.R
import mx.arquidiocesis.servicios.databinding.FragmentServicesAdminDetailRequestBinding
import mx.arquidiocesis.servicios.model.admin.view.AdminDetailModel
import mx.arquidiocesis.servicios.repository.AdminServicesRepository
import mx.arquidiocesis.servicios.ui.admin.ServiceAdminServicesFragment.Companion.SERVICE_IDENTIFIER
import mx.arquidiocesis.servicios.viewModel.AdminServicesViewModel

class ServiceAdminDetailRequestFragment : FragmentBase() {
    val acceptedServices = "ACCEPTED"
    val closedServices = "COMPLETED"
    val rejectedServices = "REJECTED"
    var map = MutableLiveData<GoogleMap>()
    var maker = MutableLiveData<Marker>()
    var publicMaps = PublicMaps(map, maker)

    companion object {
        @JvmStatic
        fun newInstance(callBack: EAMXHome) =
            ServiceAdminDetailRequestFragment().apply {
                this.callBack = callBack
            }
    }

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var binding: FragmentServicesAdminDetailRequestBinding

    val viewModel: AdminServicesViewModel by lazy {
        getViewModel {
            AdminServicesViewModel(AdminServicesRepository(requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServicesAdminDetailRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initView()
    }

    private fun initView() {
        binding.apply {
            item = AdminDetailModel()
            btnAccept.setOnClickListener { v -> handleAccept(v) }
            btnCancel.setOnClickListener { v -> handleCancel(v) }
            btnClosedRequest.setOnClickListener { v -> handleClosedRequest(v) }
            ivShared.setOnClickListener { v -> handleShared(v) }
            tvServicesShared.setOnClickListener { v -> handleShared(v) }
            mapFragment = childFragmentManager.findFragmentById(R.id.mapServices) as SupportMapFragment
            mapFragment.getMapAsync(publicMaps)
        }
    }

    private fun handleAccept(v: View?) {
        binding.apply {
            etAnswer.apply {
                isActivated = true
                isPressed = true
                isEnabled = true
                requestFocus()
            }
            btnCancel.visibility = View.GONE
            btnAccept.apply {
                text = getString(R.string.button_ready)
                setOnClickListener {
                    handleAttendRequest()
                }
            }
        }
    }

    private fun handleCancel(v: View?) {
        showLoader()
        UtilAlert.Builder()
            .setTitle(getString(R.string.title_dialog_warning))
            .setMessage(getString(R.string.message_service_attended_cancel))
            .setTextButtonOk(getString(R.string.button_yes))
            .setTextButtonCancel(getString(R.string.button_no))
            .setListener {
                when (it) {
                    ACTION_ACCEPT -> viewModel.executeChangeStatusService(
                        binding.item?.id ?: 0,
                        rejectedServices
                    )
                    ACTION_CANCEL -> {
                    }
                }
            }
            .build()
            .show(childFragmentManager, this.javaClass.toString())
    }

    private fun handleClosedRequest(v: View?) {
        UtilAlert.Builder()
            .setTitle(getString(R.string.title_dialog_warning))
            .setMessage(getString(R.string.message_service_attended_succesfull))
            .setTextButtonOk(getString(R.string.button_yes))
            .setTextButtonCancel(getString(R.string.button_no))
            .setListener {
                when (it) {
                    ACTION_ACCEPT -> {
                        showLoader()
                        viewModel.executeChangeStatusService(binding.item?.id ?: 0, closedServices)
                    }
                    ACTION_CANCEL -> {
                    } //TODO enlazar con el buzon de quejas
                }
            }
            .build()
            .show(childFragmentManager, this.javaClass.toString())
    }

    private fun handleShared(v: View?) {
        requireActivity().sharedLocationInGoogleMapsWithOtherApps(
            typeText = "text/plain",
            getString(R.string.shared_location_services),
            getString(R.string.shared_location_services),
            binding.item?.latitude.toString(),
            binding.item?.longitude.toString()
        )
    }

    private fun handleAttendRequest() {
        showLoader()
        viewModel.executeChangeStatusService(
            binding.item?.id ?: 0,
            acceptedServices,
            binding.etAnswer.text.toString()
        )
    }

    private fun initData() {
        showLoader()
        val identifier = requireArguments().getInt(SERVICE_IDENTIFIER)
        viewModel.getDetailServiceById(identifier)
    }

    private fun initObservers() {
        viewModel.responseServicesDetailP.observe(viewLifecycleOwner, handleServicesDetail())
        viewModel.responseExecuteChangeStatusP.observe(
            viewLifecycleOwner,
            handleExecuteChangeStatus()
        )
        viewModel.responseAddCommentP.observe(viewLifecycleOwner, handleAddComment())
        map.observe(viewLifecycleOwner){
            initData()
        }
        viewModel.errorP.observe(viewLifecycleOwner, handleError())
    }

    private fun handleServicesDetail(): (AdminDetailModel) -> Unit = { item ->
        hideLoader()
        callBack.showToolbar(true, item.serviceName)
        binding.item = item
        publicMaps.addMaker(
            item.latitude, item.longitude, R.drawable.ic_home_services
                .convertDrawableToBitmapDescriptionForMarketMap(requireContext(), 60, 80)
        )
    }

    private fun handleExecuteChangeStatus(): (Boolean) -> Unit = {
        hideLoader()
        binding.apply {
            when (viewModel.changeStatus) {
                acceptedServices -> {
                    etAnswer.visibility = View.GONE
                    btnAccept.visibility = View.GONE
                    cvServicesDetailMap.visibility=View.VISIBLE
                    btnClosedRequest.visibility = View.VISIBLE
                }
                closedServices -> {
                    toast("Cerrado exitoso")
                    activity?.onBackPressed()
                }
                rejectedServices -> {
                    toast("Cancelación exitosa")
                    activity?.onBackPressed()
                    /*NavigationFragment.Builder()
                        .setActivity(requireActivity())
                        .build().back()*/
                }
            }
        }
    }

    private fun handleAddComment(): (Boolean) -> Unit = {
        hideLoader()
        UtilAlert.Builder()
            .setTitle(getString(R.string.title_dialog_warning))
            .setMessage(getString(R.string.services_cancel))
            .setListener {
                //activity?.onBackPressed()
                /*NavigationFragment.Builder()
                    .setActivity(requireActivity())
                    .build().back()*/
            }
            .build()
            .show(childFragmentManager, "")
    }

    private fun handleError(): (EAMXMessageError) -> Unit = { error ->
        hideLoader()
        if (error.back) {
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(error.message ?: "")
                .setIsCancel(false)
                .setListener {
                    activity?.onBackPressed()
                    /*NavigationFragment.Builder()
                        .setActivity(requireActivity())
                        .build().back()*/
                }
                .build()
                .show(childFragmentManager, "")
        }else{
            toast(error.message ?: "")
        }
    }
}
