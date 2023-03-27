package mx.arquidiocesis.misiglesias.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_my_churches.*
import kotlinx.android.synthetic.main.fragment_my_churches.llSkeleton
import kotlinx.android.synthetic.main.fragment_my_churches.view.*
import kotlinx.android.synthetic.main.item_church_finded.*
import kotlinx.android.synthetic.main.skeleton_my_church.*
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxcommonutils.util.permission.UtilValidPermission
import mx.arquidiocesis.eamxmaps.ui.MapFragment
import mx.arquidiocesis.misiglesias.R
import mx.arquidiocesis.misiglesias.adapters.ChurchAdapter
import mx.arquidiocesis.misiglesias.adapters.ChurchesFindedAdapter
import mx.arquidiocesis.misiglesias.database.instance.MeetRoomDataBase
import mx.arquidiocesis.misiglesias.databinding.FragmentMyChurchesBinding
import mx.arquidiocesis.misiglesias.model.DataForView
import mx.arquidiocesis.misiglesias.repository.Repository
import mx.arquidiocesis.misiglesias.viewmodel.MisIgleciasViewModel

const val PERMISSION_LOCATION = 10007

class MisIglesiasFragment : FragmentBase() {
    companion object {
        fun newInstance(callBack: EAMXHome): MisIglesiasFragment {
            val fragment = MisIglesiasFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    private lateinit var binding: FragmentMyChurchesBinding
    private val myChurchViewModel: MisIgleciasViewModel by lazy {
        getViewModel {
            MisIgleciasViewModel(
                Repository(
                    requireContext(),
                    MeetRoomDataBase.getRoomInstance(requireActivity())
                )
            )
        }
    }
    private lateinit var fragmentMap: MapFragment
    private lateinit var dataView: DataForView
    private var adapterChurch = ChurchesFindedAdapter(listOf()) {}
    private var isPrincipal = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMyChurchesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "Home_MiIglesia")
            })
        }
        callBack.showToolbar(true, AppMyConstants.miIglesia)
        binding.apply {
            rvSearchChurches.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            tvChangePrincipal.setOnClickListener {
                if (!msgGuest("agregar una iglesia principal")) {
                    isPrincipal = true
                    changeMap()
                }
            }
            cvEmptyChurchPrincipal.setOnClickListener {
                if (!msgGuest("agregar una iglesia principal")) {
                    isPrincipal = true
                    changeMap()
                }
            }
            tvAddFavorite.setOnClickListener {
                if (!msgGuest("agregar una iglesia favorita")) {
                    isPrincipal = false
                    changeMap()
                }
            }
            clEmptyChurchFavorites.setOnClickListener {
                if (!msgGuest("agregar una iglesia favorita")) {
                    isPrincipal = false
                    changeMap()
                }
            }
            tvGeo.setOnClickListener {
                isPrincipal = false
                changeMap()
            }
            ivGeo.setOnClickListener {
                isPrincipal = false
                changeMap()
            }

        }
        if (!msgGuest(isMsg = false)){
            this.dataView = arguments?.getParcelable(EAMXEnumUser.VIEW.name)!!
            binding.item = this.dataView
            showSkeleton(true)
            initObservers()
            myChurchViewModel.iglesiasList()
        }
    }


    private fun initObservers() {
        myChurchViewModel.allChurchList.observe(viewLifecycleOwner) { itemInfoChurch ->
            //TODO se quita el efecto esqueleton
            showSkeleton(false)
            if (itemInfoChurch?.assigned != null) {
                val item = itemInfoChurch.assigned
                cvPrincipalOrAssigned.visibility = View.VISIBLE
                cvEmptyChurchPrincipal.visibility = View.INVISIBLE
                tvChangePrincipal.visibility = View.VISIBLE
                binding.clPrincipal.iPrincipal.apply {
                    tvTitulo.text = item.name
                    if (item.image_url != null) {
                        Glide.with(requireContext())
                            .load(Uri.parse(item.image_url))
                            .into(ivChurch)
                    } else {
                        ivChurch.setImageDrawable(context?.getDrawable(R.drawable.emptychurch))
                    }
                    if (!item.schedules.isNullOrEmpty()) {
                        tvHorarioss.text = ""
                        item.schedules.forEach {
                            if (!it.start_hour.isNullOrEmpty()) {
                                tvHorarioss.text = "${it.start_hour}  "
                            }
                            if (!it.end_hour.isNullOrEmpty()) {
                                tvHorarioss.text = "${tvHorarioss.text} ${it.end_hour} \n"
                            }

                        }
                    }
                }
                cvPrincipalOrAssigned.setOnClickListener {
                    isPrincipal = true
                    changeFragment(itemInfoChurch.assigned?.id)
                }
            } else {
                cvPrincipalOrAssigned.visibility = View.INVISIBLE
                cvEmptyChurchPrincipal.visibility = View.VISIBLE
            }

            if (itemInfoChurch?.locations.isNullOrEmpty()) {
                clEmptyChurchFavorites.visibility = View.VISIBLE
                rvUserChurches.visibility = View.INVISIBLE
                tvAddFavorite.visibility = View.INVISIBLE
                cvUserChurches.visibility = View.INVISIBLE
            } else {
                cvUserChurches.visibility = View.VISIBLE
                clEmptyChurchFavorites.visibility = View.GONE
                rvUserChurches.visibility = View.VISIBLE
                tvAddFavorite.visibility = View.VISIBLE
                rvSearchChurches.visibility = View.GONE
                rvUserChurches.apply {
                    layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    adapter = ChurchAdapter(requireContext(), itemInfoChurch?.locations) { item ->
                        isPrincipal = false
                        changeFragment(item.id)
                    }
                }
            }
            showSkeleton(false)
        }
        myChurchViewModel.getIglesiasBusqueda.observe(viewLifecycleOwner) {
            it?.let {
                adapterChurch = ChurchesFindedAdapter(it) {
                    isPrincipal = false
                    changeFragment(it.id)
                }
                cvUserChurches.visibility = View.VISIBLE
                tvAddFavorite.visibility = View.VISIBLE
                clEmptyChurchFavorites.visibility = View.GONE
                binding.apply {
                    rvSearchChurches.apply {
                        layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        visibility = View.VISIBLE
                        adapter = adapterChurch
                    }
                    rvUserChurches.visibility = View.GONE
                }
            }

            showSkeleton(false)
        }
        myChurchViewModel.errorResponse.observe(viewLifecycleOwner) {
            showSkeleton(false)
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
        }
    }

    private fun showSkeleton(show: Boolean) {
        clViewComplete.visibility = if (show) View.GONE else View.VISIBLE
        llSkeleton.visibility = if (show) View.VISIBLE else View.GONE

        if (show) {
            shimmerFaithful.startShimmer()
        } else {
            shimmerFaithful.stopShimmer()
        }
    }

    private fun changeFragment(id: Int) {
        val bundle = Bundle()
        bundle.putBoolean("isPrincipal", isPrincipal)
        bundle.putInt("idIglesia", id)

        NavigationFragment.Builder()
            .setActivity(requireActivity())
            .setView(requireView().parent as ViewGroup)
            .setFragment(DetailChurchFindedFragment.newInstance(requireActivity() as EAMXHome))
            .setBundle(bundle)
            .setAllowStack(true)
            .build().nextWithReplace()
    }

    private fun changeMapFragment(id: Int) {
        val bundle = Bundle()
        bundle.putBoolean("isPrincipal", isPrincipal)
        bundle.putInt("idIglesia", id)

        NavigationFragment.Builder()
            .setActivity(requireActivity())
            .setView(fragmentMap.requireView().parent as ViewGroup)
            .setFragment(DetailChurchFindedFragment.newInstance(requireActivity() as EAMXHome))
            .setBundle(bundle)
            .setAllowStack(true)
            .build().nextWithReplace()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (UtilValidPermission().allPermissionsAreAgree(grantResults)) {
            when (requestCode) {
                PERMISSION_LOCATION -> {
                }
            }
        } else {
            when (requestCode) {
                PERMISSION_LOCATION -> {
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

    private fun changeMap() {
        if (UtilValidPermission().validListPermissionsAndBuildRequest(
                this@MisIglesiasFragment, arrayListOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), PERMISSION_LOCATION
            )
        ) {
            fragmentMap = MapFragment(true) { item, location ->
                changeMapFragment(item.id)
            }
            NavigationFragment.Builder()
                .setActivity(requireActivity())
                .setView(requireView().parent as ViewGroup)
                .setFragment(fragmentMap)
                .setAllowStack(true)
                .build().nextWithReplace()
        }
    }
}