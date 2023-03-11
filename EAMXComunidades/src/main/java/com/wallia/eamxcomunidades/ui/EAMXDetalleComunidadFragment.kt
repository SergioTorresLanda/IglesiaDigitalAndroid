package com.wallia.eamxcomunidades.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.wallia.eamxcomunidades.R
import com.wallia.eamxcomunidades.adapter.ServiceActivityAdapter
import com.wallia.eamxcomunidades.database.instance.MeetRoomDataBaseCommunity
import com.wallia.eamxcomunidades.databinding.FragmentEamxDetalleComunidadBinding
import com.wallia.eamxcomunidades.model.CommunityDetailResponse
import com.wallia.eamxcomunidades.model.ServiceActivityModel
import com.wallia.eamxcomunidades.model.getDays
import com.wallia.eamxcomunidades.model.getHours
import com.wallia.eamxcomunidades.repository.Repository
import com.wallia.eamxcomunidades.utils.PublicFunctions
import com.wallia.eamxcomunidades.viewmodel.COMUNITYID
import com.wallia.eamxcomunidades.viewmodel.ComunidadesViewModel
import com.wallia.eamxcomunidades.viewmodel.PRINCIPAL
import com.wallia.eamxcomunidades.viewmodel.STAR
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.model.ReviewModel
import mx.arquidiocesis.eamxcommonutils.util.Adapter.ReviewAdapter
import mx.arquidiocesis.eamxcommonutils.util.Repository.ReviewRepository
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxcommonutils.util.review.ReviewFragment
import mx.arquidiocesis.eamxcommonutils.util.viewModel.ReviewViewModel

class EAMXDetalleComunidadFragment : FragmentBase() {

    lateinit var binding: FragmentEamxDetalleComunidadBinding
    private lateinit var communityDetail: CommunityDetailResponse
    private val viewModel: ComunidadesViewModel by lazy {
        getViewModel {
            ComunidadesViewModel(Repository(context = requireContext(),
                database = MeetRoomDataBaseCommunity.getRoomInstance(requireContext())))
        }
    }
    private val reviewViewModel: ReviewViewModel by lazy {
        getViewModel {
            ReviewViewModel(ReviewRepository(requireContext()))
        }
    }
    var idComunity = 0
    private var latitude = ""
    private var longitude = ""
    private var star = 0f
    private var isFav = false
    private var isDel = false
    private var isPrincipal = false
    var name = ""

    companion object {
        @JvmStatic
        fun newInstance(): EAMXDetalleComunidadFragment {
            val detalleComunidadFragment = EAMXDetalleComunidadFragment()
            return detalleComunidadFragment
        }
    }

    private fun initObservers() {
        viewModel.getCommunityDetailResponse.observe(viewLifecycleOwner) {

            communityDetail = it

            binding.apply {
                if (it.imageUrl.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(R.drawable.default_image)
                        .into(ivImage)
                } else {
                    Log.d("urlimg",it.imageUrl);
                    Glide.with(requireContext())
                        .load(Uri.parse(it.imageUrl))
                        .into(ivImage)
                }
                tvNameComunidad.text = it.name
                name = it.name.toString()
                if (!it.instituteOrAssociation.isNullOrEmpty()) {
                    tvInstituteComunidad.visibility = View.VISIBLE
                    tvInstituteComunidad.text = it.instituteOrAssociation

                }
                if (!it.description.isNullOrEmpty()) {
                    tvDescComunidad.visibility = View.VISIBLE
                    tvDescComunidad.text = it.description
                }

                if (!it.address.isNullOrEmpty()) {
                    tvDireccionIglesia.visibility = View.VISIBLE
                    tvDireccionIglesia.text = it.address
                }
                if (!it.serviceHoursGeneral.isNullOrEmpty()) {
                    val item = it.serviceHoursGeneral!!.first()
                    if (!item.schedules.isNullOrEmpty()) {
                        tvHorario.visibility = View.VISIBLE
                        tvHorario.text =
                            it.schedulesLabel
                    }
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
                if (!it.streamingChannel.isNullOrEmpty()) {
                    cardLiveEvent.visibility = View.VISIBLE
                    cardLiveEvent.text = it.streamingChannel
                    redes = true
                }
                if (redes) {
                    cvRedes.visibility = View.VISIBLE
                }
                if (!it.rating.isNullOrEmpty()) {
                    star = PublicFunctions().redondearStar(it.rating.toFloat())
                    rbIglesia.rating = star
                }
                viewModel.getMainCommunity()

            }

        }
        viewModel.mainCommunityResponse.observe(viewLifecycleOwner) {
            binding.apply {
                if (isPrincipal) {
                    if (it.assigned != null) {
                        if (it.assigned.id == idComunity) {
                            actualizaIconPrincipal(true, false)
                            binding.btnActivarIglesia.isEnabled = false
                        }
                    }
                } else {
                    if (!it.locations.isNullOrEmpty()) {
                        it?.locations?.forEach {
                            if (it.id == idComunity) {
                                isFav = true
                            }
                        }
                        actualizaIcon(false)
                    }
                }


            }
            reviewViewModel.getComentarios(idComunity, 1)
            viewModel.getActivities(idComunity)
        }
        viewModel.getActivitiesResponse.observe(viewLifecycleOwner) {
            binding.apply {
                if (!it.isNullOrEmpty()) {
                    var serviceActivityList = mutableListOf<ServiceActivityModel>()
                    it.forEach { item ->
                        serviceActivityList.add(
                            ServiceActivityModel(
                                name = item.name ?: "",
                                addressedTo = item.gearedToward ?: "",
                                description = item.description ?: "",
                                scheduleDays = item.getDays(),
                                scheduleHour = item.getHours(),
                                listOf()
                            )
                        )
                    }

                    rvEvents.visibility = View.VISIBLE
                    tvTitleEvent.visibility = View.VISIBLE
                    rvEvents.apply {
                        layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        adapter = ServiceActivityAdapter(
                            serviceActivityList,
                            rvEvents, isList = true
                        )
                    }
                }
            }
            hideLoader()
        }

        reviewViewModel.opinionListResponse.observe(viewLifecycleOwner) { item ->
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
                rvComments.apply {
                    layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    adapter = ReviewAdapter(
                        requireContext(),
                        list,
                        binding.rvComments,
                        false
                    ) { item, Etiqueta ->

                    }
                }
            }

        }
        reviewViewModel.errorResponse.observe(viewLifecycleOwner) {
            error(it)
        }
        viewModel.genericResponse.observe(viewLifecycleOwner) {
            if (isPrincipal) {
                actualizaIconPrincipal(true, true)
                binding.btnActivarIglesia.isEnabled = false
            } else {
                isFav = !isDel
                actualizaIcon(true)
            }
            hideLoader()
        }
        viewModel.errorResponse.observe(viewLifecycleOwner) {
            error(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentEamxDetalleComunidadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        idComunity = requireArguments().getInt(COMUNITYID)
        isPrincipal = requireArguments().getBoolean(PRINCIPAL)
        initObservers()
        if (idComunity != 0) {
            viewModel.getCommunityDetail(idComunity)
            showLoader()
        } else {
            error("Intente mas tarde")
        }
        binding.apply {
            if (!isPrincipal) {
                btnHeart.visibility = View.VISIBLE
                btnActivarIglesia.visibility = View.GONE
            } else {
                btnHeart.visibility = View.GONE
                btnActivarIglesia.visibility = View.VISIBLE
            }
            btnHeart.setOnClickListener {
                if (!msgGuest("agregar una comunidad como favorita")) {
                    if (isPrincipal) {
                        viewModel.postFavoriteCommunity(idComunity, true, communityDetail)
                        showLoader()
                    } else {
                        if (isFav) {
                            isDel = true
                            viewModel.deleteCommunity(idComunity)
                        } else {
                            isDel = false
                            viewModel.postFavoriteCommunity(idComunity, false, communityDetail)
                        }
                        showLoader()
                    }
                }
            }
            btnActivarIglesia.setOnClickListener {
                if (!msgGuest("agregar una comunidad como principal")) {
                    if (isPrincipal) {
                        viewModel.postFavoriteCommunity(idComunity, true, communityDetail)
                        showLoader()
                    }
                }
            }
            btnLlevame.setOnClickListener {
                val gmmIntentUri = Uri.parse(
                    "geo:0,0?q=" + latitude.replace(",", ".") + "," + longitude.replace(
                        ",",
                        "."
                    )
                )
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                try {
                    startActivity(mapIntent)
                }catch (e:Exception){
                    UtilAlert.Builder()
                        .setTitle("¡Lo sentimos!")
                        .setMessage("No pudimos encontrar la ubicación")
                        .setTextButtonOk("Aceptar")
                        .build()
                }
            }

            btnComment.setOnClickListener {
                if (!msgGuest("escribir una opinión a la comunidad")) {
                    val bundle = Bundle()
                    bundle.apply {
                        putInt("idIglesia", idComunity ?: 0)
                        bundle.putFloat(STAR, star)
                        putString("nombre", name)
                    }
                    NavigationFragment.Builder()
                        .setActivity(requireActivity())
                        .setView(requireView().parent as ViewGroup)
                        .setFragment(ReviewFragment.newInstance(requireActivity() as EAMXHome))
                        .setBundle(bundle)
                        .build().nextWithReplace()
                }
            }
        }

    }

    private fun setMailPhone(phone: String?, email: String?) {
        binding.apply {
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
                tvInformacion.text = tvInformacion.text.toString() + "Teléfono: $phone"
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

    private fun actualizaIcon(isFromResponse: Boolean) {
        if (isFav) {
            if (isFromResponse) {
                UtilAlert
                    .Builder()
                    .setTitle("${binding.tvNameComunidad.text} ahora es tu comunidad favorita  y podrás ver sus posteos, visualizar cambios y recibir invitaciones a las actividades\n")
                    .build()
                    .show(childFragmentManager, "")
            }

            Glide.with(this)
                .load(R.drawable.ic_icon_heart)
                .into(binding.btnHeart)

        } else {
            Glide.with(this)
                .load(R.drawable.ic_icon_heart_vacio)
                .into(binding.btnHeart)

        }
    }

    private fun actualizaIconPrincipal(isPri: Boolean, isFromResponse: Boolean) {
        if (isPri) {
            if (isFromResponse) {
                UtilAlert
                    .Builder()
                    .setTitle("${binding.tvNameComunidad.text} ahora es tu comunidad principal  y podrás ver sus posteos, visualizar cambios y recibir invitaciones a las actividades\n")
                    .build()
                    .show(childFragmentManager, "")
            }

            binding.btnActivarIglesia.visibility = View.GONE
        } else {
            binding.btnActivarIglesia.visibility = View.VISIBLE
        }
    }

    private fun error(it: String) {
        hideLoader()
        UtilAlert
            .Builder()
            .setTitle("Aviso")
            .setMessage(it)
            .build()
            .show(childFragmentManager, "")
    }
}