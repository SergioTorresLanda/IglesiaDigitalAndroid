package com.wallia.eamxcomunidades.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.wallia.eamxcomunidades.R
import com.wallia.eamxcomunidades.adapter.ComunitySearchAdapter
import com.wallia.eamxcomunidades.database.instance.MeetRoomDataBaseCommunity
import com.wallia.eamxcomunidades.repository.Repository
import com.wallia.eamxcomunidades.viewmodel.COMUNITYID
import com.wallia.eamxcomunidades.viewmodel.ComunidadesViewModel
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import com.wallia.eamxcomunidades.databinding.FragmentEamxSearchBinding
import com.wallia.eamxcomunidades.viewmodel.PRINCIPAL
import com.wallia.eamxcomunidades.viewmodel.SEARCH
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.util.getViewModel


class EAMXSearchFragment : FragmentBase() {

    lateinit var binding: FragmentEamxSearchBinding
    private val viewModel: ComunidadesViewModel by lazy {
        getViewModel {
            ComunidadesViewModel(Repository(context = requireContext(), database = MeetRoomDataBaseCommunity.getRoomInstance(requireContext())))
        }
    }
    private var isPrincipal = false


    companion object {
        @JvmStatic
        fun newInstance(): EAMXSearchFragment {
            val EAMXSearchFragment = EAMXSearchFragment()
            return EAMXSearchFragment
        }
    }

    private fun initObservers() {

        viewModel.getCommunitiesByNameResponse.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.apply {

                    val comunitySearchAdapter = ComunitySearchAdapter(it, requireContext()) {
                        changeFragmen(it.id!!)
                    }
                    rvComunity.apply {
                        layoutManager =
                            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                        adapter = comunitySearchAdapter

                    }

                }

            }
            hideLoader()
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_eamx_search,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isPrincipal = requireArguments().getBoolean(PRINCIPAL)
        val search = requireArguments().getString(SEARCH)
        initObservers()


        showLoader()
        binding.apply {
            svBusarComunidad.setQuery(search, false)
            viewModel.getCommunitiesByName(svBusarComunidad.query.toString())
            svBusarComunidad.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (svBusarComunidad.query.isNotEmpty()) {
                        viewModel.getCommunitiesByName(svBusarComunidad.query.toString())
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                    }
                    return false
                }
            })
            ibSearch.setOnClickListener {
                viewModel.getCommunitiesByName(svBusarComunidad.query.toString())
                showLoader()
            }
            tvGeo.setOnClickListener {
                changeFragmen()
            }

        }


    }

    private fun changeFragmen() {
        val bundle = Bundle()
        bundle.apply {
            putBoolean(PRINCIPAL, isPrincipal)
        }
        NavigationFragment.Builder()
            .setActivity(requireActivity())
            .setView(requireView().parent as ViewGroup)
            .setFragment(EAMXMapFragment.newInstance())
            .setBundle(bundle)
            .build().nextWithReplace()
    }

    private fun changeFragmen(id: Int?) {
        val bundle = Bundle()
        bundle.apply {
            putInt(COMUNITYID, id ?: 0)
            bundle.putBoolean(PRINCIPAL, isPrincipal)
        }
        NavigationFragment.Builder()
            .setActivity(requireActivity())
            .setView(requireView().parent as ViewGroup)
            .setFragment(EAMXDetalleComunidadFragment.newInstance() as Fragment)
            .setBundle(bundle)
            .build().nextWithReplace()
    }


}