package mx.arquidiocesis.eamxevent.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_event_actor.*
import kotlinx.android.synthetic.main.fragment_event_detail.lblSeleccion
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxevent.R
import mx.arquidiocesis.eamxevent.adapter.ActorAllAdapter
import mx.arquidiocesis.eamxevent.databinding.FragmentEventActorBinding
import mx.arquidiocesis.eamxevent.model.OtherActor
import mx.arquidiocesis.eamxevent.model.ViewModelEvent
import mx.arquidiocesis.eamxevent.repository.RepositoryEvent

private const val ARG_PARAM = "event_id"
private const val STATUS = "STATUS"

class EventActorFragment : FragmentBase() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentEventActorBinding
    private var event_id: Int = 0
    private lateinit var adapter: ActorAllAdapter
    private var list: ArrayList<OtherActor> = arrayListOf()
    private lateinit var viewmodel: ViewModelEvent
    private val TAG_LOADER: String = "EventActorFragment"
    private var type: Int = 0
    private var actoresG : ArrayList<OtherActor> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            event_id = it.getInt(ARG_PARAM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        viewmodel = ViewModelEvent(RepositoryEvent(requireContext()))
        binding = FragmentEventActorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "CrearActividad_ListaActores")
            })
        }
        callBack.showToolbar(true, AppMyConstants.parts)
        adapter = ActorAllAdapter(requireContext(), list, binding.rvEventActor)
        binding.rvEventActor.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        binding.rvEventActor.adapter = adapter
        initObservers()
        initPicker()
        showLoader()
        click()
        viewModelEvent.requestAllActors()
    }

    fun click() {
        adapter.onItemClickListener = { item, Etiqueta ->
            when (Etiqueta) {
                STATUS -> {
                }
            }
        }
    }

    private fun initObservers() {
        viewModelEvent.responseAllActors.observe(viewLifecycleOwner) { item ->
            println("actores:::")
            println(item)
            println("event_id:::")
            println(event_id)
            adapter.items.clear()
            adapter.notifyDataSetChanged()
            val actores = item.filter {
                it.evento_id == event_id && it.status == 1
            }
            actoresG= actores as ArrayList<OtherActor>
            println(actores)
            if (actoresG.isEmpty()){
                tvVacio.visibility=View.VISIBLE
                rvEventActor.visibility=View.GONE
            }else{
                tvVacio.visibility=View.GONE
                rvEventActor.visibility=View.VISIBLE
            }
            adapter.items.addAll(actores.ifEmpty { arrayListOf(OtherActor())})

            adapter.notifyDataSetChanged()
            hideLoader()
        }
        viewModelEvent.showLoaderView.observe(viewLifecycleOwner) {
            showLoader()
        }
        viewModelEvent.errorResponse.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert.Builder().setTitle(getString(R.string.title_dialog_warning)).setMessage(it).setListener {
                    hideLoader()
                }
                .setIsCancel(false).build().show(childFragmentManager, TAG_LOADER)
        }
    }

    private fun filterListByType(){
        adapter.items.clear()
        adapter.notifyDataSetChanged()
        val aF = if (type==0) {
            actoresG
        }else{
            actoresG.filter {
                it.tipo_actor == type
            }
        }

        if (aF.isEmpty()){
            tvVacio.visibility=View.VISIBLE
            rvEventActor.visibility=View.GONE
        }else{
            tvVacio.visibility=View.GONE
            rvEventActor.visibility=View.VISIBLE
        }
        adapter.items.addAll(aF.ifEmpty { arrayListOf(OtherActor())})
        adapter.notifyDataSetChanged()
    }

    private fun initPicker(){
        val adaptador = ArrayAdapter.createFromResource(
            requireContext(), R.array.participations3,
            android.R.layout.simple_spinner_dropdown_item
        )

        spTipoActor.adapter = adaptador
        spTipoActor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                type = position
                println(type)
                filterListByType()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                lblSeleccion.text = "Sin selección"
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(callBack: EAMXHome): EventActorFragment {
            val fragment = EventActorFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    private val viewModelEvent: ViewModelEvent by lazy {
        getViewModel {
            ViewModelEvent(
                RepositoryEvent(requireContext())
            )
        }
    }
}