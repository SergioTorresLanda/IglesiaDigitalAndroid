package mx.arquidiocesis.eamxevent.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_event_other.*
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXProfile
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxevent.R
import mx.arquidiocesis.eamxevent.adapter.OtherAllAdapter
import mx.arquidiocesis.eamxevent.databinding.FragmentEventOtherBinding
import mx.arquidiocesis.eamxevent.model.*
import mx.arquidiocesis.eamxevent.model.enum.ParticipationOther
import mx.arquidiocesis.eamxevent.repository.RepositoryEvent


class EventOtherFragment : FragmentBase() {

    lateinit var binding: FragmentEventOtherBinding
    lateinit var viewmodel: ViewModelEvent
    lateinit var adapterOther: OtherAllAdapter
    private var type: Int = 0
    private var participation: Array<ParticipationOther> = ParticipationOther.values()
    private var init = true
    private var eventoId = ""
    private var myEvent = OtherEvent()
    private var userId = 0
    var userIsAdmin=true

    companion object {
        fun newInstance(callBack: EAMXHome): EventOtherFragment {
            val fragment = EventOtherFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewmodel = ViewModelEvent(RepositoryEvent(requireContext()))
        binding = FragmentEventOtherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "Home_ActividadesOther")
            })
        }
        callBack.showToolbar(true, AppMyConstants.evento)
        init = true
        userId = eamxcu_preferences.getData(EAMXEnumUser.USER_ID.name, EAMXTypeObject.INT_OBJECT) as Int
        setupInit(msgGuest(isMsg = false))
        hideOrShowCreateBtn()
        initObservers()
        getAllOthers()
        initButtons()
    }

    private fun hideOrShowCreateBtn() {
        tvNewOther.visibility=View.INVISIBLE
        userIsAdmin = when (eamxcu_preferences.getData(EAMXEnumUser.USER_PROFILE.name, EAMXTypeObject.STRING_OBJECT) as String) {
            EAMXProfile.Devoted.rol,//fiel
            EAMXProfile.Priest.rol,//sacer
            EAMXProfile.GestorContenidos.rol //gestor de conts
            -> false
            else -> true
        }
        if (userIsAdmin){
            tvNewOther.visibility=View.VISIBLE
        }
    }

    private fun initObservers() {
        viewmodel.responseAllOther.observe(viewLifecycleOwner) { item ->
            println("XXX::: responseAllOther")
            if (item.isNotEmpty()) {
                println("XXX::: evento not empty")
                if (item[0].eventoId != null) {
                    println("XXX::: eventoId no null")
                    if (init) {
                        item.forEach {
                            if (it.userId == userId) {
                                println("XXX:::igual")
                                println(userId)
                                println(it.userId)
                                if (it.status==1){
                                    println("XXX:::status 1")
                                    eventoId = it.eventoId.toString()
                                    myEvent = it
                                    tvNewOther.text = AppMyConstants.updateOther
                                    return@forEach
                                }else{
                                    println("XXX:::status 0")
                                }
                            }else{
                                println("XXX:::no igual ")
                                println(userId)
                                println(it.userId)
                            }
                        }
                    }
                    println("XXX::: NO INIT")
                    init = false
                    println("TYPEE::")
                    println(type)
                    val others = item.filter {
                        when (type) {
                            1 -> it.donantesBool == 1 && it.status == 1
                            2 -> it.voluntariosBool == 1 && it.status == 1
                            else -> it.status == 1
                        }
                    }

                    if (others.isNotEmpty()) {
                        adapterOther.items.clear()
                        adapterOther.notifyDataSetChanged()
                        adapterOther.items.addAll(others)
                        adapterOther.notifyDataSetChanged()
                        val prevSize = adapterOther.items.size
                        if (prevSize != 0) {
                            adapterOther.notifyItemRangeInserted(
                                prevSize,
                                adapterOther.items.count() - 1
                            )
                        }
                    }
                }
            }
            if (adapterOther.items.size == 0) {
                adapterOther.items.addAll(arrayListOf(OtherEvent()))
                adapterOther.notifyDataSetChanged()
            }
            hideLoader()
           // initButtons()
        }

        viewmodel.errorResponse.observe(viewLifecycleOwner) {
            UtilAlert.Builder().setTitle("Aviso").setMessage(it).build().show(childFragmentManager, "")
        }

    }

    private fun initButtons() {
        tvNewOther.setOnClickListener {
            if (!init) {
                if (eventoId==""){
                    println("XXX::: CREATE")
                    NavigationFragment.Builder()
                        .setActivity(requireActivity())
                        .setView(requireView().parent as ViewGroup)
                        .setFragment(EventOtherDetailFragment.newInstance(callBack) as Fragment)
                        .setBundle(Bundle().apply {
                            putString("eventoId", eventoId)
                            putInt("tipoEvento", 0)
                        })
                        .build().nextWithReplace()
                }else{
                    println("XXX::: UPDATE")
                val days = myEvent.horarios!![0].days!!.filter { it.checked }
                val daysArr: ArrayList<String> = arrayListOf()
                days.forEach {daysArr.add(it.name)}

                println("XXX::: NO INIT")
                NavigationFragment.Builder()
                    .setActivity(requireActivity())
                    .setView(requireView().parent as ViewGroup)
                    .setFragment(EventOtherDetailFragment.newInstance(callBack) as Fragment)
                    .setBundle(Bundle().apply {
                        putString("eventoId", eventoId)
                        putInt("tipoEvento", myEvent.tipoEvento!!)
                        putString("nombre", myEvent.nombre!!)
                        putString("direccion", myEvent.direccion!!)
                        putStringArrayList("dias", daysArr)
                        putString("hourStart", myEvent.horarios!![0].hour_start)
                        putString("hourEnd", myEvent.horarios!![0].hour_end)
                        putInt("cobro", myEvent.cobro!!)
                        putString("descripcion", myEvent.descripcion!!)
                        putString("publico", myEvent.publico!!)
                        putString("donantesTxt", myEvent.donantesTxt!!)
                        putString("voluntariosTxt", myEvent.voluntariosTxt!!)
                    })
                    .build().nextWithReplace()
                }
            }else{
                println("XXX::: SI INIT")
            }
        }

        btnComedoresActDespensa.setOnClickListener {
            NavigationFragment.Builder()
                .setActivity(requireActivity())
                .setView(requireView().parent as ViewGroup)
                .setFragment(EventFragment.newInstance(callBack) as Fragment)
                .build().nextWithReplace()
        }
        btnDespensasActDespensa.setOnClickListener {
            NavigationFragment.Builder()
                .setActivity(requireActivity())
                .setView(requireView().parent as ViewGroup)
                .setFragment(EventPantriesFragment.newInstance(callBack) as Fragment)
                .build().nextWithReplace()
        }

        val adaptador = ArrayAdapter.createFromResource(
            requireContext(), R.array.participations2,
            android.R.layout.simple_spinner_dropdown_item
        )

        spTipo.adapter = adaptador
        spTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                type = participation[position].pos
                println(type)
                getAllOthers()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                //lblSeleccion.text = "Sin selección"
            }
        }
    }

    fun getAllOthers() {
        showLoader()
        adapterOther = OtherAllAdapter(requireContext(), type)
        adapterOther.items = arrayListOf()
        setupRecyclerView()
        click()
        viewmodel.requestAllOther(0,0)
    }

    fun click() {
        adapterOther.onItemClickListener = { item, e ->
            when (e) {
                EDITAR -> {
                }
                DONAR -> {
                    if (!msgGuest("realizar una donación")) {
                        NavigationFragment.Builder()
                            .setActivity(requireActivity())
                            .setView(requireView().parent as ViewGroup)
                            .setFragment(EventOtherActorDetailFrag.newInstance(callBack) as Fragment)
                            .setBundle(Bundle().apply {
                                putInt("eventoId", item.eventoId!!)
                                putInt("tipoActor", 1)
                            })
                            .build().nextWithReplace()
                    }
                }
                AYUDAR -> {
                    if (!msgGuest("apoyar como voluntario.")) {
                        NavigationFragment.Builder()
                            .setActivity(requireActivity())
                            .setView(requireView().parent as ViewGroup)
                            .setFragment(EventOtherActorDetailFrag.newInstance(callBack) as Fragment)
                            .setBundle(Bundle().apply {
                                putInt("eventoId", item.eventoId!!)
                                putInt("tipoActor", 2)
                            })
                            .build().nextWithReplace()
                    }
                }
                PARTICIPAR -> {
                    if (!msgGuest("beneficiarte la actividad.")) {
                        NavigationFragment.Builder()
                            .setActivity(requireActivity())
                            .setView(requireView().parent as ViewGroup)
                            .setFragment(EventOtherActorDetailFrag.newInstance(callBack) as Fragment)
                            .setBundle(Bundle().apply {
                                putInt("eventoId", item.eventoId!!)
                                putInt("tipoActor", 3)
                            })
                            .build().nextWithReplace()
                    }
                }
                "" -> {
                }
            }
        }
    }
}