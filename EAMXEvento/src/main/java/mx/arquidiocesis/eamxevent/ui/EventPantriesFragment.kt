package mx.arquidiocesis.eamxevent.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_event_detail.*
import kotlinx.android.synthetic.main.fragment_event_pantries.*
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
import mx.arquidiocesis.eamxevent.adapter.PantryAllAdapter
import mx.arquidiocesis.eamxevent.databinding.FragmentEventPantriesBinding
import mx.arquidiocesis.eamxevent.model.Pantry
import mx.arquidiocesis.eamxevent.model.ViewModelEvent
import mx.arquidiocesis.eamxevent.model.enum.Delegations
import mx.arquidiocesis.eamxevent.model.enum.Participation
import mx.arquidiocesis.eamxevent.repository.RepositoryEvent

class EventPantriesFragment : FragmentBase() {

    lateinit var binding: FragmentEventPantriesBinding
    lateinit var viewmodel: ViewModelEvent
    lateinit var adapterPantry: PantryAllAdapter
    private var zona: Int = 0
    private var type: Int = 0
    private var participation: Array<Participation> = Participation.values()
    private var delegations: Array<Delegations> = Delegations.values()
    private var init = true
    private var pantry_id = ""
    private var userId = 0

    companion object {
        fun newInstance(callBack: EAMXHome): EventPantriesFragment {
            var fragment = EventPantriesFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewmodel = ViewModelEvent(RepositoryEvent(requireContext()))
        binding = FragmentEventPantriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "Home_ActividadesDespensas")
            })
        }
        callBack.showToolbar(true, AppMyConstants.eventoDespensas)
        init = true
        userId = eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID.name,
            EAMXTypeObject.INT_OBJECT
        ) as Int
        setupInit(msgGuest(isMsg = false))
        initObservers()
        initButtons()
    }

    private fun initObservers() {
        viewmodel.responseAllPan.observe(viewLifecycleOwner) { item ->
            if (item.size > 0) {
                if (item[0].id != null) {

                    if (init) { //Segunda vez
                        item.forEach {
                            if (it.user_id == userId) {
                                pantry_id = it.id.toString()
                                tvNewDespensa.setText(AppMyConstants.updatePantry)
                                return@forEach
                            }
                        }
                    }

                    init = false
                    val despensas = item.filter {
                        if (zona == 0) it.status == 1 else it.zone_id == zona && it.status == 1
                    }
                    if (despensas.size > 0) {
                        adapterPantry.items.clear()
                        adapterPantry.notifyDataSetChanged()

                        adapterPantry.items.addAll(despensas)
                        adapterPantry.notifyDataSetChanged()
                        val prevSize = adapterPantry.items.size
                        if (prevSize != 0) {
                            adapterPantry.notifyItemRangeInserted(
                                prevSize,
                                adapterPantry.items.count() - 1
                            )
                        }
                    }
                }
            }
            if (adapterPantry.items.size == 0) {
                adapterPantry.items.addAll(arrayListOf(Pantry()))
                adapterPantry.notifyDataSetChanged()
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

    private fun initButtons() {
        tvNewDespensa.setOnClickListener {
            if (!init) {
                NavigationFragment.Builder()
                    .setActivity(requireActivity())
                    .setView(requireView().parent as ViewGroup)
                    .setFragment(EventPantriesDetailFragment.newInstance(callBack) as Fragment)
                    .setBundle(Bundle().apply {
                        putString("pantry_id", pantry_id)
                    })
                    .build().nextWithReplace()
            }
        }
        btnComedoresActDespensa.setOnClickListener {
            NavigationFragment.Builder()
                .setActivity(requireActivity())
                .setView(requireView().parent as ViewGroup)
                .setFragment(EventFragment.newInstance(callBack) as Fragment)
                .build().nextWithReplace()
        }
        spZoneDes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                zona = delegations[position].pos
                println(zona)
                getAllPantries()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                lblSeleccion.text = "Sin selección"
            }
        }

        val adaptador = ArrayAdapter.createFromResource(
            requireContext(), R.array.participations,
            android.R.layout.simple_spinner_dropdown_item
        )

        spParticipacionDes.adapter = adaptador
        spParticipacionDes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                type = participation[position].pos
                println(type)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                lblSeleccion.text = "Sin selección"
            }
        }
    }

    fun getAllPantries() {
        showLoader()
        adapterPantry =
            PantryAllAdapter(requireContext(), type)
        adapterPantry.items = arrayListOf()
        setupRecyclerView()
        //click()
        viewmodel.requestAllPantry(0)
    }


}