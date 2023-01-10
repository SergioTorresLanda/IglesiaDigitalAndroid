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
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
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

class MapFragment(
    val isLocation: Boolean = false,
    val listener: (IgleciasModel, Location?) -> Unit
) :
    FragmentMapBase() {
    private val viewModel: MapViewModel by lazy {
        getViewModel {
            MapViewModel(Repository(requireContext()))
        }
    }
    private var location: Location? = null
    private var LOCATIONP = 100
    private var iniciarEdit = true
    private var busqueda = false
    private var first = true
    private var firstLocation = true
    private lateinit var binding: FragmentMapBinding
    var map = MutableLiveData<GoogleMap>()
    var maker = MutableLiveData<Marker>()
    var publicMaps = PublicMaps(map, maker)
    var mapView: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true
        showLoader()
        setMapa()
        binding.ibBuscarMap.setOnClickListener {
            showLoader()
            busqueda = true
            viewModel.getiglesiasList(binding.etBusarMap.text.toString().trimEnd())
        }
        binding.etBusarMap.setOnKeyListener { view, i, keyEvent ->
            binding.etBusarMap.setText(stripAccents(binding.etBusarMap.text.toString()))
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
                it.forEach { igleciasModel ->
                    if (!igleciasModel.latitude.isNullOrEmpty() && !igleciasModel.longitude.isNullOrEmpty()) {
                        if (igleciasModel.latitude.toDouble() != 0.0 && igleciasModel.longitude.toDouble() != 0.0) {
                            if (busqueda) {
                                moveMap(
                                    igleciasModel.latitude.toDouble(),
                                    igleciasModel.longitude.toDouble()
                                )
                                busqueda = false
                            }
                            if (iniciarEdit) {
                                stripAccents("${igleciasModel.name}\n${igleciasModel.address}").let { it1 ->
                                    arrayString.add(
                                        it1
                                    )
                                }
                            }
                            val centerMark =
                                LatLng(
                                    igleciasModel.latitude.toDouble(),
                                    igleciasModel.longitude.toDouble()
                                )
                            val markerOptions = MarkerOptions()
                            markerOptions.position(centerMark)
                            markerOptions.title(igleciasModel.name)
                            val bitmapDraw = context?.applicationContext?.let {
                                ContextCompat.getDrawable(
                                    it,
                                    R.drawable.ic_church_map
                                )
                            } as BitmapDrawable
                            val smallMarker =
                                Bitmap.createBitmap(bitmapDraw.bitmap)
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                            val marker = map.value?.addMarker(markerOptions)
                            marker?.tag = igleciasModel
                            FunImagen().descargaImagen(
                                requireContext(),
                                igleciasModel.imageUrl,
                                igleciasModel.id.toString()
                            )
                            map.value?.setInfoWindowAdapter(CustomInfoWindowGoogleMap(requireContext()))
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
                moveMap(19.362028, -99.166414)
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
        grantResults: IntArray
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

    private fun stripAccents(s: String): String {
        /*Salvamos las ñ*/
        var s = s
        s = s.replace('ñ', '\u0001')
        s = s.replace('Ñ', '\u0002')
        s = Normalizer.normalize(s, Normalizer.Form.NFD)
        s = s.replace("[\\p{InCombiningDiacriticalMarks}]".toRegex(), "")
        /*Volvemos las ñ a la cadena*/s = s.replace('\u0001', 'ñ')
        s = s.replace('\u0002', 'Ñ')
        return s
    }
}