package mx.arquidiocesis.eamxdonaciones.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.eamxdonaciones.R
import com.example.eamxdonaciones.databinding.FragmentMyDonationBinding
import kotlinx.android.synthetic.main.fragment_my_donation.*
import kotlinx.android.synthetic.main.fragment_my_donation.llSkeleton
import kotlinx.android.synthetic.main.fragment_my_donation.view.*
import kotlinx.android.synthetic.main.item_donation.*
import kotlinx.android.synthetic.main.skeleton_my_donation.*
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxcommonutils.util.permission.UtilValidPermission
import mx.arquidiocesis.eamxcommonutils.util.visibility
import mx.arquidiocesis.eamxdonaciones.model.DonationModel
import mx.arquidiocesis.eamxmaps.ui.MapFragment
import mx.arquidiocesis.misiglesias.adapters.ChurchAdapter
import mx.arquidiocesis.misiglesias.database.instance.MeetRoomDataBase
import mx.arquidiocesis.misiglesias.repository.Repository
import mx.arquidiocesis.misiglesias.ui.PERMISSION_LOCATION
import mx.arquidiocesis.misiglesias.viewmodel.MisIgleciasViewModel


class MyDonationFragment : FragmentBase() {
    companion object {
        fun newInstance(): MyDonationFragment {
            val fragment = MyDonationFragment()
            return fragment
        }
    }

    private lateinit var binding: FragmentMyDonationBinding
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
    private var isPrincipal = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyDonationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            tvGeo.setOnClickListener {
                isPrincipal = false
                changeMap()
            }
            ivGeo.setOnClickListener {
                isPrincipal = false
                changeMap()
            }

        }
        showSkeleton(true)
        initObservers()
        myChurchViewModel.iglesiasList()
        // myChurchViewModel.getiglesiasList()
    }


    private fun initObservers() {
        myChurchViewModel.allChurchList.observe(viewLifecycleOwner) { itemInfoChurch ->
            binding.apply { //TODO se quita el efecto esqueleton
                myChurchViewModel.getSuggestion()
                clPrincipal.visibility(itemInfoChurch?.assigned != null)
                tvPrincipal.visibility(itemInfoChurch?.assigned != null)
                itemInfoChurch?.assigned?.let { item ->
                    clPrincipal.iPrincipal.apply {
                        tvTitulo.text = item.name
                        if (item.image_url.isNotEmpty())
                            Glide.with(requireContext())
                                .load(Uri.parse(item.image_url))
                                .into(ivChurch)
                        else ivChurch.setImageResource(R.drawable.emptychurch)
                    }
                    clPrincipal.setOnClickListener {
                        changeFragment(DonationModel(item.id, item.name, item.image_url))
                    }
                }
                tvFavorites.visibility(!itemInfoChurch?.locations.isNullOrEmpty())
                rvUserChurches.visibility(!itemInfoChurch?.locations.isNullOrEmpty())
                rvUserChurches.adapter =
                    ChurchAdapter(requireContext(), itemInfoChurch?.locations) { item ->
                        isPrincipal = false
                        changeFragment(DonationModel(item.id, item.name, item.image_url))
                    }
            }

        }
        myChurchViewModel.suggestionList.observe(viewLifecycleOwner){itemInfoChurch ->
            showSkeleton(false)
            if (!itemInfoChurch.isNullOrEmpty()) {
                rvUserCampana.apply {
                    layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    adapter =
                        ChurchAdapter(requireContext(), itemInfoChurch) { item ->
                            isPrincipal = false
                            changeFragment(DonationModel(item.id,item.name,item.image_url))
                        }
                }
            }
        }

        myChurchViewModel.errorResponse.observe(viewLifecycleOwner) {
            showSkeleton(false)
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .setIsCancel(false)
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


   /* private fun Alerta(item: DonationModel) {
        UtilAlert
            .Builder()
            .setTitle("Aviso")
            .setMessage("¿Deseas continuar con el apoyo o donación?")
            .setListener { action ->
                when (action) {
                    UtilAlert.ACTION_ACCEPT -> {

                    }
                    UtilAlert.ACTION_CANCEL -> {

                    }
                }
            }
            .build()
            .show(childFragmentManager, "")
    }*/
    private fun changeFragment(item: DonationModel){
        NavigationFragment.Builder()
            .setActivity(requireActivity())
            .setView(requireView().parent as ViewGroup)
            .setFragment(DonationFragment(item))
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
                PERMISSION_LOCATION ->{

                }
            }
        }else{
            when (requestCode) {
                PERMISSION_LOCATION ->{
                    UtilAlert.Builder()
                        .setTitle(getString(mx.arquidiocesis.misiglesias.R.string.title_dialog_warning))
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
                this@MyDonationFragment, arrayListOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), PERMISSION_LOCATION
            )
        ) {
            fragmentMap = MapFragment(true) { item, location ->
                changeFragment(DonationModel(item.id, item.name, item.imageUrl))
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