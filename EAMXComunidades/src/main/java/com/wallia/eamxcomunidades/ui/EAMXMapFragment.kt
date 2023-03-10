package com.wallia.eamxcomunidades.ui

import android.Manifest
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.wallia.eamxcomunidades.R
import com.wallia.eamxcomunidades.adapter.CustomInfoWindowCommunity
import com.wallia.eamxcomunidades.database.instance.MeetRoomDataBaseCommunity
import com.wallia.eamxcomunidades.repository.Repository
import com.wallia.eamxcomunidades.viewmodel.COMUNITYID
import com.wallia.eamxcomunidades.viewmodel.ComunidadesViewModel
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import com.wallia.eamxcomunidades.databinding.FragmentEamxMapBinding
import com.wallia.eamxcomunidades.model.CommunityMapModel
import com.wallia.eamxcomunidades.viewmodel.PRINCIPAL
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.util.SinAcentos
import mx.arquidiocesis.eamxcommonutils.util.SinEspaciosSinAcentos
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.imagen.FunImagen
import mx.arquidiocesis.eamxcommonutils.util.permission.UtilValidPermission
import mx.arquidiocesis.eamxmaps.PublicMaps

class EAMXMapFragment : FragmentBase() {
    var map = MutableLiveData<GoogleMap>()
    var maker = MutableLiveData<Marker>()
    var publicMaps = PublicMaps(map, maker)
    var mapView: View? = null
    private var busqueda = false
    private var firstLocation = true
    private var iniciarEdit = true
    lateinit var binding: FragmentEamxMapBinding
    lateinit var mapFragment: SupportMapFragment
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
    private var isLocatio = true
    private var isPrincipal = false
    private var LOCATIONP = 100

    var location = MutableLiveData<Location>()

    companion object {
        @JvmStatic
        fun newInstance(): EAMXMapFragment {
            return EAMXMapFragment()
        }
    }

    private fun initObservers() {
        viewModel.getCommunitiesByNameResponse.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                val arrayString: MutableList<String> = mutableListOf()
                binding.apply {
                    map.value?.clear()
                    var isFirst = true
                    it.forEach {
                        if (!it.latitude.isNullOrEmpty() && !it.longitude.isNullOrEmpty()) {
                            if (it.latitude.toDouble() != 0.0 && it.longitude.toDouble() != 0.0) {
                                val item = CommunityMapModel(
                                    it.id,
                                    it.imgUrl,
                                    it.latitude,
                                    it.longitude,
                                    it.name,
                                    it.address
                                )
                                if (iniciarEdit) {
                                    arrayString.add("${it.name}\n${it.address}".SinAcentos())
                                }
                                addMarker(item, isFirst)
                                isFirst = false
                            }
                        }
                    }
                    if (iniciarEdit) {
                        var adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1, arrayString
                        )
                        binding.svBusarComunidad.setAdapter(adapter)
                        iniciarEdit = false
                    }

                }

            }
            hideLoader()
        }
        viewModel.getCommunitiesByLocationsResponse.observe(viewLifecycleOwner) {
            map.value?.clear()
            var isFirst = true
            if (!it.isNullOrEmpty()) {
                it.forEach {
                    if (!it.latitude.isNullOrEmpty() && !it.longitude.isNullOrEmpty()) {
                        if (it.latitude.toDouble() != 0.0 && it.longitude.toDouble() != 0.0) {
                            val item = CommunityMapModel(
                                it.id,
                                it.imgUrl,
                                it.latitude,
                                it.longitude,
                                it.name,
                                it.address
                            )
                            addMarker(item, isFirst)
                            isFirst = false
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
                .setTitle("Aviso")
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
        }
        viewModel.getLocation()
        viewModel.locationResponse.observe(viewLifecycleOwner) {
            hideLoader()
            if (firstLocation) {
                moveMap(it.latitude, it.longitude)
                firstLocation = false
            } else if (it != null) {
                if (isLocatio) {
                    isLocatio = false
                    location.value = it
                    mapFragment = childFragmentManager.findFragmentById(R.id.mapCommunity) as SupportMapFragment
                    mapView = mapFragment.view
                    // Get the button view
                    val locationButton =
                        (mapView?.findViewById<View>("1".toInt())
                            ?.getParent() as View).findViewById<View>("2".toInt())
                    // and next place it, on bottom right (as Google Maps app)
                    val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
                    // position on right bottom
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
                    layoutParams.setMargins(0, 0, 50, 330)
                    mapFragment.getMapAsync(publicMaps)
                }
            } else {
                hideLoader()
                UtilAlert.Builder()
                    .setTitle("Aviso")
                    .setMessage("Dirección no valida")
                    .build()
                    .show(childFragmentManager, tag)
            }
        }
        map.observe(viewLifecycleOwner) {
            //moveMap(location.value!!.latitude, location.value!!.longitude)
            it.setOnMarkerClickListener(publicMaps)
            /*  viewModel.getCommunitiesByLocations(
                  location.value!!.latitude.toString(),
                  location.value!!.longitude.toString()
              )*/
            viewModel.getCommunitiesByName("")

            it.setOnInfoWindowClickListener {
                it.hideInfoWindow()
                (it.tag as CommunityMapModel).id?.let { it1 -> changeFragmen(it1) }
                //listener(it.tag as IgleciasModel)
            }

            showLoader()
        }
        maker.observe(viewLifecycleOwner) {
            it.showInfoWindow()
        }
    }

    fun moveMap(lat: Double, lng: Double) {
        val zoom = 14f
        val centerMap = LatLng(lat, lng)
        map.value?.animateCamera(CameraUpdateFactory.newLatLngZoom(centerMap, zoom))
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_eamx_map,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment =
            childFragmentManager.findFragmentById(R.id.mapCommunity) as SupportMapFragment
        initObservers()
        isPrincipal = requireArguments().getBoolean(PRINCIPAL)

        if (UtilValidPermission().validListPermissionsAndBuildRequest(
                this@EAMXMapFragment,
                arrayListOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), LOCATIONP
            )
        ) {
            viewModel.getLocation()
            showLoader()
        }

        binding.apply {
            svBusarComunidad.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    showLoader()
                    busqueda = true
                    viewModel.getCommunitiesByName(
                        svBusarComunidad.text.toString().substringBefore("\n").trimEnd()
                    )
                }
            ibSearch.setOnClickListener {
                showLoader()
                busqueda = true
                viewModel.getCommunitiesByName(svBusarComunidad.text.toString().trimEnd())
            }


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
                    viewModel.getLocation()
                    showLoader()
                }

            }
        }
    }

    private fun addMarker(item: CommunityMapModel, isFirst: Boolean) {
        if (isFirst) {
            moveMap(
                item.latitude.toDouble(),
                item.longitude.toDouble()
            )
        }
        if (busqueda) {
            moveMap(
                item.latitude.toDouble(),
                item.longitude.toDouble()
            )
            busqueda = false
        }
        val centerMark =
            LatLng(
                item.latitude.toDouble(),
                item.longitude.toDouble()
            )
        val markerOptions = MarkerOptions()
        markerOptions.position(centerMark)
        markerOptions.title(item.name)
        val bitmapDraw = context?.applicationContext?.let {
            ContextCompat.getDrawable(
                it,
                mx.arquidiocesis.eamxmaps.R.drawable.ic_church_map
            )
        } as BitmapDrawable
        val smallMarker =
            Bitmap.createScaledBitmap(bitmapDraw.bitmap, 80, 100, false)
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
        val marker = map.value?.addMarker(markerOptions)
        marker?.tag = item
        FunImagen().descargaImagen(
            requireContext(),
            item.imgUrl!!,
            item.id.toString()
        )
        map.value?.setInfoWindowAdapter(CustomInfoWindowCommunity(requireContext()))
        if ((item.name + item.address).SinEspaciosSinAcentos() == binding.svBusarComunidad.text.toString()
                .SinEspaciosSinAcentos()
        ) {
            marker?.showInfoWindow()
        }
    }

    private fun changeFragmen(id: Int?) {
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