package mx.arquidiocesis.misiglesias.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.*
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.model.ReviewModel
import mx.arquidiocesis.eamxcommonutils.model.ViewPagerModel
import mx.arquidiocesis.eamxcommonutils.util.*
import mx.arquidiocesis.eamxcommonutils.util.Adapter.ReviewAdapter
import mx.arquidiocesis.eamxcommonutils.util.Repository.ReviewRepository
import mx.arquidiocesis.eamxcommonutils.util.ViewPager.ViewPagerPrincipal
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxcommonutils.util.review.ReviewFragment
import mx.arquidiocesis.eamxcommonutils.util.viewModel.ReviewViewModel
import mx.arquidiocesis.misiglesias.R
import mx.arquidiocesis.misiglesias.adapters.MassAdapter
import mx.arquidiocesis.misiglesias.adapters.ServiceAdapter
import mx.arquidiocesis.misiglesias.database.instance.MeetRoomDataBase
import mx.arquidiocesis.misiglesias.databinding.FragmentDetailChurchFindedBinding
import mx.arquidiocesis.misiglesias.model.*
import mx.arquidiocesis.misiglesias.repository.Repository
import mx.arquidiocesis.misiglesias.utils.PublicFunctions
import mx.arquidiocesis.misiglesias.viewmodel.DetalleIglesiaViewModel
import java.lang.reflect.Type

class DetailChurchFindedFragment : FragmentBase() {
    companion object {
        fun newInstance(callBack: EAMXHome): DetailChurchFindedFragment {
            val fragment = DetailChurchFindedFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    private val detalleIglesiaViewModel: DetalleIglesiaViewModel by lazy {
        getViewModel {
            DetalleIglesiaViewModel(
                Repository(
                    requireContext(),
                    MeetRoomDataBase.getRoomInstance(requireActivity())
                )
            )
        }
    }
    private val reviewViewModel: ReviewViewModel by lazy {
        getViewModel {
            ReviewViewModel(ReviewRepository(requireContext()))
        }
    }
    private var latitude = ""
    private var longitude = ""
    private var isFav = false
    private var isPrincipal = false
    var userId = 0
    private var fragment = DialogFragment()
    lateinit var binding: FragmentDetailChurchFindedBinding
    var idIglesia = 0
    var star = 0f
    var nombre = ""
    var tutorial = "tutorialIglesias"
    lateinit var churchDetaillModel: ChurchDetaillModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDetailChurchFindedBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            isPrincipal = it.getBoolean("isPrincipal") ?: false
            idIglesia = it.getInt("idIglesia") ?: 0
        }


        if (userAllowAccessAsAdmin(EAMXEnumUser.USER_PERMISSION_CHURCH.name) && isChurchAvailableForEdit(
                idIglesia
            )
        ) {
            "EDIT CHURCH".log()
            callBack.showToolbar(true, AppMyConstants.miIglesia) {
                changeFragmentEdit()
            }
        } else {
            "NOT EDIT CHURCH".log()
            callBack.showToolbar(true, AppMyConstants.miIglesia)
        }

        userId = PublicFunctions().getUserId()
        binding.apply {

            var isTutorial = eamxcu_preferences.getData(
                tutorial,
                EAMXTypeObject.BOOLEAN_OBJECT
            ) as Boolean
            if (!isTutorial) {
                tutorial()
            }
            if (!isPrincipal) {
                llButtons.visibility = View.VISIBLE
                btnActivarIglesia.visibility = View.GONE
            } else {
                llButtons.visibility = View.GONE
                btnActivarIglesia.visibility = View.VISIBLE
            }

            detalleIglesiaViewModel.obtenerDetalle(idIglesia)
            initObservers()

            showLoader()


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

            btnHeart.setOnClickListener {

                if (isFav) {
                    detalleIglesiaViewModel.delFavoritas(userId, idIglesia)
                } else {
                    detalleIglesiaViewModel.setFavoritas(churchDetaillModel, false)
                }
                showLoader()

            }
            ivChurch.setOnClickListener {
                //  changeFragmentEdit()
            }
            btnComentarios.setOnClickListener {
                changeFragment()
            }

            btnActivarIglesia.setOnClickListener {
                if (isPrincipal) {
                    detalleIglesiaViewModel.setFavoritas(churchDetaillModel, true)
                    showLoader()
                }
            }

        }
    }

    private fun isChurchAvailableForEdit(churchId: Int): Boolean {
        val list = eamxcu_preferences.getData(
            EAMXEnumUser.USER_CHURCH_ALLOW_EDIT.name,
            EAMXTypeObject.STRING_OBJECT
        ).toString()
        if (list.isEmpty()) {
            return false
        }

        return try {
            val collectionType: Type =
                object : TypeToken<Collection<ModuleAdminEnabled?>?>() {}.type
            val dataList: Collection<ModuleAdminEnabled> = Gson().fromJson(list, collectionType)
            return dataList.firstOrNull { item -> item.id == churchId } != null
        } catch (ex: Exception) {
            false
        }
    }

    fun initObservers() {
        detalleIglesiaViewModel.response.observe(viewLifecycleOwner) {
            churchDetaillModel = it
            detalleIglesiaViewModel.favoritasList()
            binding.apply {
                if (it.image_url.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(R.drawable.defaultimage)
                        .into(binding.ivChurch)
                } else {
                    Glide.with(requireContext())
                        .load(Uri.parse(it.image_url))
                        .into(binding.ivChurch)
                }
                tvNombreIglesia.text = it.name
                nombre = it.name.toString()
                tvDescripcionIglesia.text = it.description
                if (!it.principal?.name.isNullOrEmpty()) {
                    tvResponsable.visibility = View.VISIBLE
                    tvResponsable.text = "Responsable: "+it.principal?.name
                }else{
                    tvResponsable.visibility = View.VISIBLE
                    tvResponsable.text = "Responsable: No disponible"
                }
                if (!it.address.isNullOrEmpty()) {
                    tvDireccionIglesia.visibility = View.VISIBLE
                    tvDireccionIglesia.text = it.address
                }
                if (!it.schedules.isNullOrEmpty()) {
                    lnlHora.visibility = View.VISIBLE
                    val item = it.schedules!!.first()
                    tvHT.text = "Horario de templo:"
                    tvHorarioTemplo.text =
                        "${PublicFunctions().obtenerDias(item.days)} de ${item.hour_start} a ${item.hour_end} \n"

                } else {
                    lnlHora.visibility = View.VISIBLE
                    tvHT.text = "Horario de templo:"
                    tvHorarioTemplo.text =
                        "No disponible"

                }
                if (!it.attention.isNullOrEmpty()) {
                    lnlHora.visibility = View.VISIBLE
                    val horariosLunes = arrayListOf<ScheduleAttention>()
                    val horariosMartes = arrayListOf<ScheduleAttention>()
                    val horariosMiercoles = arrayListOf<ScheduleAttention>()
                    val horariosJueves = arrayListOf<ScheduleAttention>()
                    val horariosViernes = arrayListOf<ScheduleAttention>()
                    val horariosSabado = arrayListOf<ScheduleAttention>()
                    val horariosDomingo = arrayListOf<ScheduleAttention>()
                    tvHS.text = "Horarios de oficina:"
                    tvHorario.text = ""
                    if (!cargar(horariosDomingo, it, 0).isNullOrEmpty()) {
                        tvHorario.text =
                            tvHorario.text.toString() + "Domingo :\n" + cargar(
                                horariosDomingo,
                                it,
                                0
                            )
                    }
                    if (!cargar(horariosLunes, it, 1).isNullOrEmpty()) {
                        tvHorario.text =
                            tvHorario.text.toString() + "Lunes :\n" + cargar(horariosLunes, it, 1)
                    }

                    if (!cargar(horariosMartes, it, 2).isNullOrEmpty()) {
                        tvHorario.text =
                            tvHorario.text.toString() + "Martes :\n" + cargar(horariosMartes, it, 2)
                    }

                    if (!cargar(horariosMiercoles, it, 3).isNullOrEmpty()) {
                        tvHorario.text = tvHorario.text.toString() + "Miércoles :\n" + cargar(
                            horariosMiercoles,
                            it,
                            3
                        )
                    }

                    if (!cargar(horariosJueves, it, 4).isNullOrEmpty()) {
                        tvHorario.text =
                            tvHorario.text.toString() + "Jueves :\n" + cargar(horariosJueves, it, 4)
                    }

                    if (!cargar(horariosViernes, it, 5).isNullOrEmpty()) {
                        tvHorario.text =
                            tvHorario.text.toString() + "Viernes :\n" + cargar(
                                horariosViernes,
                                it,
                                5
                            )
                    }
                    if (!cargar(horariosSabado, it, 6).isNullOrEmpty()) {
                        tvHorario.text =
                            binding.tvHorario.text.toString() + "Sábado :\n" + cargar(
                                horariosSabado,
                                it,
                                6
                            )
                    }
                } else {
                    lnlHora.visibility = View.VISIBLE
                    tvHS.text = "Horarios de oficina:"
                    tvHorario.text = "No disponible"
                }
                val phone = it.phone
                val email = it.email
                setMailPhone(phone, email)
                latitude = it.latitude.toString()
                longitude = it.longitude.toString()
                if (latitude == "0.0" && longitude == "0.0") {
                    binding.btnLlevame.visibility = View.GONE
                }
                var redes = false
                if (!it.website.isNullOrEmpty()) {
                    tvWeb.visibility = View.VISIBLE
                    tvWeb.text = it.website
                    redes = true
                }
                if (!it.twitter.isNullOrEmpty()) {
                    tvTwiter.visibility = View.VISIBLE
                    tvTwiter.text = it.twitter
                    redes = true
                }
                if (!it.facebook.isNullOrEmpty()) {
                    tvFacebook.visibility = View.VISIBLE
                    tvFacebook.text = it.facebook
                    redes = true
                }
                if (!it.instagram.isNullOrEmpty()) {
                    tvInstagram.visibility = View.VISIBLE
                    tvInstagram.text = it.instagram
                    redes = true
                }
                if (!it.stream.isNullOrEmpty()) {
                    tvStreaming.visibility = View.VISIBLE
                    tvStreaming.text = it.stream
                    redes = true
                }
                if (redes) {
                    cvRedes.visibility = View.VISIBLE
                }

                /*if (!it.stream.isNullOrEmpty()) {
                    cvEventosEnvivo.visibility = View.VISIBLE
                    cvEventosEnvivo.setOnClickListener {

                    }
                }*/
                if (!it.rating.isNullOrEmpty()) {
                    star = PublicFunctions().redondearStar(it.rating!!.toFloat())
                    rbIglesia.rating = star
                }

                if (!it.masses.isNullOrEmpty()) {
                    binding.rvHorarios.visibility = View.VISIBLE
                    binding.tvTitleHorario.visibility = View.VISIBLE
                    binding.rvHorarios.apply {
                        layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.VERTICAL,
                            false
                        )

                        adapter = MassAdapter(getMasses(it.masses!!), false, binding.rvHorarios)

                        /*adapter = HorarioAdapter(
                            requireContext(),
                            getMasses(it.masses!!),
                            //it.masses as MutableList<HoraryModelItem>,
                            rvHorarios
                        ) {
                        }*/
                    }
                }

                if (!it.services.isNullOrEmpty()) {
                    binding.rvHorariosServicios.visibility = View.VISIBLE
                    binding.textView4.visibility = View.VISIBLE
                    binding.rvHorariosServicios.apply {
                        layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        adapter = ServiceAdapter(
                            services = it.services as MutableList<ServiceModel>,
                            recyclerView = rvHorariosServicios
                        ) {
                        }
                    }
                }
                reviewViewModel.getComentarios(idIglesia, 1)

            }

        }
        detalleIglesiaViewModel.favResponse.observe(viewLifecycleOwner)
        {
            if (isPrincipal) {
                actualizaIconPrincipal(true)
                binding.btnActivarIglesia.isEnabled = false
            } else {
                isFav = true
                actualizaIcon()
            }
            hideLoader()
        }
        detalleIglesiaViewModel.delResponse.observe(viewLifecycleOwner)
        {
            isFav = false
            actualizaIcon()
            hideLoader()
        }
        detalleIglesiaViewModel.responseFavotritas.observe(viewLifecycleOwner)
        {
            if (isPrincipal) {
                if (it?.assigned?.id == idIglesia) {
                    actualizaIconPrincipal(true)
                    binding.btnActivarIglesia.isEnabled = false
                }
            } else {
                it?.locations?.forEach {
                    if (it.id == idIglesia) {
                        isFav = true
                    }
                }
                actualizaIcon()
            }


        }
        detalleIglesiaViewModel.errorResponse.observe(viewLifecycleOwner)
        {
            hideLoader()
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .setIsCancel(false)
                .build()
                .show(childFragmentManager, "")
        }
        reviewViewModel.opinionListResponse.observe(viewLifecycleOwner)
        { item ->
            hideLoader()
            var list: MutableList<ReviewModel> = mutableListOf()
            if (item.my_review != null) {
                list.add(item.my_review!!)
                if (!item.other_reviews.isNullOrEmpty()) {
                    item.other_reviews!!.forEach {
                        list.add(it)
                    }
                }
            } else if (!item.other_reviews.isNullOrEmpty()) {
                list = item.other_reviews as MutableList<ReviewModel>
            }
            binding.apply {
                tvNumComnetarios.text = "${list.size} comentarios"
                rvComentarios.apply {
                    layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    adapter = ReviewAdapter(
                        requireContext(),
                        list,
                        binding.rvComentarios,
                        false
                    ) { item, Etiqueta ->

                    }
                }
            }

        }
        reviewViewModel.errorResponse.observe(viewLifecycleOwner)
        {
            error(it)
        }
        detalleIglesiaViewModel.errorExitScreenResponse.observe(viewLifecycleOwner)
        {
            error(it)
        }
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
                ivCorreo.visibility = View.INVISIBLE
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
                ivCall.visibility = View.INVISIBLE
            }
        }


    }

    private fun actualizaIcon() {
        binding.btnHeart.apply {
            if (isFav) {
                setImageResource(R.drawable.ic_icon_heart)
            } else {
                setImageResource(R.drawable.ic_icon_heart_vacio)
            }
        }

    }

    private fun actualizaIconPrincipal(isPri: Boolean) {
        if (isPri) {
            binding.btnActivarIglesia.visibility = View.GONE
        } else {
            binding.btnActivarIglesia.visibility = View.VISIBLE
        }
    }

    private fun tutorial() {
        fragment = ViewPagerPrincipal(
            listOf(
                ViewPagerModel(
                    "",
                    BitmapFactory.decodeResource(resources, R.drawable.tutorial_1_iglesias),
                    0
                ),
                ViewPagerModel(
                    "",
                    BitmapFactory.decodeResource(resources, R.drawable.tutorial_2_iglesias),
                    0
                )
            ), false
        ) {
            eamxcu_preferences.saveData(tutorial, true)
            fragment.dismiss()
        }

        fragment.show(childFragmentManager, "LOADER")
    }

    private fun changeFragment() {
        val bundle = Bundle()
        bundle.putInt("idIglesia", idIglesia)
        bundle.putFloat("star", star)
        bundle.putString("nombre", nombre)
        NavigationFragment.Builder()
            .setActivity(requireActivity())
            .setView(requireView().parent as ViewGroup)
            .setFragment(ReviewFragment.newInstance(this))
            .setBundle(bundle)
            .setAllowStack(true)
            .build().nextWithReplace()

    }

    private fun changeFragmentEdit() {
        val bundle = Bundle()
        bundle.putBoolean("isPrincipal", isPrincipal)
        NavigationFragment.Builder()
            .setActivity(requireActivity())
            .setView(requireView().parent as ViewGroup)
            .setFragment(EditFragment(churchDetaillModel))
            .setBundle(bundle)
            .setAllowStack(true)
            .build().nextWithReplace()
    }

    private fun cargar(
        arrayList: MutableList<ScheduleAttention>,
        churchDetailModel: ChurchDetaillModel,
        day: Int
    ): String {
        var horas = ""
        churchDetailModel.attention!!.sortedBy {
            it.hour_start
        }.forEach { it ->
            arrayList.clear()
            arrayList.add(
                ScheduleAttention(
                    it.days[day].name,
                    it.days[day].checked, Hour(it.hour_start, it.hour_end)
                )
            )
            var list: MutableList<ScheduleAttention> = mutableListOf()
            list =
                arrayList.sortedByDescending { it.hours.end_hour } as MutableList<ScheduleAttention>
            for (i in list.indices) {
                list.forEach {
                    if (it.status == true) {
                        var count = i
                        for (i in i..count) {
                            horas += "${list[i].hours.start_hour} a ${list[i].hours.end_hour}\n"
                        }
                    }

                }
            }

        }
        return horas
    }

    private fun error(it: String?) {
        hideLoader()
        if (!it.isNullOrEmpty()) {
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .setListener {
                    activity?.onBackPressed()
                }
                .setIsCancel(false)
                .build()
                .show(childFragmentManager, "")
        } else {
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage("Error")
                .setIsCancel(false)
                .setListener {
                    activity?.onBackPressed()
                }
                .build()
                .show(childFragmentManager, "")
        }

    }
}
