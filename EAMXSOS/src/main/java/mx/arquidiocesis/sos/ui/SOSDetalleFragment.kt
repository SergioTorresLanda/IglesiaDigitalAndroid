package mx.arquidiocesis.sos.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.upax.eamxsos.R
import com.upax.eamxsos.databinding.FragmentDetalleBinding
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.EAMXDiferenciaHoras
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxmaps.PublicMaps
import mx.arquidiocesis.sos.repository.SOSRepository
import mx.arquidiocesis.sos.utils.Constants
import mx.arquidiocesis.sos.viewmodel.SOSServicesFaithfulViewModel
import java.io.File


class SOSDetalleFragment : FragmentBase() {

    var map = MutableLiveData<GoogleMap>()
    var maker = MutableLiveData<Marker>()
    var publicMaps = PublicMaps(map, maker)

    lateinit var mapFragment: SupportMapFragment
    lateinit var fileImage: File

    companion object {
        fun newInstance(): SOSDetalleFragment {
            val fragment = SOSDetalleFragment()
            return fragment
        }
    }

    private val notificationViewModel: SOSServicesFaithfulViewModel by lazy {
        getViewModel {
            SOSServicesFaithfulViewModel(SOSRepository(requireContext()))
        }
    }
    lateinit var binding: FragmentDetalleBinding
    private var idService = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadView()
        initObservers()

    }

    private fun loadView() {
        //   if(fromSOS){
        if (arguments != null &&
            arguments?.get(ID) != null
        ) {//Se agrego arguments
            idService = requireArguments().getInt(ID)
            loadCounter()
        }


    }

    private fun loadCounter() {
        showLoader()
        mapFragment =
            childFragmentManager.findFragmentById(R.id.mapSOS) as SupportMapFragment
        mapFragment.getMapAsync(publicMaps)
        binding.tvSOSCompartir.setOnClickListener {
            compartir()
        }

    }


    private fun initObservers() {

        notificationViewModel.serviceDetalle.observe(viewLifecycleOwner) {
            binding.apply {
                tvTitulo.text = it.service.name
                tvParroco.text = it.devotee.name
                tvDistancia.text = it.location.distance.toString()
                tvDirrecion.text = it.address.description
                tvTelefono.text = it.devotee.phone
                if (it.review != null) {
                    if (!it.review.review_value.isNullOrEmpty()) {
                        tvComentario.text = it.review.review_value
                    }
                    if (!it.review.rating.isNullOrEmpty()) {
                        rbSOS.rating = it.review.rating.toFloat()
                    }
                }
                publicMaps.addMaker(it.address.latitude.toDouble(), it.address.longitude.toDouble())


                val servicesPriest = it.progress_history.last()
                var estado = ""
                if (!servicesPriest.sub_status.isNullOrEmpty()) {
                    when (servicesPriest.sub_status) {
                        Constants.SubStatus.PENDING_CONFIRMATION -> {
                            estado = "Por aceptar"
                        }
                        Constants.SubStatus.CALL_WAITING -> {
                            estado = "Aceptada  \n" +
                                    "Estamos buscando a un celebrante"
                        }
                        Constants.SubStatus.CALL_FINISHED -> {
                            estado = "Aceptada  \n" +
                                    "Llamada realizada"
                        }
                        Constants.SubStatus.LOOKING_FOR_ASSISTANCE -> {
                            estado = "Si se realizará el servicio"
                        }
                        Constants.SubStatus.HELP_ON_THE_WAY -> {
                            estado = "Ayuda en camino"

                        }
                        Constants.SubStatus.SUCCESSFULLY -> {
                            if(!it.modification_date.isNullOrEmpty()){
                                tiempo(it.modification_date)
                            }
                            estado = "Con éxito"
                        }
                        Constants.SubStatus.UNSUCCESSFULLY -> {
                            if(!it.modification_date.isNullOrEmpty()){
                                tiempo(it.modification_date)
                            }
                            estado = "Sin éxito"
                        }
                    }
                } else {
                    estado = servicesPriest.status
                }
                tvEstado.text = estado

                notificationViewModel.createImage(it, estado)
            }

            hideLoader()

        }
        map.observe(viewLifecycleOwner) {
            notificationViewModel.getServices(idService)
            it.setOnMarkerClickListener(publicMaps)
        }
        maker.observe(viewLifecycleOwner) {
            // it.showInfoWindow()
        }
        notificationViewModel.imagen.observe(viewLifecycleOwner) {
            fileImage = it
        }
        notificationViewModel.errorResponse.observe(viewLifecycleOwner)
        {
            hideLoader()
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
        }
    }
    fun tiempo(fecha:String){
        binding.tvNumComnetarios.text=EAMXDiferenciaHoras().main(fecha)

    }

    fun compartir() {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            val uri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().applicationContext.packageName + ".provider",
                fileImage
            )

            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/*"

            putExtra(
                Intent.EXTRA_TEXT, "Ubicacion \n" +
                        "https://www.google.com.mx/maps?q=" + (maker.value?.position?.latitude
                    ?: 0.0) + "," + (maker.value?.position?.longitude
                    ?: 0.0)
            )
            // type="text/*"

        }
        startActivity(Intent.createChooser(shareIntent, "Compartir"))
        // Iniciamos la Actividad (Activity) con la lógica de nuestro proyecto

    }


}