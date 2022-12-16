package mx.arquidiocesis.sos.ui

import android.Manifest
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.upax.eamxsos.R
import com.upax.eamxsos.databinding.FragmentListPriestChurchSOSFielBinding
import mx.arquidiocesis.eamxcommonutils.base.FragmentBaseWithGps
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.permission.UtilValidPermission
import mx.arquidiocesis.sos.adapters.PriestChurchFaithfulAdapter
import mx.arquidiocesis.sos.model.LocationSOSModel
import mx.arquidiocesis.sos.model.SupportModel
import mx.arquidiocesis.sos.repository.SOSRepository
import mx.arquidiocesis.sos.viewmodel.LOCATIONP
import mx.arquidiocesis.sos.viewmodel.SOSServicesFaithfulViewModel

const val PRIEST_NAME: String = "PriestName"
const val DISTANCE: String = "Distance"
const val USER_ID: String = "UserId"

class SOSListPriestChurchFaithfulFragment : FragmentBaseWithGps() {

    companion object {
        fun newInstance(): SOSListPriestChurchFaithfulFragment {
            val fragment = SOSListPriestChurchFaithfulFragment()
            //fragment.callBack = callBack
            return fragment
        }
    }

    private val TAG = SOSListPriestChurchFaithfulFragment.javaClass.name
    private lateinit var namePriest: String
    private var distance: Double? = null
    var executeNext: Boolean = false
    private var idServicio = 0

    private val sosViewModel: SOSServicesFaithfulViewModel by lazy {
        getViewModel {
            SOSServicesFaithfulViewModel(SOSRepository(requireContext()))
        }
    }
    lateinit var binding: FragmentListPriestChurchSOSFielBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        idServicio = requireArguments().getInt(ID)
        initObservers()
        binding = FragmentListPriestChurchSOSFielBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        if (requireArguments().getDouble(LATITUDE) != 0.0 && requireArguments().getDouble(LONGITUDE) != 0.0) {
            if (binding.rvService.adapter == null || binding.rvService.adapter?.itemCount == 0) {
                showLoader()
                sosViewModel.getLocationsSOS(
                    requireArguments().getDouble(LATITUDE), requireArguments().getDouble(LONGITUDE)
                )
            }
        } else if (UtilValidPermission().validListPermissionsAndBuildRequest(
                this@SOSListPriestChurchFaithfulFragment,
                arrayListOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), LOCATIONP
            )
        ) {
            iniciar()
        }


    }

    private fun iniciar() {
        showLoader()
        sosViewModel.getLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (UtilValidPermission().allPermissionsAreAgree(grantResults)) {
            when (requestCode) {
                LOCATIONP -> {
                    iniciar()
                }

            }
        }
    }

    private fun initObservers() {
        sosViewModel.locationResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                if (binding.rvService.adapter == null || binding.rvService.adapter?.itemCount == 0) {
                    sosViewModel.setLocation(it)
                    sosViewModel.getLocationsSOS()
                }
            } else {
                hideLoader()
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_warning))
                    .setMessage(getString(R.string.text_message_location_not_available))
                    .build()
                    .show(childFragmentManager, TAG)
            }
        }

        sosViewModel.locationsSOS.observe(viewLifecycleOwner) {
            binding.rvService.adapter = PriestChurchFaithfulAdapter(it,
                { phone -> startActivity(Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:$phone"))) }) { local, priest ->
                if (!executeNext) {
                    showLoader()
                    namePriest = priest.name
                    distance = local.distance
                    // if (idServicio == 0) {
                    this.sendRequest(
                        sosViewModel.getUserId(), local, priest
                    )
                    /*} else {
                        sosViewModel.progresStatus(idServicio, priest.id, local.id)
                    }*/

                } else {
                    executeNext = false
                }
            }
            hideLoader()
        }

        sosViewModel.successRegistrySOS.observe(viewLifecycleOwner) {
            hideLoader()
            idServicio = it.service_id
            if (!executeNext) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.text_title_request_sos_success))
                    .setMessageCustom(this.buildTextSuccess())
                    .setListener {
                        nextScreenNotification()
                    }
                    .build()
                    .show(childFragmentManager, TAG)
            }
        }
        sosViewModel.updateStatus.observe(viewLifecycleOwner) {
            hideLoader()
            if (!executeNext) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.text_title_request_sos_success))
                    .setMessageCustom(this.buildTextSuccess())
                    .setListener {
                        nextScreenNotification()
                    }
                    .build()
                    .show(childFragmentManager, TAG)
            }
        }
        sosViewModel.errorResponse.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert
                .Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(it)
                .build()
                .show(childFragmentManager, TAG)
        };
    }

    private fun nextScreenNotification() {
        executeNext = true

        val bundle = Bundle().apply {
            putInt(ID, idServicio)
            putInt(USER_ID, sosViewModel.getUserId())
        }
        val fragment = SOSNotificationFaithfulFragment()
        fragment.arguments = bundle
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameSOSProfile, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()

    }

    private fun sendRequest(devoteeId: Int, local: LocationSOSModel, priest: SupportModel) {
        sosViewModel.registrySOS(
            devoteeId,
            eamxcu_preferences.getData(EAMXEnumUser.USER_PHONE.name, EAMXTypeObject.STRING_OBJECT)
                .toString(),
            requireArguments().getInt(SERVICE_ID),
            priest.id,
            sosViewModel.latitudeFaithful,
            sosViewModel.longitudeFaithful,
            requireArguments().getDouble(LATITUDE),
            requireArguments().getDouble(LONGITUDE),
            requireArguments().getString(NOMBRE),
            requireArguments().getString(DIRECCION)
        )
    }

    private fun initView() {
        //callBack.showToolbar(true, AppMyConstants.sos)
        binding.tvNameServices.text = requireArguments().getString(SERVICE_NAME)
    }

    private fun buildTextSuccess(): SpannableString {
        val textBuilder = StringBuilder().apply {
            append("${getString(R.string.text_message_sos_name_priest_begin)}\n")
            append(
                "${
                    String.format(
                        getString(R.string.text_message_sos_name_priest_middle),
                        namePriest
                    )
                } \n"
            )
            append(getString(R.string.text_message_sos_name_priest_final))
        }

        val spannableString = SpannableString(textBuilder.toString())
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            getString(R.string.text_message_sos_name_priest_begin).length + 1,
            textBuilder.lastIndexOf("Se"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannableString
    }
}