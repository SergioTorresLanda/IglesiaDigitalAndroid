package mx.arquidiocesis.eamxprofilemodule.customviews

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
import kotlinx.android.synthetic.main.fragment_churches_map.*
import mx.arquidiocesis.eamxcommonutils.base.FragmentDialogBase
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxmaps.PublicMaps
import mx.arquidiocesis.eamxprofilemodule.R
import mx.arquidiocesis.eamxprofilemodule.adapter.CustomInfoWindowGoogleMap
import mx.arquidiocesis.eamxprofilemodule.model.ChurchModel
import mx.arquidiocesis.eamxprofilemodule.repository.RepositoryProfile
import mx.arquidiocesis.eamxprofilemodule.viewmodel.MapViewModel
import java.text.Normalizer

class ChurchesMapFragment constructor(
    val isLaico: Boolean = false,
    val listener: (ChurchModel) -> Unit
) : FragmentDialogBase() {
    var map = MutableLiveData<GoogleMap>()
    var maker = MutableLiveData<Marker>()
    var publicMaps = PublicMaps(map, maker)
    private var iniciarEdit = true
    private val viewModel: MapViewModel by lazy {
        getViewModel {
            MapViewModel(RepositoryProfile(requireContext()))
        }
    }
    private var ubicacioList: MutableList<MarkerOptions> = mutableListOf()
    private var churchList: MutableList<ChurchModel> = mutableListOf()
    var mapView: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_churches_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        layoutParams.setMargins(0, 0, 60, 330)
        mapFragment.getMapAsync(publicMaps)
        ibBuscarMap.setOnClickListener {
            viewModel.getChurchList(etBusarMap.text.toString())
            showLoader()
        }
        etBusarMap.setOnKeyListener { view, i, keyEvent ->
            etBusarMap.setText(stripAccents(etBusarMap.text.toString()))
            val textLength: Int = etBusarMap.getText().length
            etBusarMap.setSelection(textLength, textLength)
            false
        }
        etBusarMap.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                showLoader()
                viewModel.getChurchList(
                    etBusarMap.text.toString().substringBefore("\n").trimEnd()
                )
            }
        if (isLaico) {
            btnCommunity.visibility = View.VISIBLE
        }
        btnCommunity.setOnClickListener {
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage("Por lo visto tu comunidad no está en nuestra base de datos. En cuanto guardes tu perfil, te aparecerá un formulario donde podrás registrar tu comunidad")
                .setListener {
                    listener(ChurchModel(0, "", "", "", "", ""))
                    dismiss()
                }
                .build()
                .show(childFragmentManager, tag)
        }
        initObservers()
    }

    fun initObservers() {
        viewModel.response.observe(viewLifecycleOwner) {
            val churchTempList = it as MutableList<ChurchModel>
            churchList = if (!isLaico) {
                val temp = churchTempList.filter { item -> item.category == "CHURCH" }
                temp.toMutableList()
            } else {
                churchTempList
            }
            map.value?.clear()
            if (churchList.isNotEmpty()) {
                val arrayString: MutableList<String> = mutableListOf()
                churchList[0].latitude?.toDouble()?.let { it1 ->
                    churchList[0].longitude?.toDouble()?.let { it2 ->
                        moveMap(
                            it1,
                            it2
                        )
                    }
                }
                churchList.forEach { igleciasModel ->
                    val centerMark =
                        igleciasModel.latitude?.toDouble()?.let { it1 ->
                            igleciasModel.longitude?.toDouble()?.let { it2 ->
                                LatLng(
                                    it1,
                                    it2
                                )
                            }
                        }
                    if (iniciarEdit) {
                        stripAccents("${igleciasModel.name}\n${igleciasModel.address}").let { it1 ->
                            arrayString.add(
                                it1
                            )
                        }
                    }

                    val markerOptions = MarkerOptions()
                    if (centerMark != null)
                        markerOptions.position(centerMark)
                    markerOptions.title(igleciasModel.name)
                    val bitmapDraw = context?.applicationContext?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.ic_church_map
                        )
                    } as BitmapDrawable
                    val smallMarker = Bitmap.createScaledBitmap(bitmapDraw.bitmap, 80, 100, false)
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))

                    if (igleciasModel.latitude != null && igleciasModel.longitude != null) {
                        val marker = map.value?.addMarker(markerOptions)
                        marker?.tag = igleciasModel
                        map.value?.setInfoWindowAdapter(CustomInfoWindowGoogleMap(requireContext()))
                        ubicacioList.add(markerOptions)
                        if (quitarEspacios(stripAccents(igleciasModel.name + igleciasModel.address)) == quitarEspacios(
                                etBusarMap.text.toString()
                            )
                        ) {
                            marker?.showInfoWindow()
                        }
                    }
                }
                if (iniciarEdit) {
                    var adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1, arrayString
                    )
                    etBusarMap.setAdapter(adapter)
                    iniciarEdit = false
                }
            }
            hideLoader()

        }
        viewModel.errorResponse.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            hideLoader()
        }
        map.observe(viewLifecycleOwner) {
            moveMap(19.362028, -99.166414)
            it.setOnMarkerClickListener(publicMaps)
            if (isLaico) {
                viewModel.getCommunitiesByName("")
            } else {
                viewModel.getChurchList()
            }
            it.setOnInfoWindowClickListener {
                it.hideInfoWindow()
                changeFragment((it?.tag as ChurchModel))
                false
            }
            showLoader()
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

    private fun changeFragment(church: ChurchModel) {
        UtilAlert.Builder()
            .setTitle(getString(R.string.title_dialog_warning))
            .setMessage("Se ha seleccionado " + church.name)
            .setListener {
                listener(church)
                dismiss()
            }
            .build()
            .show(childFragmentManager, tag)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
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

    private fun quitarEspacios(s: String): String {
        return s.replace("\\s".toRegex(), "");
    }
}