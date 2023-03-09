package mx.arquidiocesis.eamxmaps.ui

import android.Manifest
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import mx.arquidiocesis.eamxcommonutils.util.*
import mx.arquidiocesis.eamxcommonutils.util.imagen.FunImagen
import mx.arquidiocesis.eamxcommonutils.util.permission.UtilValidPermission
import mx.arquidiocesis.eamxmaps.PublicMaps
import mx.arquidiocesis.eamxmaps.R
import mx.arquidiocesis.eamxmaps.adapters.CustomInfoWindowGoogleMap
import mx.arquidiocesis.eamxmaps.databinding.FragmentMapBinding
import mx.arquidiocesis.eamxmaps.model.IgleciasModel
import mx.arquidiocesis.eamxmaps.repository.Repository
import mx.arquidiocesis.eamxmaps.utils.base.FragmentMapBase
import mx.arquidiocesis.eamxmaps.viewmodel.MapViewModel
import java.text.Normalizer

class MapFragment constructor(
    val isLocation: Boolean = false,
    val listener: (IgleciasModel, Location?) -> Unit,
) : FragmentMapBase() {
    var map = MutableLiveData<GoogleMap>()
    var maker = MutableLiveData<Marker>()
    var publicMaps = PublicMaps(map, maker)
    private var iniciarEdit = true
    private val viewModel: MapViewModel by lazy {
        getViewModel {
            MapViewModel(Repository(requireContext()))
        }
    }
    private var location: Location? = null
    private var busqueda = false
    private var firstLocation = true
    private var LOCATIONP = 100
    private var first = true
    private lateinit var binding: FragmentMapBinding
    var mapView: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "MiIglesia_MapaIglesias")
            })
        }
        isCancelable = true
        showLoader()
        setMapa()
        binding.ibBuscarMap.setOnClickListener {
            showLoader()
            busqueda = true
            viewModel.getiglesiasList(binding.etBusarMap.text.toString().trimEnd())
        }
        binding.etBusarMap.setOnKeyListener { view, i, keyEvent ->
            binding.etBusarMap.setText(binding.etBusarMap.text.toString().SinAcentos())
            val textLength: Int = binding.etBusarMap.getText().length
            binding.etBusarMap.setSelection(textLength, textLength)
            false
        }
        binding.etBusarMap.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                showLoader()
                busqueda = true
                viewModel.getiglesiasList(
                    binding.etBusarMap.text.toString().substringBefore("\n").trimEnd()
                )
            }
        initObservers()
    }

    fun initObservers() {
        viewModel.response.observe(viewLifecycleOwner) {
            map.value?.clear()
            if (it.isNotEmpty()) {
                val arrayString: MutableList<String> = mutableListOf()
                if (busqueda) {
                    moveMap(
                        it[0].latitude.toDouble(),
                        it[0].longitude.toDouble()
                    )
                    busqueda = false
                }
                it.forEach { igleciasModel ->
                    if (!igleciasModel.latitude.isNullOrEmpty() && !igleciasModel.longitude.isNullOrEmpty()) {
                        if (igleciasModel.latitude.toDouble() != 0.0 && igleciasModel.longitude.toDouble() != 0.0) {
                            val centerMark =
                                LatLng(
                                    igleciasModel.latitude.toDouble(),
                                    igleciasModel.longitude.toDouble()
                                )
                            if (iniciarEdit) {
                                "${igleciasModel.name}\n${igleciasModel.address}".SinAcentos()
                                    .let { it1 ->
                                        arrayString.add(
                                            it1
                                        )
                                    }
                            }
                            val markerOptions = MarkerOptions()
                            markerOptions.position(centerMark)
                            markerOptions.title(igleciasModel.name)
                            val bitmapDraw = context?.applicationContext?.let {
                                ContextCompat.getDrawable(
                                    it,
                                    R.drawable.ic_church_map
                                )
                            } as BitmapDrawable
                            val smallMarker = Bitmap.createBitmap(bitmapDraw.bitmap)
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                            val marker = map.value?.addMarker(markerOptions)
                            marker?.tag = igleciasModel
                            FunImagen().descargaImagen(
                                requireContext(),
                                igleciasModel.imageUrl,
                                igleciasModel.id.toString()
                            )
                            map.value?.setInfoWindowAdapter(
                                CustomInfoWindowGoogleMap(
                                    requireContext()
                                )
                            )
                            if ((igleciasModel.name + igleciasModel.address).SinEspaciosSinAcentos() == binding.etBusarMap.text.toString()
                                    .SinEspaciosSinAcentos()
                            ) {
                                marker?.showInfoWindow()
                            }
                        }
                    }
                }
                if (iniciarEdit) {
                    var adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1, arrayString
                    )
                    binding.etBusarMap.setAdapter(adapter)
                    iniciarEdit = false
                }
            }
            hideLoader()
            if (isLocation) {
                permisoLocation()
            }
        }
        viewModel.locationResponse.observe(viewLifecycleOwner) {
            hideLoader()
            if (firstLocation) {
                location = it
                moveMap(location!!.latitude, location!!.longitude)
                firstLocation = false
            }
        }
        viewModel.errorResponse.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            hideLoader()
        }
        map.observe(viewLifecycleOwner) {
            if (first) {
                //moveMap(19.362028, -99.166414)
                viewModel.getiglesiasList()
                first = false
            }
            it.setOnMarkerClickListener(publicMaps)
            it.setOnInfoWindowClickListener {
                it.hideInfoWindow()
                if (isLocation) {
                    listener(it.tag as IgleciasModel, location)
                } else {
                    listener(it.tag as IgleciasModel, null)
                }
            }
        }
        maker.observe(viewLifecycleOwner) {
            it.showInfoWindow()
        }
    }

    private fun moveMap(lat: Double, lng: Double) {
        val zoom = 14f
        val centerMap = LatLng(lat, lng)
        map.value?.animateCamera(CameraUpdateFactory.newLatLngZoom(centerMap, zoom))
    }

    private fun permisoLocation() {
        if (UtilValidPermission().validListPermissionsAndBuildRequest(
                this,
                arrayListOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), LOCATIONP
            )
        ) {
            showLoader()
            viewModel.getLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (UtilValidPermission().allPermissionsAreAgree(grantResults)) {
            when (requestCode) {
                LOCATIONP -> {
                    showLoader()
                    viewModel.getLocation()
                }
            }
        }
    }

    fun setMapa() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
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
}