package mx.arquidiocesis.sos.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.upax.eamxsos.R
import com.upax.eamxsos.databinding.FragmentFaithfulProfileBinding
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.sos.repository.SOSRepository
import mx.arquidiocesis.sos.utils.Constants
import mx.arquidiocesis.sos.viewmodel.SOSServicesFaithfulViewModel

const val SERVICE_NAME: String = "ServiceName"
const val SERVICE_ID: String = "ServiceId"
const val ID: String = "id"
const val NOMBRE: String = "Nombre"
const val DIRECCION: String = "Dirrecion"
const val LATITUDE: String = "LATITUDE"
const val LONGITUDE: String = "LONGITUDE"
class FaithfulProfileFragment : FragmentBase() {

    companion object {
        fun newInstance(): FaithfulProfileFragment {
            val fragment = FaithfulProfileFragment()
            //fragment.callBack = callBack
            return fragment
        }
    }

    private val TAG: String = FaithfulProfileFragment::getTag.name
    private val viewModel: SOSServicesFaithfulViewModel by lazy {
        getViewModel {
            SOSServicesFaithfulViewModel(SOSRepository(requireContext()))
        }
    }
    lateinit var binding: FragmentFaithfulProfileBinding
    private var boton = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFaithfulProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initObservers()
        initView()
        if (!viewModel.twiceInvoke) {
            showLoader()
            viewModel.getServicesList()
        }
    }

    private fun initObservers() {
        viewModel.setObserver(viewLifecycleOwner)

        viewModel.liveAdapter().observe(viewLifecycleOwner) {
            binding.rvService.apply {
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                adapter = it
            }
            hideLoader()
        }
        viewModel.servicioPendiente.observe(viewLifecycleOwner) {
            if (boton) {
                if (it != null) {
                    var id = 0
                    when (it.status) {
                        Constants.Status.COMPLETED -> {

                        }
                        Constants.Status.CANCELLED -> {
                        }
                        else -> {
                            id = it.service_id
                        }
                    }
                    boton = false
                    if (id == 0) {
                        nextFragment()
                    } else {

                        val bundle = Bundle().apply {
                            putInt(ID, id)
                            putInt(USER_ID, viewModel.getUserId())
                        }

                        NavigationFragment.Builder()
                            .setActivity(requireActivity())
                            .setView(requireView().parent as ViewGroup)
                            .setFragment(SOSNotificationFaithfulFragment.newInstance())
                            .setBundle(bundle)
                            .setAllowStack(false)
                            .build().nextWithReplace()
                    }
                }
            }
            hideLoader()

        }

        viewModel.errorResponse.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert
                .Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(it)
                .setIsCancel(false)
                .build()
                .show(childFragmentManager, TAG)
        }

        viewModel.errorResponseExit.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert
                .Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(it)
                .setListener {
                    activity?.onBackPressed()
                }
                .setIsCancel(false)
                .build()
                .show(childFragmentManager, TAG)
        }
    }

    private fun initView() {
        //callBack.showToolbar(true, AppMyConstants.sosServicios)

        binding.tvActionNext.setOnClickListener {
            process()
        }
    }

    private fun process() {
        if (viewModel.itemService == null) {
            UtilAlert
                .Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(getString(R.string.text_message_priesfult_empty))
                .setIsCancel(false)
                .build()
                .show(childFragmentManager, TAG)
        } else {
            showLoader()
            boton = true
            if (viewModel.itemService!!.id==12){
                activity?.let {
                    EAMXFirebaseManager(it).setLogEvent("screen_view_tag", Bundle().apply {
                        putString("screen_name", "Android_Sos_UncionDeLosEnfermos")
                    })
                }
            } else if (viewModel.itemService!!.id==13){
                activity?.let {
                    EAMXFirebaseManager(it).setLogEvent("screen_view_tag", Bundle().apply {
                        putString("screen_name", "Android_Sos_CelebracionDeDifuntos")
                    })
                }
            }
            // viewModel.getPriestServicesWithDevotee(viewModel.getUserId())
            viewModel.pendiente(viewModel.getUserId(), "FIEL", viewModel.itemService!!.id)
        }
    }

    private fun nextFragment() {

        // alertFragment(viewModel.itemService!!.id)

        val bundle = Bundle()
        bundle.apply {
            putInt(SERVICE_ID, viewModel.itemService?.id ?: 0)
            putString(SERVICE_NAME, viewModel.itemService?.name)
        }
        NavigationFragment.Builder()
            .setActivity(requireActivity())
            .setView(requireView().parent as ViewGroup)
            .setFragment(SOSMapFragment())
            .setAllowStack(false)
            .setBundle(bundle)
            .build().nextWithReplace()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        // callBack.restoreToolbar()
    }
}