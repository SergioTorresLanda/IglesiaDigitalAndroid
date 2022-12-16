package mx.arquidiocesis.sos.ui

import android.Manifest
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.upax.eamxsos.R
import kotlinx.android.synthetic.main.fragment_s_o_s_map.*
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxcommonutils.util.permission.UtilValidPermission
import mx.arquidiocesis.eamxmaps.PublicMaps
import mx.arquidiocesis.sos.repository.SOSRepository
import mx.arquidiocesis.sos.viewmodel.LOCATIONP
import mx.arquidiocesis.sos.viewmodel.SOSServicesFaithfulViewModel
import java.io.IOException

class SOSMapFragment : FragmentBase() {
    var map = MutableLiveData<GoogleMap>()
    var address = MutableLiveData<String>()
    var maker = MutableLiveData<Marker>()
    var client = MutableLiveData<GoogleApiClient>()
    var publicMaps = PublicMaps(map, maker,true)
    var location = MutableLiveData<Location>()
    private val sosViewModel: SOSServicesFaithfulViewModel by lazy {
        getViewModel {
            SOSServicesFaithfulViewModel(SOSRepository(requireContext()))
        }
    }
    var loadAddressFirstTime = false
    lateinit var mapFragment: SupportMapFragment
    var addressList= MutableLiveData<List<Address>>()
    var idService = 0
    var ubicacionInicial = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_s_o_s_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.mapDirSOS) as SupportMapFragment

        if (UtilValidPermission().validListPermissionsAndBuildRequest(
                this@SOSMapFragment,
                arrayListOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), LOCATIONP
            )
        ) {
            iniciar()
        }
    }

    private fun iniciar() {
        idService = requireArguments().getInt(SERVICE_ID)
        when (idService) {
            0 -> {
                activity?.onBackPressed()
            }
            12 -> {
                llUno.visibility = View.GONE

            }
        }
        publicMaps.buildGoogleApiClient(client, requireContext())
        showLoader()
        sosViewModel.getLocation()
        initObservers()
        btnSOSEnviar.setOnClickListener {
            when (idService) {
                0 -> {
                    activity?.onBackPressed()
                }
                12 -> {
                    if (srAddress.query.isNotEmpty()) {
                        val bundle = Bundle().apply {
                            putString(DIRECCION, srAddress.query.toString())
                            putDouble(LATITUDE, maker.value!!.position.latitude ?: 0.0)
                            putDouble(LONGITUDE, maker.value!!.position.longitude ?: 0.0)
                            putInt(SERVICE_ID, requireArguments().getInt(SERVICE_ID))
                            putString(SERVICE_NAME, requireArguments().getString(SERVICE_NAME))
                        }
                        changeFragment(bundle)
                    } else {
                        UtilAlert
                            .Builder()
                            .setTitle("Aviso")
                            .setMessage("Llene todos los campos ")
                            .build()
                            .show(childFragmentManager, "")
                    }
                }
                13 -> {
                    if (srAddress.query.isNotEmpty() && etUno.text.toString().isNotEmpty()) {
                        val bundle = Bundle().apply {
                            putString(DIRECCION, srAddress.query.toString())
                            putString(NOMBRE, etUno.text.toString())
                            putDouble(LATITUDE, maker.value!!.position.latitude ?: 0.0)
                            putDouble(LONGITUDE, maker.value!!.position.longitude ?: 0.0)
                            putInt(SERVICE_ID, requireArguments().getInt(SERVICE_ID))
                            putString(SERVICE_NAME, requireArguments().getString(SERVICE_NAME))
                        }
                        changeFragment(bundle)
                    } else {
                        UtilAlert
                            .Builder()
                            .setTitle("Aviso")
                            .setMessage("Llene todos los campos ")
                            .build()
                            .show(childFragmentManager, "")
                    }
                }
            }

        }
        srAddress.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (srAddress.query.isNotEmpty()) {
                    searchLocation(srAddress.query.toString())
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                newText?.let {

                }

                return false
            }
        })
    }

    fun initObservers() {
        sosViewModel.locationResponse.observe(viewLifecycleOwner) {
            if (ubicacionInicial) {
                if (it != null) {
                    location.value = it
                    mapFragment.getMapAsync(publicMaps)
                    ubicacionInicial = false
                    sosViewModel.setLocation(it)

                } else {
                    hideLoader()
                    UtilAlert.Builder()
                        .setTitle(getString(R.string.title_dialog_warning))
                        .setMessage(getString(R.string.text_message_location_not_available))
                        .build()
                        .show(childFragmentManager, tag)
                }
            }

        }
        client.observe(viewLifecycleOwner) {
            it.connect()
        }
        map.observe(viewLifecycleOwner) {
            it.setOnMarkerClickListener(publicMaps)
            publicMaps.addMaker(location.value!!.latitude, location.value!!.longitude)
            it.setOnMapClickListener {
                publicMaps.moveMarker(it.latitude, it.longitude)
            }
            // it.setOnMarkerDragListener(publicMaps)

            //  map.value?.setOnMarkerDragListener()
            hideLoader()
            //viewModel.getiglesiasList()
        }
        maker.observe(viewLifecycleOwner) {
            // it.showInfoWindow()
        }
        sosViewModel.errorResponse.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
        }
        addressList.observe(viewLifecycleOwner){
            if (!it.isNullOrEmpty()) {
                val address = it.first()
                publicMaps.moveMarker(address.latitude, address.longitude)
            } else {
                UtilAlert
                    .Builder()
                    .setTitle("Aviso")
                    .setMessage("Dirección no valida")
                    .setListener { hideLoader() }
                    .build()
                    .show(childFragmentManager, "")
            }
            btnSOSEnviar.isEnabled=true
        }
        publicMaps.addressMap.observe(viewLifecycleOwner, handleAddress())
    }

    private fun handleAddress(): (String) -> Unit = {
        if (loadAddressFirstTime.not()) {
            loadAddressFirstTime = true
            srAddress.setQuery(it, false)
        }
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


    fun searchLocation(location: String) {
        btnSOSEnviar.isEnabled=false
        if (location == null || location == "") {
            Toast.makeText(requireContext(), "provide location", Toast.LENGTH_SHORT).show()
        } else {
            val geoCoder = Geocoder(requireContext())
            try {
                addressList.value = geoCoder.getFromLocationName(location, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun changeFragment(bundle: Bundle) {

        NavigationFragment.Builder()
            .setActivity(requireActivity())
            .setView(requireView().parent as ViewGroup)
            .setFragment(SOSListPriestChurchFaithfulFragment.newInstance())
            .setAllowStack(true)
            .setBundle(bundle)
            .build().nextWithReplace()

    }
}