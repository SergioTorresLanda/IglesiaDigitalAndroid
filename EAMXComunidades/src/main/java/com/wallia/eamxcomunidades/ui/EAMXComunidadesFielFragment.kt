package com.wallia.eamxcomunidades.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wallia.eamxcomunidades.R
import com.wallia.eamxcomunidades.adapter.ComunityAdapter
import com.wallia.eamxcomunidades.config.Constants
import com.wallia.eamxcomunidades.database.instance.MeetRoomDataBaseCommunity
import com.wallia.eamxcomunidades.databinding.FragmentEamxComunidadesFielBinding
import com.wallia.eamxcomunidades.repository.Repository
import com.wallia.eamxcomunidades.viewmodel.COMUNITYID
import com.wallia.eamxcomunidades.viewmodel.ComunidadesViewModel
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import com.wallia.eamxcomunidades.viewmodel.PRINCIPAL
import com.wallia.eamxcomunidades.viewmodel.SEARCH
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.*
import mx.arquidiocesis.eamxcommonutils.util.*
import mx.arquidiocesis.eamxcommonutils.util.permission.UtilValidPermission
import mx.arquidiocesis.eamxprofilemodule.ui.profile.PERMISSION_LOCATION
import java.lang.reflect.Type

class EAMXComunidadesFielFragment : FragmentBase() {

    lateinit var binding: FragmentEamxComunidadesFielBinding
    lateinit var signOut: EAMXSignOut
    var isAdmin: Boolean = false
    var rol = ""
    private val viewModel: ComunidadesViewModel by lazy {
        getViewModel {
            ComunidadesViewModel(
                Repository(
                    context = requireContext(),
                    database = MeetRoomDataBaseCommunity.getRoomInstance(requireContext())
                )
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(callBack: EAMXHome, signOut: EAMXSignOut): EAMXComunidadesFielFragment {
            val comunidadesFielFragment = EAMXComunidadesFielFragment()
            comunidadesFielFragment.callBack = callBack
            comunidadesFielFragment.signOut = signOut
            return comunidadesFielFragment
        }
    }

    private fun initObservers() {
        viewModel.mainCommunityResponse.observe(viewLifecycleOwner) {
            binding.apply {
                if (it.assigned != null) {
                    val item = it.assigned
                    cvAddPrincipal.visibility = View.GONE
                    iPrincipal.apply {
                        if (rol != EAMXProfile.CommunityResponsible.rol) {
                            tvChangePrincipal.visibility = View.VISIBLE
                        }

                        cvImagen.visibility = View.VISIBLE
                        tvTitle.text = item.name
                        item.arrayImage?.let {
                            ivCommunity.setImageBitmap(it.convertToBitmap())
                        } ?: kotlin.run {
                            if (item.imageUrl.isNullOrEmpty()) {
                                Glide.with(requireContext())
                                    .load(R.drawable.default_image)
                                    .into(ivCommunity)
                            } else {
                                Glide.with(requireContext())
                                    .load(Uri.parse(item.imageUrl))
                                    .into(ivCommunity)
                            }
                        }

                        cvComunity.setOnClickListener {
                            item.id?.let {
                                changeFragmen(it, true)
                                "FLOW EAMXRegisterCommunityFragment EAMXComunidadesFielFragment".log()
                            }
                        }
                    }
                }
                if (!it.locations.isNullOrEmpty()) {
                    cvAddFavorite.visibility = View.GONE
                    tvAddFavorite.visibility = View.VISIBLE
                    val comunityAdapter = ComunityAdapter(it.locations, requireContext()) {
                        it.id?.let {
                            changeFragmen(it)
                        }

                    }
                    binding.rvFavorite.apply {
                        layoutManager =
                            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                        adapter = comunityAdapter
                    }
                }
            }

            hideLoader()
        }

        viewModel.errorResponse.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_eamx_comunidades_fiel,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "Home_Comunidades")
            })
        }
        rol =eamxcu_preferences.getData(
            EAMXEnumUser.USER_PROFILE.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String
        isAdmin = when (rol) {
            EAMXProfile.CommunityResponsible.rol,
            EAMXProfile.CommunityAdmin.rol,
            EAMXProfile.CommunityMember.rol,
            -> true
            else -> false
        }
        if (!(eamxcu_preferences.getData(
                EAMXEnumUser.GUEST.name,
                EAMXTypeObject.BOOLEAN_OBJECT
            ) as Boolean)) {
            viewModel.getMainCommunity()
            initObservers()
            showLoader()
        }
        binding.apply {
            svBusarComunidad.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (svBusarComunidad.query.isNotEmpty()) {
                        changeFragmen(
                            svBusarComunidad.query.toString(),
                            EAMXSearchFragment.newInstance()
                        )
                    }
                    return false
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                    }
                    return false
                }
            })
            //Principal
            cvAddPrincipal.setOnClickListener {
                if (!msgGuest("agregar una comunidad como principal")) {
                    changeFragmen("", EAMXSearchFragment.newInstance(), true)
                }
            }
            tvChangePrincipal.setOnClickListener {
                if (!msgGuest("agregar una comunidad como principal")) {
                    changeFragmen("", EAMXSearchFragment.newInstance(), true)
                }
            }
            //Favorite
            cvAddFavorite.setOnClickListener {
                if (!msgGuest("agregar una comunidad como favorita")) {
                    changeFragmen("", EAMXSearchFragment.newInstance())
                }
            }
            tvAddFavorite.setOnClickListener {
                if (!msgGuest("agregar una comunidad como favorita")) {
                    changeFragmen("", EAMXSearchFragment.newInstance())
                }
            }
            //Buscar
            tvGeo.setOnClickListener {
                changeFragmen("", EAMXMapFragment.newInstance())
            }
            ivGeo.setOnClickListener {
                changeFragmen("", EAMXMapFragment.newInstance())
            }
            ibSearch.setOnClickListener {
                changeFragmen(svBusarComunidad.query.toString(), EAMXSearchFragment.newInstance())
            }
        }
    }

    private fun isAvailableForEdit(churchId: Int): Boolean {
        val list = eamxcu_preferences.getData(
            EAMXEnumUser.USER_CHURCH_ALLOW_EDIT.name,
            EAMXTypeObject.STRING_OBJECT
        ).toString().replace("&quot;", "\"")
        if (list.isEmpty()) {
            return false
        }
        return try {
            val collectionType: Type =
                object : TypeToken<Collection<ModuleAdminEnabled?>?>() {}.type
            val dataList: Collection<ModuleAdminEnabled> = Gson().fromJson(list, collectionType)
            return dataList.firstOrNull { item ->
                item.id == churchId
            } != null
        } catch (ex: Exception) {
            false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (UtilValidPermission().allPermissionsAreAgree(grantResults)) {
            when (requestCode) {
                PERMISSION_LOCATION ->{

                }
            }
        }else{
            when (requestCode) {
                PERMISSION_LOCATION ->{
                    UtilAlert.Builder()
                        .setTitle(getString(R.string.title_dialog_warning))
                        .setMessage("Debes otorgar el permiso de ubicación para poder continuar")
                        .setTextButtonOk("Ir a la configuración")
                        .setListener {
                            startActivity(
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    .setData(Uri.parse("package:" + context?.packageName))
                            )
                        }
                        .build()
                        .show(childFragmentManager, "")
                }
            }

        }
    }

    private fun changeFragmen(search: String, fragment: Fragment, isPrincipal: Boolean = false) {
        if (UtilValidPermission().validListPermissionsAndBuildRequest(
                this@EAMXComunidadesFielFragment, arrayListOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), PERMISSION_LOCATION
            )
        ) {
            val bundle = Bundle()
            bundle.apply {
                putString(SEARCH, search ?: "")
                putBoolean(PRINCIPAL, isPrincipal)
            }
            NavigationFragment.Builder()
                .setActivity(requireActivity())
                .setView(requireView().parent as ViewGroup)
                .setFragment(fragment)
                .setBundle(bundle)
                .build().nextWithReplace()
        }
    }

    private fun changeFragmen(id: Int, isPrincipal: Boolean = false) {
        if (isAdmin && isAvailableForEdit(id)) {
            if (isPrincipal) {
                when (eamxcu_preferences.getData(
                    EAMXEnumUser.USER_COMMUNITY_STATUS.name,
                    EAMXTypeObject.STRING_OBJECT
                ) as String) {
                    Constants.PENDING_COMPLETION -> {
                        NavigationFragment.Builder()
                            .setActivity(requireActivity())
                            .setView((requireView().parent as ViewGroup))
                            .setFragment(
                                EAMXRegisterCommunityFragment.newInstance(
                                    Constants.PENDING_COMPLETION
                                )
                            )
                            .setAllowStack(false)
                            .build().nextWithReplace()
                    }
                    Constants.COMPLETED -> {//Aqui

                        NavigationFragment.Builder()
                            .setActivity(requireActivity())
                            .setView((requireView().parent as ViewGroup))
                            .setFragment(
                                EAMXRegisterCommunityFragment.newInstance(
                                    Constants.COMPLETED
                                )
                            )
                            .setAllowStack(false)
                            .build().nextWithReplace()


                    }
                    else -> {
                        changeRegistro()
                    }
                }
            } else {
                NavigationFragment.Builder()
                    .setActivity(requireActivity())
                    .setView((requireView().parent as ViewGroup))
                    .setFragment(
                        EAMXRegisterCommunityFragment.newInstance(
                            Constants.COMPLETED,
                            id
                        )
                    )
                    .setAllowStack(false)
                    .build().nextWithReplace()
            }
        } else {
            val bundle = Bundle()
            bundle.apply {
                putInt(COMUNITYID, id ?: 0)
                bundle.putBoolean(PRINCIPAL, isPrincipal)
            }
            NavigationFragment.Builder()
                .setActivity(requireActivity())
                .setView(requireView().parent as ViewGroup)
                .setFragment(EAMXDetalleComunidadFragment.newInstance() as Fragment)
                .setBundle(bundle)
                .build().nextWithReplace()
        }

    }

    fun changeRegistro() {
        NavigationFragment.Builder()
            .setActivity(requireActivity())
            .setView(requireView().parent as ViewGroup)
            .setFragment(
                EAMXComunidadesSacerdoteFragment.newInstance(
                    this,
                    signOut
                ) as Fragment
            )
            .setAllowStack(false)
            .build().nextWithReplace()
    }

}