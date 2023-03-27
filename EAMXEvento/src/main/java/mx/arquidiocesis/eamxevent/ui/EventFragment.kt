package mx.arquidiocesis.eamxevent.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.fragment_event.*
import kotlinx.android.synthetic.main.fragment_event_detail.*
import kotlinx.android.synthetic.main.fragment_event_detail.spZone
import kotlinx.android.synthetic.main.item_event_detail.*
import kotlinx.android.synthetic.main.item_event_detail.view.*
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxevent.R
import mx.arquidiocesis.eamxevent.adapter.DinerAllAdapter
import mx.arquidiocesis.eamxevent.databinding.FragmentEventBinding
import mx.arquidiocesis.eamxevent.model.*
import mx.arquidiocesis.eamxevent.model.enum.Delegations
import mx.arquidiocesis.eamxevent.model.enum.Participation
import mx.arquidiocesis.eamxevent.repository.RepositoryEvent


const val EDITAR = "EDITAR"
const val DONAR = "DONAR"
const val PARTICIPAR = "PARTICIPAR"
class EventFragment : FragmentBase() {

    lateinit var binding: FragmentEventBinding
    lateinit var viewmodel: ViewModelEvent
    lateinit var adapter: DinerAllAdapter
    private var zona: Int = 0
    private var type: Int = 0
    private var participation: Array<Participation> = Participation.values()
    private var delegations: Array<Delegations> = Delegations.values()
    private var init = true
    private var diner_id = ""
    private var userId = 0

    companion object {
        fun newInstance(callBack: EAMXHome): EventFragment {
            var fragment = EventFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        viewmodel = ViewModelEvent(RepositoryEvent(requireContext()))
        binding = FragmentEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "Home_Actividades")
            })
        }
        callBack.showToolbar(true, AppMyConstants.evento)
        init = true
        userId = eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID.name,
            EAMXTypeObject.INT_OBJECT
        ) as Int
        setupInit(msgGuest(isMsg = false))
        initObservers()
        //getAllDiners() //Ya no se ejecuta por que se activa en el spinner: spZone
        initButtons()

    }

    private fun initObservers() {
        viewmodel.responseAllDin.observe(viewLifecycleOwner) { item ->
            if (item.size > 0) {
                if (item[0].fCCOMEDORID != null) {
                    if (init) { //Segunda vez
                        item.forEach {
                            if (it.fIUSERID == userId.toString()) {
                                diner_id = it.fCCOMEDORID.toString()
                                tvNewEvent.setText(AppMyConstants.updateEvento)
                                return@forEach
                            }
                        }
                    }
                    init = false

                    val comedores = item.filter {
                        when (type) {
                            1 -> {
                                it.fCVOLUNTARIOS == "1" && it.fCSTATUS == "1"
                            }
                            else -> {
                                it.fCSTATUS == "1"
                            }
                        }
                                &&
                                when (zona) {
                                    0 -> {
                                        it.fCSTATUS == "1"
                                    }
                                    else -> {
                                        it.fIZONA == zona.toString() && it.fCSTATUS == "1"
                                    }
                                }
                    }
                    if (comedores.size > 0) {
                        adapter.items.clear()
                        adapter.notifyDataSetChanged()

                        adapter.items.addAll(comedores)
                        adapter.notifyDataSetChanged()
                        val prevSize = adapter.items.size
                        if (prevSize != 0) {
                            adapter.notifyItemRangeInserted(prevSize, adapter.items.count() - 1)
                        }
                    }
                }
            }
            if (adapter.items.size == 0) {
                adapter.items.addAll(arrayListOf(DinerResponse()))
                adapter.notifyDataSetChanged()
            }
            hideLoader()
        }

        viewmodel.errorResponse.observe(viewLifecycleOwner) {
            //showSkeleton(false)
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
        }
    }
    /*
    fun selectRow(item: DinerResponse) {
        if (item.fIZONA.isNotEmpty()) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.content)))
        } else {
            changeFragment(EAMXDetailFragment(), Bundle().apply {
                putParcelable(EAMXEnumUser.PUBLICATIONS.name, item)
            })
        }
    }

     */

    fun initButtons() {
        tvNewEvent.setOnClickListener {
            if (!init) {
                NavigationFragment.Builder()
                    .setActivity(requireActivity())
                    .setView(requireView().parent as ViewGroup)
                    .setFragment(EventDetailFragment.newInstance(callBack) as Fragment)
                    .setBundle(Bundle().apply {
                        putString("diner_id", diner_id)
                    })
                    .build().nextWithReplace()
            }
        }
        tvNewDon.setOnClickListener {
            if (!init) {
                NavigationFragment.Builder()
                    .setActivity(requireActivity())
                    .setView(requireView().parent as ViewGroup)
                    .setFragment(EventDonorFragment.newInstance(callBack) as Fragment)
                    .setBundle(Bundle().apply {
                        putString("diner_id", diner_id)
                    })
                    .build().nextWithReplace()
            }
        }
        spZone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                zona = delegations[position].pos
                getAllDiners()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                lblSeleccion.text = "Sin selección"
            }
        }

        val adaptador = ArrayAdapter.createFromResource(
            requireContext(), R.array.participations,
            android.R.layout.simple_spinner_dropdown_item
        )

        spParticipacion.adapter = adaptador
        spParticipacion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                type = participation[position].pos
                println(type)
                getAllDiners()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                lblSeleccion.text = "Sin selección"
            }
        }
    }

    fun getAllDiners() {
        showLoader()
        adapter =
            DinerAllAdapter(requireContext(), type)
        adapter.items = arrayListOf()//arrayListOf(DinerResponse())
        setupRecyclerView()
        click()
        viewmodel.requestAllDiner(0)
    }

    fun click() {
        adapter.onItemClickListener = { item, Etiqueta ->
            when (Etiqueta) {
                EDITAR -> {
                }
                DONAR -> {
                    NavigationFragment.Builder()
                        .setActivity(requireActivity())
                        .setView(requireView().parent as ViewGroup)
                        .setFragment(EventDonorFragment.newInstance(callBack) as Fragment)
                        .setBundle(Bundle().apply {
                            putString("diner_id", item.fCCOMEDORID)
                        })
                        .build().nextWithReplace()
                }
                PARTICIPAR ->{
                    NavigationFragment.Builder()
                        .setActivity(requireActivity())
                        .setView(requireView().parent as ViewGroup)
                        .setFragment(EventVolunteerFragment.newInstance(callBack) as Fragment)
                        .setBundle(Bundle().apply {
                            putString("diner_id", item.fCCOMEDORID)
                        })
                        .build().nextWithReplace()
                }
                "" -> {
                }
            }
        }
    }
}




