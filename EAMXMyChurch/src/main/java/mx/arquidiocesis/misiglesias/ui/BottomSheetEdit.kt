package mx.arquidiocesis.misiglesias.ui

import android.Manifest
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.InputType.TYPE_CLASS_PHONE
import android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.customui.loader.UtilLoader
import mx.arquidiocesis.eamxcommonutils.util.permission.UtilValidPermission
import mx.arquidiocesis.eamxmaps.PublicMaps
import mx.arquidiocesis.misiglesias.R
import mx.arquidiocesis.misiglesias.databinding.FragmentEditChurchBinding
import mx.arquidiocesis.misiglesias.model.*
import mx.arquidiocesis.misiglesias.utils.PublicFunctions
import mx.arquidiocesis.misiglesias.viewmodel.EditarIglesiaViewModel
import java.io.IOException

open class BottomSheetEdit(
    val viewModel: EditarIglesiaViewModel,
    var church: MutableLiveData<ChurchDetaillModel>
) :
    BottomSheetDialogFragment() {
    var map = MutableLiveData<GoogleMap>()
    var maker = MutableLiveData<Marker>()
    var client = MutableLiveData<GoogleApiClient>()
    var publicMaps = PublicMaps(map, maker,true)
    private val loader by lazy { UtilLoader() }

    lateinit var binding: FragmentEditChurchBinding

    var location = MutableLiveData<Location>()
    lateinit var mapFragment: SupportMapFragment
    var ubicacionInicial = true
    lateinit var model: ChurchDetaillModel
    var idPrincipal = 0
    var listP = listOf<PriestModel>()
    private var LOCATIONP = 100

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEditChurchBinding.inflate(inflater, container, false)
        model = church.value!!

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment =
            childFragmentManager.findFragmentById(R.id.mapEdit) as SupportMapFragment

        if (UtilValidPermission().validListPermissionsAndBuildRequest(
                this@BottomSheetEdit,
                arrayListOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), LOCATIONP
            )
        ) {
            iniciar()
        }
    }

    private fun iniciar() {
        publicMaps.buildGoogleApiClient(client, requireContext())
        showLoader()
        binding.apply {
            iNom.apply {
                tvItemTitulo.text = "Nombre de la Iglesia"
                etItemEdit.isFocusable = false
                etItemEdit.isEnabled = false
                if (model.name.isNullOrEmpty()) {
                    etItemEdit.hint = "Parroquia del Señor de la Santísima Resurrección"
                } else {
                    etItemEdit.setText(model.name)
                }

            }
            iPar.apply {
                tvItemTitulo.text = "Párroco o Responsable"
                PublicFunctions().maxminEdit(etItemEdit,100)
                if (model.principal?.name.isNullOrEmpty()) {
                    etItemEdit.hint = "Pbro Juan José López Rodríguez"
                } else {
                    etItemEdit.setText(model.principal?.name)
                    idPrincipal = model.principal!!.id
                }
                etItemEdit.isEnabled = false
                /*etItemEdit.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        viewModel.getpriests(s.toString())
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                    }
                })*/
            }
            iDir.apply {
                tvItemTitulo.text = "Dirección"
                if (model.address.isNullOrEmpty()) {
                    etItemEdit.queryHint =
                        "Bosques de la Reforma 486, Bosques de  las Lomas, Miguel Hidalgo, 11700,  Ciudad de México, CDMX"
                } else {
                    etItemEdit.setQuery(model.address, true)
                }
                etItemEdit.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                    androidx.appcompat.widget.SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (etItemEdit.query.isNotEmpty()) {
                            searchLocation(etItemEdit.query.toString())
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
            //map
            iHor.apply {
                etHoras.isFocusable = false
                etDias.isFocusable = false
                var dias = MutableLiveData<MutableList<Day>>()
                var hora = MutableLiveData<String>()
                var first = true
                if (!model.schedules.isNullOrEmpty()) {
                    val item = model.schedules!!.first()
                    etDias.setText(PublicFunctions().obtenerDias(item.days))
                    etHoras.setText("${item.hour_start} a ${item.hour_end}")
                } else {
                    model.schedules = PublicFunctions().getHoraryList()
                }
                etDias.setOnClickListener {
                    dias.value = model.schedules!!.first().days as MutableList<Day>
                    getDays(dias)
                }
                ivCalendario.setOnClickListener {
                    dias.value = model.schedules!!.first().days as MutableList<Day>
                    getDays(dias)
                }
                etHoras.setOnClickListener {
                    first = true
                    getHora(hora)
                }
                ivRelog.setOnClickListener {
                    first = true
                    getHora(hora)
                }
                dias.observe(viewLifecycleOwner, Observer {
                    model.schedules!!.first().days = it
                    etDias.setText(PublicFunctions().obtenerDias(it))
                })
                hora.observe(viewLifecycleOwner, Observer {

                    if (first) {
                        model.schedules!!.first().hour_start = it
                        getHora(hora)
                        first = false
                    } else {
                        model.schedules!!.first().hour_end = it
                        etHoras.setText("${model.schedules!!.first().hour_start} a ${model.schedules!!.first().hour_end} hrs")

                    }
                })

            }
            iOfi.apply {
                tvItemTitulo.text = "Horarios de oficina"
                etHoras.isFocusable = false
                etDias.isFocusable = false
                var dias = MutableLiveData<MutableList<Day>>()
                var hora = MutableLiveData<String>()
                var first = true
                if (!model.attention.isNullOrEmpty()) {
                    val item = model.attention!!.first()
                    etDias.setText(PublicFunctions().obtenerDias(item.days))
                    etHoras.setText("${item.hour_start} a ${item.hour_end}")
                } else {
                    model.attention = PublicFunctions().getHoraryList()
                }
                etDias.setOnClickListener {

                    dias.value = model.attention!!.first().days as MutableList<Day>

                    getDays(dias)
                }
                ivCalendario.setOnClickListener {
                    dias.value = model.attention!!.first().days as MutableList<Day>
                    getDays(dias)
                }
                etHoras.setOnClickListener {

                    first = true
                    getHora(hora)
                }
                ivRelog.setOnClickListener {

                    first = true
                    getHora(hora)
                }
                dias.observe(viewLifecycleOwner, Observer {
                    model.attention!!.first().days = it
                    etDias.setText(PublicFunctions().obtenerDias(it))
                })
                hora.observe(viewLifecycleOwner, Observer {
                    if (first) {
                        model.attention!!.first().hour_start = it
                        getHora(hora)
                        first = false
                    } else {
                        model.attention!!.first().hour_end = it
                        etHoras.setText("${model.attention!!.first().hour_start} a ${model.attention!!.first().hour_end} hrs")
                    }
                })
            }
            iEma.apply {
                tvItemTitulo.text = "E-mail"
                etItemEdit.inputType = TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                PublicFunctions().maxminEdit(etItemEdit,60)
                if (model.email.isNullOrEmpty()) {
                    etItemEdit.hint = "mperez@gmail.com"
                } else {
                    etItemEdit.setText(model.email)
                }
            }
            iTel.apply {
                tvItemTitulo.text = "Teléfono"
                etItemEdit.inputType = TYPE_CLASS_PHONE
                PublicFunctions().maxminEdit(etItemEdit,10)
                if (model.phone.isNullOrEmpty()) {
                    etItemEdit.hint = "55 5555 5555"
                } else {
                    etItemEdit.setText(model.phone)
                }
            }
            btnListo.setOnClickListener {
                var vacio = false

                vacio = PublicFunctions().validarVacios(iPar.etItemEdit)
                if (!vacio) {
                    vacio = PublicFunctions().validarVacios(iEma.etItemEdit)
                    if (!vacio) {
                        vacio = PublicFunctions().validarVacios(iTel.etItemEdit)
                        if (!vacio) {
                            vacio = PublicFunctions().validarVacios(iDir.etItemEdit)
                            if (!vacio) {
                                listP.forEach {
                                    if (it.name == iPar.etItemEdit.text.toString()) {
                                        idPrincipal = it.id
                                    }
                                }
                            }
                        }
                    }
                }
                if (!vacio) {
                    if (idPrincipal != 0) {
                        model.name = iNom.etItemEdit.text.toString()
                        //deve ser dropdo
                        model.principal =
                            PrincipalModel(iPar.etItemEdit.text.toString(), idPrincipal)

                        model.address = iDir.etItemEdit.query.toString()
                        model.latitude = maker.value!!.position.latitude.toString()
                        model.longitude = maker.value!!.position.longitude.toString()

                        model.email = iEma.etItemEdit.text.toString()
                        model.phone = iTel.etItemEdit.text.toString()
                        church.value = model
                        dismiss()
                    } else {
                        error("Selecione un padre valido")
                    }
                } else {
                    error("llene todos los campos")
                }


            }
            viewModel.getLocation()
            initObservers()
        }
    }

    fun initObservers() {

        viewModel.locationResponse.observe(viewLifecycleOwner, Observer {
            if (ubicacionInicial) {
                if (it != null) {
                    location.value = it
                    mapFragment.getMapAsync(publicMaps)
                    ubicacionInicial = false

                } else {
                    //hideLoader()
                    UtilAlert.Builder()
                        .setTitle("Aviso")
                        .setMessage("Dirección no valida")
                        .build()
                        .show(childFragmentManager, tag)
                }
            }

        })
        viewModel.reponsePrients.observe(viewLifecycleOwner, Observer {
            listP = it
            binding.apply {
                val list: ArrayList<String> = ArrayList()
                it.forEach {
                    list.add(it.name)
                }
                val adapterSpinner = ArrayAdapter(
                    requireContext(),
                    R.layout.custom_spinner_item, list
                )
                iPar.etItemEdit.setAdapter(adapterSpinner)

            }

        })
        client.observe(viewLifecycleOwner, Observer {
            it.connect()
        })
        map.observe(viewLifecycleOwner, Observer {
            it.setOnMarkerClickListener(publicMaps)
            if(!model.latitude.isNullOrEmpty()&&!model.longitude.isNullOrEmpty()){
                publicMaps.addMaker(model.latitude!!.toDouble(),model.longitude!!.toDouble())
            }else{
                publicMaps.addMaker(location.value!!.latitude, location.value!!.longitude)

            }
            it.setOnMapClickListener {
                publicMaps.moveMarker(it.latitude, it.longitude)
            }
            hidenLoader()
            // it.setOnMarkerDragListener(publicMaps)

            //  map.value?.setOnMarkerDragListener()
            //hideLoader()
            //viewModel.getiglesiasList()
        })
        maker.observe(viewLifecycleOwner, Observer {
            // it.showInfoWindow()
        })

        viewModel.errorResponse.observe(viewLifecycleOwner, Observer {
            //hideLoader()
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
        })
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

    fun getDays(dias: MutableLiveData<MutableList<Day>>) {
        PublicFunctions().selectDayRange(requireContext(), dias)
    }


    fun getHora(hora: MutableLiveData<String>) {
        PublicFunctions().selectFirstHour(requireContext(), hora)
    }

    fun searchLocation(location: String) {
        var addressList: List<Address>? = null
        if (location == null || location == "") {
            Toast.makeText(requireContext(), "provide location", Toast.LENGTH_SHORT).show()
        } else {
            val geoCoder = Geocoder(requireContext())
            try {
                addressList = geoCoder.getFromLocationName(location, 1)

            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (!addressList?.isEmpty()!!) {
                val address = addressList!![0]
                publicMaps.moveMarker(address.latitude, address.longitude)
            } else {
                //   (requireParentFragment() as FragmentBase).hideLoader()
                UtilAlert
                    .Builder()
                    .setTitle("Aviso")
                    .setMessage("Dirección no valida")
                    .build()
                    .show(childFragmentManager, "")
            }

        }
    }

    fun showLoader() {
        loader.show(childFragmentManager, "")
    }

    fun hidenLoader() {
        loader.dismissAllowingStateLoss()
    }

    fun agregada(mensaje: String) {

        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    fun error(mensaje: String) {
        UtilAlert
            .Builder()
            .setTitle("Aviso")
            .setMessage(mensaje)
            .build()
            .show(childFragmentManager, "")
    }

}