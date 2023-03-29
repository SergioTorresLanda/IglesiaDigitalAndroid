package mx.arquidiocesis.misiglesias.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.loadByUrlIntDrawableerror
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxcommonutils.util.review.ReviewFragment
import mx.arquidiocesis.misiglesias.R
import mx.arquidiocesis.misiglesias.adapters.HorarioAdapter
import mx.arquidiocesis.misiglesias.adapters.ServiceAdapter
import mx.arquidiocesis.misiglesias.database.instance.MeetRoomDataBase
import mx.arquidiocesis.misiglesias.databinding.FragmentEditBinding
import mx.arquidiocesis.misiglesias.model.*
import mx.arquidiocesis.misiglesias.repository.Repository
import mx.arquidiocesis.misiglesias.utils.PublicFunctions
import mx.arquidiocesis.misiglesias.viewmodel.EditarIglesiaViewModel

class EditFragment(val church: ChurchDetaillModel) : FragmentBase() {

    private val viewModel: EditarIglesiaViewModel by lazy {
        getViewModel {
            EditarIglesiaViewModel(
                Repository(
                    requireContext(),
                    MeetRoomDataBase.getRoomInstance(requireActivity())
                )
            )
        }
    }
    lateinit var binding: FragmentEditBinding
    var idIglesia = 0
    var star = 0f
    var model = MutableLiveData<ChurchDetaillModel>()
    private var latitude = ""
    private var longitude = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as EAMXHome).showToolbar(true, AppMyConstants.miIglesia)

        binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (church.id != null) {
            idIglesia = church.id
            model.value = church

            initObservers()
        } else {
            requireActivity().onBackPressed()
        }
        binding.apply {
            btnHeart.setOnClickListener {
                val mBottomSheetFragment = BottomSheetEdit(viewModel, model)
                mBottomSheetFragment.show(
                    requireActivity().supportFragmentManager,
                    "BottomSheetEdit"
                )
            }
            tvAddWeb.setOnClickListener {
                val mBottomSheetFragment = BottomSheetRedes(model)
                mBottomSheetFragment.show(
                    requireActivity().supportFragmentManager,
                    "BottomSheetRedes"
                )
            }
            tvAddMisas.setOnClickListener {
                val mBottomSheetFragment = BottomSheetHorarios(true, viewModel, model)
                mBottomSheetFragment.show(
                    requireActivity().supportFragmentManager,
                    "BottomSheetHorarios"
                )
            }
            tvAddServicio.setOnClickListener {
                val mBottomSheetFragment = BottomSheetHorarios(false, viewModel, model)
                mBottomSheetFragment.show(
                    requireActivity().supportFragmentManager,
                    "BottomSheetHorarios"
                )
            }
            ivWebClose.setOnClickListener {
                model.value!!.website = null
                tvWeb.visibility = View.GONE
                ivWebClose.visibility = View.GONE
            }
            ivTwiterClose.setOnClickListener {
                model.value!!.twitter = null
                tvTwiter.visibility = View.GONE
                ivTwiterClose.visibility = View.GONE
            }
            ivFacebookClose.setOnClickListener {
                model.value!!.facebook = null
                tvFacebook.visibility = View.GONE
                ivFacebookClose.visibility = View.GONE
            }
            ivInstagramClose.setOnClickListener {
                model.value!!.instagram = null
                tvInstagram.visibility = View.GONE
                ivInstagramClose.visibility = View.GONE
            }
            ivStreamingClose.setOnClickListener {
                model.value!!.stream = null
                tvStreaming.visibility = View.GONE
                ivStreamingClose.visibility = View.GONE
            }
            btnLlevame.setOnClickListener {
                val gmmIntentUri = Uri.parse(
                    "geo:0,0?q=" + latitude.replace(",", ".") + "," + longitude.replace(
                        ",",
                        "."
                    )
                )
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                startActivity(mapIntent)
            }
            btnComentarios.setOnClickListener {
                changeFragment()
            }
            btnGuardar.setOnClickListener {
                model.value?.let { m ->
                    m.principal?.let { p ->
                        showLoader()
                        if(validaRedes(m)){
                            viewModel.editIglesias(model.value!!)
                        }else{
                            error("La url de la red social no es correcta")
                            val mBottomSheetFragment = BottomSheetEdit(viewModel, model)
                            mBottomSheetFragment.show(
                                requireActivity().supportFragmentManager,
                                "BottomSheetEdit"
                            )
                        }
                    } ?: run {
                        error("Deve de asignar un padre")
                        val mBottomSheetFragment = BottomSheetEdit(viewModel, model)
                        mBottomSheetFragment.show(
                            requireActivity().supportFragmentManager,
                            "BottomSheetEdit"
                        )
                    }

                } ?: run {
                    error("Error al obtener la información")
                }

            }
        }


    }

    fun iniEdit(churchDetaillModel: ChurchDetaillModel) {
        binding.apply {
            ivChurch.loadByUrlIntDrawableerror(churchDetaillModel.image_url.toString(),R.drawable.emptychurch)
/*
            if (churchDetaillModel.image_url.isNullOrEmpty()) {
                Glide.with(requireContext())
                    .load(R.drawable.defaultimage)
                    .into(binding.ivChurch)
            } else {
                Glide.with(requireContext())
                    .load(Uri.parse(churchDetaillModel.image_url))
                    .into(binding.ivChurch)
            }*/

            tvNombreIglesia.text = churchDetaillModel.name
            tvDescripcionIglesia.text = churchDetaillModel.description
            if (!churchDetaillModel.principal?.name.isNullOrEmpty()) {
                tvResponsable.visibility = View.VISIBLE
                tvResponsable.text = churchDetaillModel.principal?.name
            }
            if (!churchDetaillModel.address.isNullOrEmpty()) {
                tvDireccionIglesia.visibility = View.VISIBLE
                tvDireccionIglesia.text = churchDetaillModel.address
            }
            if (churchDetaillModel.schedules!!.isNotEmpty()) {
                val item = churchDetaillModel.schedules!!.first()
                tvHorario.text =
                    "Horario del templo: ${PublicFunctions().obtenerDias(item.days)} de ${item.hour_start} a ${item.hour_end} \n"


            }
            if (!churchDetaillModel.attention.isNullOrEmpty()) {
                val item = churchDetaillModel.attention!!.first()
                tvHorario.visibility = View.VISIBLE
                tvHorario.text =
                    tvHorario.text.toString() + "Oficinas: ${PublicFunctions().obtenerDias(item.days)} de ${item.hour_start} a ${item.hour_end}"

            }

            val phone = churchDetaillModel.phone
            val email = churchDetaillModel.email
            setMailPhone(phone, email)


            latitude = churchDetaillModel.latitude.toString()
            longitude = churchDetaillModel.longitude.toString()
            if (latitude == "0.0" && longitude == "0.0") {
                btnLlevame.visibility = View.GONE
            }
            var redes = false
            if (!churchDetaillModel.website.isNullOrEmpty()) {
                tvWeb.visibility = View.VISIBLE
                ivWebClose.visibility = View.VISIBLE
                tvWeb.text = churchDetaillModel.website
                redes = true
            }
            if (!churchDetaillModel.twitter.isNullOrEmpty()) {
                tvTwiter.visibility = View.VISIBLE
                ivTwiterClose.visibility = View.VISIBLE
                tvTwiter.text = churchDetaillModel.twitter
                redes = true
            }
            if (!churchDetaillModel.facebook.isNullOrEmpty()) {
                tvFacebook.visibility = View.VISIBLE
                ivFacebookClose.visibility = View.VISIBLE
                tvFacebook.text = churchDetaillModel.facebook
                redes = true
            }
            if (!churchDetaillModel.instagram.isNullOrEmpty()) {
                tvInstagram.visibility = View.VISIBLE
                ivInstagramClose.visibility = View.VISIBLE
                tvInstagram.text = churchDetaillModel.instagram
                redes = true
            }
            if (!churchDetaillModel.stream.isNullOrEmpty()) {
                tvStreaming.visibility = View.VISIBLE
                ivStreamingClose.visibility = View.VISIBLE
                tvStreaming.text = churchDetaillModel.stream
                redes = true
            }
            if (redes) {
                cvRedes.visibility = View.VISIBLE
            }


            if (!churchDetaillModel.rating.isNullOrEmpty()) {
                star = PublicFunctions().redondearStar(churchDetaillModel.rating!!.toFloat())
                tvNumStar.text = "$star"
                rbIglesia.rating = star

            }
            if (!churchDetaillModel.masses.isNullOrEmpty()) {
                binding.rvHorarios.visibility = View.VISIBLE
                binding.rvHorarios.apply {
                    layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                    )

                    //adapter = MassAdapter(getMasses(churchDetaillModel.masses!!), true, binding.rvHorarios)

                    adapter = HorarioAdapter(
                        true,
                        churchDetaillModel.masses as MutableList<HoraryModelItem>, rvHorarios
                    ) {

                    }
                }
            }
            if (!churchDetaillModel.services.isNullOrEmpty()) {
                rvHorariosServicios.visibility = View.VISIBLE
                rvHorariosServicios.apply {
                    layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    adapter = ServiceAdapter(
                        true,
                        churchDetaillModel.services as MutableList<ServiceModel>,
                        rvHorariosServicios
                    ) {

                    }
                }
            }
            if (churchDetaillModel.reviewing!!) {
                btnComentarios.visibility = View.GONE
            }
        }
    }

    fun initObservers() {
        model.observe(viewLifecycleOwner) {
            iniEdit(it)
        }
        viewModel.response.observe(viewLifecycleOwner) {
            hideLoader()
            if (it == 200) {
                agregada("Se guardo la iglesia correctamente")
                requireActivity().onBackPressed()
            } else {
                error("Error al guardar la iglesia")
            }

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
    }

    private fun setMailPhone(phone: String?, email: String?) {
        binding.apply {
            tvInformacion.text = ""
            email?.let {
                tvInformacion.visibility = View.VISIBLE
                tvInformacion.text = "E-mail: $email\n"
                ivCorreo.setOnClickListener {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto: $email")
                    startActivity(intent)
                }
            } ?: run {
                ivCorreo.visibility = View.GONE
            }

            phone?.let {
                tvInformacion.visibility = View.VISIBLE
                tvInformacion.text = tvInformacion.text.toString() + "Teléfono: $phone "
                ivCall.setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel: $phone")
                    startActivity(intent)
                }
            } ?: run {
                ivCall.visibility = View.GONE
            }
        }


    }

    private fun changeFragment() {
        val bundle = Bundle()
        bundle.apply {
            putInt("idIglesia", idIglesia)
            putFloat("star", star)
            putString("nombre", church.name)
        }
        NavigationFragment.Builder()
            .setActivity(requireActivity())
            .setView(requireView().parent as ViewGroup)
            .setFragment(ReviewFragment.newInstance(requireActivity() as EAMXHome))
            .setBundle(bundle)
            .setAllowStack(true)
            .build().nextWithReplace()

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

    private fun getMasses(masses: List<HoraryModelItem>): ArrayList<ScheduleMass> {
        val lunesHora = ArrayList<Hour>()
        val lunes = ScheduleMass("Lunes", lunesHora)

        val martesHora = ArrayList<Hour>()
        val martes = ScheduleMass("Martes", martesHora)

        val miercolesHora = ArrayList<Hour>()
        val miercoles = ScheduleMass("Miercoles", miercolesHora)

        val juevesHora = ArrayList<Hour>()
        val jueves = ScheduleMass("Jueves", juevesHora)

        val viernesHora = ArrayList<Hour>()
        val viernes = ScheduleMass("Viernes", viernesHora)

        val sabadoHora = ArrayList<Hour>()
        val sabado = ScheduleMass("Sabado", sabadoHora)

        val domingoHora = ArrayList<Hour>()
        val domingo = ScheduleMass("Domingo", domingoHora)


        masses.forEach { hora ->
            hora.days.forEach { day ->
                when (day.name) {
                    "Lunes" -> {
                        if (day.checked)
                            lunesHora.add(Hour(hora.hour_start, hora.hour_end))
                    }
                    "Martes" -> {
                        if (day.checked)
                            martesHora.add(Hour(hora.hour_start, hora.hour_end))
                    }
                    "Miércoles" -> {
                        if (day.checked)
                            miercolesHora.add(Hour(hora.hour_start, hora.hour_end))
                    }
                    "Jueves" -> {
                        if (day.checked)
                            juevesHora.add(Hour(hora.hour_start, hora.hour_end))
                    }
                    "Viernes" -> {
                        if (day.checked)
                            viernesHora.add(Hour(hora.hour_start, hora.hour_end))
                    }
                    "Sábado" -> {
                        if (day.checked)
                            sabadoHora.add(Hour(hora.hour_start, hora.hour_end))
                    }
                    "Domingo" -> {
                        if (day.checked)
                            domingoHora.add(Hour(hora.hour_start, hora.hour_end))
                    }
                }
            }
        }

        val diasHorarios = ArrayList<ScheduleMass>()
        if (lunes.hours.isNotEmpty()) {
            diasHorarios.add(lunes)
        }
        if (martes.hours.isNotEmpty()) {
            diasHorarios.add(martes)
        }
        if (miercoles.hours.isNotEmpty()) {
            diasHorarios.add(miercoles)
        }
        if (jueves.hours.isNotEmpty()) {
            diasHorarios.add(jueves)
        }
        if (viernes.hours.isNotEmpty()) {
            diasHorarios.add(viernes)
        }
        if (sabado.hours.isNotEmpty()) {
            diasHorarios.add(sabado)
        }
        if (domingo.hours.isNotEmpty()) {
            diasHorarios.add(domingo)
        }

        return diasHorarios
        //return masses as MutableList<HoraryModelItem>
    }

    fun validaRedes(churchDetaillModel: ChurchDetaillModel): Boolean {
        binding.apply {
            if (!churchDetaillModel.website.isNullOrEmpty()) {
                if (Patterns.WEB_URL.matcher(churchDetaillModel.website).matches().not()) {
                    return false
                }
            }
            if (!churchDetaillModel.twitter.isNullOrEmpty()) {
                if (Patterns.WEB_URL.matcher(churchDetaillModel.twitter).matches().not()) {
                    return false
                }
            }
            if (!churchDetaillModel.facebook.isNullOrEmpty()) {
                if (Patterns.WEB_URL.matcher(churchDetaillModel.facebook).matches().not()) {
                    return false
                }
            }
            if (!churchDetaillModel.instagram.isNullOrEmpty()) {
                if (Patterns.WEB_URL.matcher(churchDetaillModel.instagram).matches().not()) {
                    return false
                }
            }
            if (!churchDetaillModel.stream.isNullOrEmpty()) {
                if (Patterns.WEB_URL.matcher(churchDetaillModel.stream).matches().not()) {
                    return false
                }
            }
            return true

        }

    }

}
