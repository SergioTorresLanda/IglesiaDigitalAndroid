package mx.arquidiocesis.eamxevent.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_event_detail.*
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import mx.arquidiocesis.eamxcommonutils.util.log
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxevent.databinding.FragmentEventBinding
import mx.arquidiocesis.eamxevent.model.*
import mx.arquidiocesis.eamxevent.model.enum.Delegations
import mx.arquidiocesis.eamxevent.repository.RepositoryEvent

const val EDITAR = "EDITAR"
class EventFragment : FragmentBase() {

    lateinit var binding: FragmentEventBinding
    lateinit var viewmodel: ViewModelEvent
    lateinit var adapter: DinerAllAdapter
    private var zona: Int = 0
    private var delegations: Array<Delegations> = Delegations.values()

    companion object {
        fun newInstance(callBack: EAMXHome): EventFragment {
            var fragment = EventFragment()
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
        binding = FragmentEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callBack.showToolbar(true, AppMyConstants.evento)
        binding.tvNewActivity.setOnClickListener {
            NavigationFragment.Builder()
                .setActivity(requireActivity())
                .setView(requireView().parent as ViewGroup)
                .setFragment(EventDetailFragment.newInstance(callBack) as Fragment)
                .build().nextWithReplace()
        }
        initObservers()
        initButtons()

    }

    private fun initObservers() {
        getAllDiners()
        viewmodel.responseAllDin.observe(viewLifecycleOwner) { item ->
            if (item.size > 0) {
                if (item[0].fCCOMEDORID != null) {
                    val comedores = if (zona == 0) item else item.filter { it.fIZONA == zona.toString() }
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
        /*
        val adaptador = ArrayAdapter.createFromResource(
            requireContext(), R.array.delegations,
            android.R.layout.simple_spinner_dropdown_item
        )
        spZone.adapter = adaptador

         */
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
    }

    fun getAllDiners() {
        adapter =
            DinerAllAdapter(requireContext(), viewmodel.getFine())
        adapter.items = arrayListOf(DinerResponse())
        setupRecyclerView()
        click()
        viewmodel.requestAllDiner(0)
    }

    fun click() {
        val interactuar = "interactuar con el contenido de la red social"
        adapter.onItemClickListener = { item, Etiqueta ->
            when (Etiqueta) {
                EDITAR -> {
                    "si entre editar".log()
                   //7 model.value = item
                    //showBottonSheeat()
                }
                "" -> {
                        ("yane").log()

                }
            }
        }
    }
}




