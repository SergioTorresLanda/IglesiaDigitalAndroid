package com.wallia.eamxcomunidades.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import com.wallia.eamxcomunidades.R
import com.wallia.eamxcomunidades.config.Constants
import com.wallia.eamxcomunidades.databinding.FragmentEamxComunidadesSacerdoteBinding
import com.wallia.eamxcomunidades.repository.Repository
import com.wallia.eamxcomunidades.viewmodel.ComunidadesViewModel
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.wallia.eamxcomunidades.database.instance.MeetRoomDataBaseCommunity
import com.wallia.eamxcomunidades.model.CreateCommunityRequest
import com.wallia.eamxcomunidades.model.Data
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import com.wallia.eamxcomunidades.utils.PublicFunctions
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXSignOut
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxlivestreammodule.ProfileLiveFragment

class EAMXComunidadesSacerdoteFragment : FragmentBase() {

    lateinit var binding: FragmentEamxComunidadesSacerdoteBinding
    lateinit var signOut: EAMXSignOut
    private var firtsTime = true
    private val viewModel: ComunidadesViewModel by lazy {
        getViewModel {
            ComunidadesViewModel(Repository(context = requireContext(),
                database = MeetRoomDataBaseCommunity.getRoomInstance(requireContext())))
        }
    }

    var communitiesObj = ArrayList<Data>()
    var indexSelected = 0

    companion object {
        val isBack = MutableLiveData<Boolean>()

        @JvmStatic
        fun newInstance(
            callBack: EAMXHome,
            signOut: EAMXSignOut,
        ): EAMXComunidadesSacerdoteFragment {
            val comunidadesSacerdoteFragment = EAMXComunidadesSacerdoteFragment()
            comunidadesSacerdoteFragment.callBack = callBack
            comunidadesSacerdoteFragment.signOut = signOut
            return comunidadesSacerdoteFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_eamx_comunidades_sacerdote,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()

        val publicFunctions = PublicFunctions()
        loadViewByStatusCommunity()
        //showLoader()

        binding.btnRideME.setOnClickListener {
            NavigationFragment.Builder()
                .setActivity(requireActivity())
                .setView((requireView().parent as ViewGroup))
                .setFragment(EAMXRegisterCommunityFragment.newInstance(
                    Constants.PENDING_COMPLETION))
                .setAllowStack(false)
                .build().nextWithReplace()
        }

        binding.tieResponsableName.setText(publicFunctions.getUserName())

        binding.btnCancel.setOnClickListener {
            isBack.value = true

            callBack.restoreToolbar()
        }

        binding.btnSend.setOnClickListener {
            if (validateField()) {
                binding.apply {
                    showLoader()
                    viewModel.createCommunity(
                        CreateCommunityRequest(
                            "${tieCommunityAddress.text.toString()} ${tieColony.text.toString()} ${tieCP.text.toString()} ${tieTown.text.toString()}",
                            tieCommunityMail.text.toString(),
                            0.0,
                            0.0,
                            tieCommunityName.text.toString(),
                            tieCommunityPhone.text.toString(),
                            communitiesObj[indexSelected - 1].id
                        )
                    )
                }
            }
        }

        binding.cvEnVivo.setOnClickListener {
            NavigationFragment.Builder()
                .setActivity(requireActivity())
                .setView((requireView().parent as ViewGroup))
                .setFragment(ProfileLiveFragment.newInstance(callBack))
                .setAllowStack(false)
                .build().nextWithReplace()
        }

        callBack.showToolbar(true, AppMyConstants.comunidadAdd)
    }

    private fun initObservers() {
        viewModel.userDetailResponse.observe(viewLifecycleOwner) {
            hideLoader()
            it.data?.user?.community?.let {
                binding.apply {
                    when (it.status) {
                        Constants.PENDING_VICARAGE_APPROVAL -> {
                            binding.cvEnVivo.visibility = View.VISIBLE
                            cvPendingCommunity.visibility = View.VISIBLE
                            tvAdviceCommunity.text =
                                "Tu solicitud esta en proceso de revisión"
                        }
                        Constants.PENDING_COMPLETION -> {
                            NavigationFragment.Builder()
                                .setActivity(requireActivity())
                                .setView((requireView().parent as ViewGroup))
                                .setFragment(EAMXRegisterCommunityFragment.newInstance(
                                    Constants.PENDING_COMPLETION))
                                .setAllowStack(false)
                                .build().nextWithReplace()
                        }
                        Constants.COMPLETED -> {//Aqui
                            if (firtsTime) {
                                firtsTime = false
                                NavigationFragment.Builder()
                                    .setActivity(requireActivity())
                                    .setView((requireView().parent as ViewGroup))
                                    .setFragment(EAMXRegisterCommunityFragment.newInstance(
                                        Constants.COMPLETED))
                                    .setAllowStack(true)
                                    .build().nextWithReplace()
                            } else {
                                requireActivity().onBackPressed()
                            }

                        }

                        Constants.REJECTED -> {
                            binding.cvEnVivo.visibility = View.VISIBLE
                            cvPendingCommunity.visibility = View.VISIBLE
                            tvAdviceCommunity.text = "Tu solicitud ha sido rechazada"
                        }
                    }
                }
            } ?: kotlin.run {
                binding.cvRegisterNewCommunity.visibility = View.VISIBLE
                viewModel.getCommunityTypes()
                showLoader()
            }
        }

        viewModel.getCommunityTypesResponse.observe(viewLifecycleOwner) {
            hideLoader()
            communitiesObj = it.data as ArrayList<Data>

            val communities = ArrayList<String>()
            communities.add("Tipo de comunidad")
            it.data.forEach { community ->
                community.description?.let { it1 ->
                    communities.add(it1)
                }
            }

            binding.spCommunityTypes.apply {
                adapter = ArrayAdapter(
                    activity?.baseContext!!,
                    android.R.layout.simple_spinner_dropdown_item,
                    communities
                )

                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        indexSelected = p2
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        print("")
                    }
                }
            }
        }

        viewModel.genericResponse.observe(viewLifecycleOwner) {
            hideLoader()
            binding.cvRegisterNewCommunity.visibility = View.GONE
            binding.cvPendingCommunity.visibility = View.VISIBLE
            callBack.showToolbar(true, AppMyConstants.home)
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage("Gracias por tu envío, espera la notificación de la Vicaría para proseguir con el registro de tu comunidad.")
                .build()
                .show(childFragmentManager, "")
        }

        viewModel.errorResponse.observe(viewLifecycleOwner) {
            hideLoader()
            print("")

            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
        }
    }

    private fun loadViewByStatusCommunity() {
        binding.apply {
            when (eamxcu_preferences.getData(EAMXEnumUser.USER_COMMUNITY_STATUS.name,
                EAMXTypeObject.STRING_OBJECT) as String) {
                Constants.PENDING_VICARAGE_APPROVAL -> {
                    binding.cvEnVivo.visibility = View.VISIBLE
                    cvPendingCommunity.visibility = View.VISIBLE
                    tvAdviceCommunity.text =
                        "Tu solicitud esta en proceso de revisión"
                }
                Constants.PENDING_COMPLETION -> {
                    NavigationFragment.Builder()
                        .setActivity(requireActivity())
                        .setView((requireView().parent as ViewGroup))
                        .setFragment(EAMXRegisterCommunityFragment.newInstance(
                            Constants.PENDING_COMPLETION))
                        .setAllowStack(false)
                        .build().nextWithReplace()
                }
                Constants.COMPLETED -> {//Aqui
                    if (firtsTime) {
                        firtsTime = false
                        NavigationFragment.Builder()
                            .setActivity(requireActivity())
                            .setView((requireView().parent as ViewGroup))
                            .setFragment(EAMXRegisterCommunityFragment.newInstance(
                                Constants.COMPLETED))
                            .setAllowStack(true)
                            .build().nextWithReplace()
                    } else {
                        requireActivity().onBackPressed()
                    }

                }
                Constants.REJECTED -> {
                    binding.cvEnVivo.visibility = View.VISIBLE
                    cvPendingCommunity.visibility = View.VISIBLE
                    tvAdviceCommunity.text = "Tu solicitud ha sido rechazada"
                }
                else -> {
                    binding.cvRegisterNewCommunity.visibility = View.VISIBLE
                    viewModel.getCommunityTypes()
                }

            }
        }
    }

    private fun validateField(): Boolean {
        binding.apply {
            if (tieCommunityName.text.isNullOrBlank() || tieCommunityName.text.isNullOrEmpty()) {
                //tilCommunityName.error = "El nombre es Obligatorio"
                tilCommunityName.error = "Para poder guardar la información, te pedimos que llenes todos los campos"
                return false
            } else if (tieCommunityAddress.text.isNullOrBlank() || tieCommunityAddress.text.isNullOrEmpty()) {
                //tilCommunityAddress.error = "La dirección es Obligatoria"
                tilCommunityAddress.error = "Para poder guardar la información, te pedimos que llenes todos los campos"
                return false
            } else if (tieCP.text.isNullOrBlank() || tieCP.text.isNullOrEmpty()) {
                //tieCP.error = "El Codigo Postal es Obligatoria"
                tieCP.error = "Para poder guardar la información, te pedimos que llenes todos los campos"
                return false
            } else if (tieCommunityMail.text.isNullOrBlank() || tieCommunityMail.text.isNullOrEmpty()) {
                //tilCommunityMail.error = "El correo es Obligatorio"
                tilCommunityMail.error = "Para poder guardar la información, te pedimos que llenes todos los campos"
                return false
            } else if (!tieCommunityMail.text.toString().isEmailValid()) {
                //tilCommunityMail.error = "El correo es erroneo"
                tilCommunityMail.error = "Para poder guardar la información, te pedimos que llenes todos los campos"
                return false
            } else if (indexSelected == 0) {
                Toast.makeText(
                    activity,
                    "Porfavor selecciona un tipo de comunidad",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }
        return true
    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this)
            .matches()
    }
}