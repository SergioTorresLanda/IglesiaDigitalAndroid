package mx.arquidiocesis.servicios.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert.Companion.ACTION_ACCEPT
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert.Companion.ACTION_CANCEL
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.servicios.R
import mx.arquidiocesis.servicios.adapter.SacramentsAdapter
import mx.arquidiocesis.servicios.databinding.FragmentSacramentsBinding
import mx.arquidiocesis.servicios.repository.Repository
import mx.arquidiocesis.servicios.viewModel.ServiciosViewModel
import mx.arquidiocesis.sos.ui.SOSProfileFragment

const val ACTION_CALL = "ALERT_PHONE"
const val ACTION_SOS = "ALERT_SOS"
const val CODE_REQUEST = 800

class SacramentsFragment: FragmentBase() {

    private val TAG = "SacramentsFragment"
    private lateinit var numberPhone : String

    companion object {
        fun newInstance(callBack: EAMXHome) : SacramentsFragment {
            val fragment = SacramentsFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    private lateinit var binding: FragmentSacramentsBinding
    private val viewModel: ServiciosViewModel by lazy {
        getViewModel {
            ServiciosViewModel(Repository(requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as EAMXHome).showToolbar(true, AppMyConstants.sacramentos)
        binding = FragmentSacramentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoader()
        initObservers()
        viewModel.getSacramentsList()
    }

    private fun initObservers() {
        viewModel.responseSacraments.observe(viewLifecycleOwner) {
            binding.rvSacraments.apply {
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                adapter = SacramentsAdapter(it) { itemSacrament ->
                    when (itemSacrament.action){
                        ACTION_CALL, ACTION_SOS -> {
                            UtilAlert
                                .Builder()
                                .setTitle(itemSacrament.name)
                                .setMessage(itemSacrament.description)
                                .setTextButtonCancel(  when(itemSacrament.action){
                                    ACTION_SOS -> {
                                        getString(R.string.button_go_to_sos)
                                    }
                                    ACTION_CALL -> {
                                        getString(R.string.button_go_to_call)
                                    }
                                    else -> ""
                                })
                                .setTextButtonOk(getString(R.string.button_go_to_descargar))
                                .setListener { response ->
                                    when(response){
                                        ACTION_ACCEPT -> {
                                            executeIntent(Intent.ACTION_VIEW, itemSacrament.file)
                                        }
                                        ACTION_CANCEL -> {
                                            when (itemSacrament.action){
                                                ACTION_SOS -> {
                                                    NavigationFragment.Builder()
                                                        .setActivity(requireActivity())
                                                        .setView(requireView().parent as ViewGroup)
                                                        .setFragment(SOSProfileFragment.newInstance(super.callBack))
                                                        .build().nextWithReplace()
                                                }
                                                ACTION_CALL -> {
                                                    numberPhone = getString(R.string.number_pone_default)
                                                    checkPermissionForExecuteCall()
                                                }
                                            }
                                        }
                                    }
                                }
                                .build()
                                .show(childFragmentManager, TAG)
                        }
                        else -> {
                            executeIntent(Intent.ACTION_VIEW, itemSacrament.file)
                        }
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
                .build()
                .show(childFragmentManager, TAG)
        }
    }

    private fun executeIntent(action: String, url : String){
        val intent = Intent(action, Uri.parse(url))
        startActivity(intent)
    }

    private fun checkPermissionForExecuteCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                executeIntent(Intent.ACTION_CALL, "tel:${numberPhone}")
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CALL_PHONE
                    ),
                    CODE_REQUEST
                )
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CODE_REQUEST ->
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    executeIntent(Intent.ACTION_CALL, "tel:${numberPhone}")
                }
        }
    }
}