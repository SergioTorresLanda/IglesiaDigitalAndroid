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
import mx.arquidiocesis.eamxmaps.model.IgleciasModel
import mx.arquidiocesis.eamxprofilemodule.R
import mx.arquidiocesis.eamxprofilemodule.adapter.CustomInfoWindowGoogleMap
import mx.arquidiocesis.eamxprofilemodule.model.ChurchModel
import mx.arquidiocesis.eamxprofilemodule.repository.RepositoryProfile
import mx.arquidiocesis.eamxprofilemodule.viewmodel.MapViewModel

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

        mapFragment.getMapAsync(publicMaps)
        ibBuscarMap.setOnClickListener {
            viewModel.getChurchList(etBusarMap.text.toString())
            showLoader()
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

            churchList = if(!isLaico){
                val temp = churchTempList.filter { item -> item.category == "CHURCH" }
                temp.toMutableList()
            } else{
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
                        arrayString.add("${igleciasModel.name}\n${igleciasModel.address}")
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
                       /* map.value?.setOnMarkerClickListener { p0 ->
                            changeFragment((p0?.tag as ChurchModel))
                            false
                        }*/
                        map.value?.setInfoWindowAdapter(CustomInfoWindowGoogleMap(requireContext()))
                        ubicacioList.add(markerOptions)
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
        //dismiss()
        UtilAlert.Builder()
            .setTitle(getString(R.string.title_dialog_warning))
            .setMessage("Se ha seleccionado "+ church.name)
            .setListener {
                listener(church)
                dismiss()
            }
            .build()
            .show(childFragmentManager, tag)
        //listener(church)
        //requireActivity().onBackPressed()
    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }
}