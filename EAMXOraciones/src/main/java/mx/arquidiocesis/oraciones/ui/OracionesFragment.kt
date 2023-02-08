package mx.arquidiocesis.oraciones.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ExpandableListView.OnGroupClickListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.arquidiocesis.oraciones.R
import com.arquidiocesis.oraciones.databinding.FragmentOracionesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.oraciones.adapter.AdapterExpandible
import mx.arquidiocesis.oraciones.adapter.TipoOracionesAdapter
import mx.arquidiocesis.oraciones.model.OracionesModel
import mx.arquidiocesis.oraciones.model.OracionesTipoModel
import mx.arquidiocesis.oraciones.repository.Repository
import mx.arquidiocesis.oraciones.viewmodel.OracionViewModel

class OracionesFragment : FragmentBase() {

    companion object {
        fun newInstance(): OracionesFragment {
            var oracionFragment = OracionesFragment()
            return oracionFragment
        }
    }


    private lateinit var binding: FragmentOracionesBinding
    private val oracionViewModel: OracionViewModel by lazy {
        getViewModel {
            OracionViewModel(Repository(requireContext()))
        }
    }

    private var oracionesList: MutableList<List<OracionesModel>> = mutableListOf()
    private var oracionTipeList: MutableList<OracionesTipoModel> = mutableListOf()
    private var arrayString: MutableList<String> = mutableListOf()

    private var adapterExpandible: AdapterExpandible? = null
    private var cargar = true
    private var iniciarEdit = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOracionesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "Home_Oraciones")
            })
        }
        callBack = (activity as EAMXHome)
        callBack.showToolbar(true, AppMyConstants.oraciones)

        binding.elvOraciones.setOnGroupClickListener(OnGroupClickListener { parent, v, groupPosition, id ->
            binding.elvOraciones.expandGroup(groupPosition)
            //return true;
            true
        })
        adapterExpandible = AdapterExpandible(
            oracionTipeList,
            requireContext(), oracionesList, binding.elvOraciones
        ) {
            binding.etBusarMisas.text?.clear()
            selectItem(it.id)
        }
        showLoader()
        if (adapterExpandible == null) {

        } else {
            cargar = false

            binding.elvOraciones.apply {
                setAdapter(adapterExpandible)
            }
        }
        binding.etBusarMisas.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cargar = true
                oracionViewModel.oracionesBuscar(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        binding.etBusarMisas.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            showLoader()
            cargar = true
            oracionViewModel.oracionesBuscar(binding.etBusarMisas.text.toString())

        })


        binding.ibBusacar.setOnClickListener {
            showLoader()
            cargar = true
            oracionViewModel.oracionesBuscar(binding.etBusarMisas.text.toString())
        }

        binding.etBusarMisas.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                showLoader()
                if (binding.etBusarMisas.text!!.isEmpty()) {
                    GlobalScope.launch(Dispatchers.IO) {
                        oracionViewModel.oracionTipo()
                    }
                } else {
                    oracionViewModel.oracionesBuscar(binding.etBusarMisas.text.toString())
                }
            }
            false
        }

        GlobalScope.launch(Dispatchers.IO) {
            oracionViewModel.oracionTipo()
        }
        initObservers()

    }

    private fun initObservers() {
        oracionViewModel.tipoResponse.observe(viewLifecycleOwner) {
            it?.let {
                binding.elvOraciones.setOnGroupClickListener(OnGroupClickListener { parent, v, groupPosition, id ->
                    binding.elvOraciones.expandGroup(groupPosition)
                    //return true;
                    true
                })
                binding.elvOraciones.apply {
                    if (oracionTipeList.isEmpty()) {
                        oracionTipeList = it as MutableList<OracionesTipoModel>
                        adapterExpandible?.updateReceiptsList(oracionTipeList)
                        setAdapter(adapterExpandible)
                    }
                }
                if (oracionViewModel.expandibleInSearch) {
                    oracionTipeList.forEachIndexed { index, _ ->
                        adapterExpandible?.expandableListView?.expandGroup(index)
                    }
                }
                binding.rcBusqueda.apply {
                    layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    if (it.isNotEmpty()) {
                        adapter = TipoOracionesAdapter(it[1].devotions, requireContext()) {
                            selectItem(it.id)
                        }

                        if (iniciarEdit) {
                            it.forEach {
                                arrayString.add(it.name)
                                it.devotions.forEach {
                                    arrayString.add(it.name)
                                }
                            }
                            var adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_list_item_1, arrayString
                            )
                            binding.etBusarMisas.setAdapter(adapter)
                            iniciarEdit = false
                        }

                    }
                }
            }

            hideLoader()
        }
        oracionViewModel.buscarResponse.observe(viewLifecycleOwner) {
            it?.let {
                binding.elvOraciones.setOnGroupClickListener(OnGroupClickListener { parent, v, groupPosition, id ->
                    binding.elvOraciones.expandGroup(groupPosition)
                    true
                })

                binding.elvOraciones.apply {
                    if (cargar) {
                        oracionTipeList = it as MutableList<OracionesTipoModel>
                        adapterExpandible?.updateReceiptsList(oracionTipeList)
                        setAdapter(adapterExpandible)

                    }

                }
                if (oracionViewModel.expandibleInSearch) {
                    oracionTipeList.forEachIndexed { index, _ ->
                        adapterExpandible?.expandableListView?.expandGroup(index)
                    }
                }
            }
            hideLoader()
        }
        oracionViewModel.errorResponse.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setListener {
                    requireActivity().onBackPressed()
                }
                .setMessage(it).build().show(childFragmentManager, "")
        }
    }

    private fun selectItem(id: Int) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        val fragment = DetalleOracionFragment.newInstance()
        val bundle = Bundle()
        bundle.putInt("idOracion", id)
        fragment.arguments = bundle
        transaction.replace((requireView().parent as ViewGroup).id, fragment).addToBackStack(tag)
            .commit()
    }
}