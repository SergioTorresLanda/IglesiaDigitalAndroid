package mx.arquidiocesis.eamxevent.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_event_detail.*
import kotlinx.android.synthetic.main.fragment_event_other.tvNewOther
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
import mx.arquidiocesis.eamxevent.adapter.OtherAllAdapter
import mx.arquidiocesis.eamxevent.adapter.PantryAllAdapter
import mx.arquidiocesis.eamxevent.databinding.FragmentEventOtherBinding
import mx.arquidiocesis.eamxevent.databinding.FragmentEventPantriesBinding
import mx.arquidiocesis.eamxevent.model.OtherEvent
import mx.arquidiocesis.eamxevent.model.Pantry
import mx.arquidiocesis.eamxevent.model.ViewModelEvent
import mx.arquidiocesis.eamxevent.model.enum.Participation
import mx.arquidiocesis.eamxevent.repository.RepositoryEvent

class EventOtherFragment : FragmentBase() {

    lateinit var binding: FragmentEventOtherBinding
    lateinit var viewmodel: ViewModelEvent
    lateinit var adapterOther: OtherAllAdapter
    private var type: Int = 0
    private var participation: Array<Participation> = Participation.values()
    private var init = true
    private var event_id = ""
    private var userId = 0

    companion object {
        fun newInstance(callBack: EAMXHome): EventOtherFragment {
            var fragment = EventOtherFragment()
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
        userId = eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID.name,
            EAMXTypeObject.INT_OBJECT
        ) as Int
        setupInit(msgGuest(isMsg = false))
        initObservers()
        initButtons()
    }

    private fun initObservers() {
        viewmodel.responseAllOther.observe(viewLifecycleOwner) { item ->
            if (item.isNotEmpty()) {
                if (item[0].eventoId != null) {
                    if (init) {
                        item.forEach {
                            if (it.userId == userId) {
                                event_id = it.eventoId.toString()
                                tvNewOther.text = AppMyConstants.updateOther
                                return@forEach
                            }
                        }
                    }

                    init = false
                    val other = item.filter {
                        it.status == 1
                    }
                    if (other.isNotEmpty()) {
                        adapterOther.items.clear()
                        adapterOther.notifyDataSetChanged()
                        adapterOther.items.addAll(other)
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
        tvNewOther.setOnClickListener {
            if (!init) {
                NavigationFragment.Builder()
                    .setActivity(requireActivity())
                    .setView(requireView().parent as ViewGroup)
                    .setFragment(EventOtherDetailFragment.newInstance(callBack) as Fragment)
                    .setBundle(Bundle().apply {
                        putString("event_id", event_id)
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
                //zona = delegations[position].pos
                //println(zona)
                getAllOthers()
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

    fun getAllOthers() {
        showLoader()
        adapterOther =
            OtherAllAdapter(requireContext(), type)
        adapterOther.items = arrayListOf()
        setupRecyclerView()
        viewmodel.requestAllOther(0)
    }
}