package mx.arquidiocesis.eamxcommonutils.multimedia

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import mx.arquidiocesis.eamxcommonutils.Model.Municipalities
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.base.FragmentDialogBase
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentMapsBinding
import mx.arquidiocesis.eamxcommonutils.util.isUrlYoutube
import mx.arquidiocesis.eamxcommonutils.util.log
import mx.arquidiocesis.eamxcommonutils.util.permission.UtilValidPermission
import mx.arquidiocesis.eamxcommonutils.util.search

const val PERMISSION_LOCATION = 10007

class MapsFragment(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val listener: (rlatitude: Double, rlongitude: Double, raddress: String, municipality: Int) -> Unit,
) : FragmentDialogBase() {
    lateinit var binding: FragmentMapsBinding

    // Ubicación de prueba
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    // Variable para gestionar
    var map = MutableLiveData<GoogleMap>()
    var maker = MutableLiveData<Marker>()
    var client = MutableLiveData<GoogleApiClient>()

    // Gestíon de acciones de google maps
    var maps = Maps(map, maker, true)

    // Datos que serán devueltos
    var rlatitude: Double = 0.0
    var rlongitude: Double = 0.0
    var raddress: String = ""
    var municipality: Int = 0

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        rlatitude = latitude
        rlongitude = longitude
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (chechPermissions()) {
            // Se hace referencia al fragment para ser usadado con binding
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            // Se genera el mapa
            mapFragment?.getMapAsync(maps)
            // Se agrega botón para posicionarse en la ubicación actual
            addButtonMyUbication(mapFragment)
            //Se crea el cliente
            maps.buildGoogleApiClient(client, requireContext())
            //##binding.tvAddress.text = maps.addressMap.value.toString()

            //Inicia el observador
            //Se define el pin
            val bitmapDraw = requireContext().let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_map
                )
            } as BitmapDrawable
            val smallMarker = Bitmap.createScaledBitmap(bitmapDraw.bitmap, 80, 100, false)
            val pin = BitmapDescriptorFactory.fromBitmap(smallMarker)
            // Observa que el mapa esta cargado correctamente
            //maps.map.observe(viewLifecycleOwner) {
            var mover = false
            map.observe(viewLifecycleOwner) {
                map.value?.apply {
                    // Evento al presionar el mapa
                    /*setOnMapClickListener {
                        maps.addMaker(it.latitude, it.longitude, false, pin)
                        rlatitude = it.latitude
                        rlongitude = it.longitude
                    }*/
                    // Evento al mover el mapa
                    setOnCameraChangeListener {
                        if (mover) {
                            maps.addMaker(it.target.latitude, it.target.longitude, false, pin)
                            rlatitude = it.target.latitude
                            rlongitude = it.target.longitude
                        }
                        mover = true
                    }
                }
                // Observa si la dirección tiene un cambio
                maps.addressMap.observe(viewLifecycleOwner) {
                    binding.tvAddress.text = it
                    raddress = it
                }

                // Validar y obtener, localización actual
                var lastKnownLocation: Location? = null
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                    } else {
                        maps.map.value!!.isMyLocationEnabled = false
                        map.value!!.isMyLocationEnabled = false
                    }
                    if (rlatitude != 0.0 && rlongitude != 0.0) {
                        maps.addMaker(rlatitude, rlongitude, true, pin)
                    } else {
                        if (lastKnownLocation != null) {
                            maps.addMaker(
                                lastKnownLocation!!.latitude,
                                lastKnownLocation!!.longitude,
                                true,
                                pin
                            )
                        }
                    }
                }
            }
            // Evento del botón para guardar ubicación
            binding.bContinue.setOnClickListener {
                municipality = 0
                Municipalities.values().forEach {
                    if (it.del.search(raddress)) {
                        municipality = it.pos
                    }
                }
                if (municipality == 0) {
                    UtilAlert.Builder()
                        .setTitle(getString(R.string.title_dialog_warning))
                        .setMessage("Dirección no valida, debe de ser más precisa")
                        .setListener {
                            listener(rlatitude, rlongitude, raddress, municipality)
                            dismiss()
                        }
                        .build()
                        .show(childFragmentManager, tag)
                } else {
                    UtilAlert.Builder()
                        .setTitle(getString(R.string.title_dialog_warning))
                        .setMessage("Se ha seleccionado una dirección.")
                        .setListener {
                            listener(rlatitude, rlongitude, raddress, municipality)
                            dismiss()
                        }
                        .build()
                        .show(childFragmentManager, tag)
                }
            }
        } else {
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage("Debe de activar los permisos")
                .setListener {
                    listener(rlatitude, rlongitude, raddress, municipality)
                    dismiss()
                }
                .build()
                .show(childFragmentManager, tag)
        }
    }

    protected fun addButtonMyUbication(mapFragment: SupportMapFragment?) {
        var mapView: View? = mapFragment?.view
        // Get the button view
        val locationButton = (mapView?.findViewById<View>("1".toInt())
            ?.getParent() as View).findViewById<View>("2".toInt())
        // and next place it, on bottom right (as Google Maps app)
        val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
        // position on right bottom
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        layoutParams.setMargins(0, 0, 60, 330)
    }

    private fun handleAddress(): (String) -> Unit = {
        binding.tvAddress.text = it
    }

    private fun chechPermissions(): Boolean {
        return UtilValidPermission().validListPermissionsAndBuildRequest(
            this@MapsFragment, arrayListOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_LOCATION
        )
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }
}