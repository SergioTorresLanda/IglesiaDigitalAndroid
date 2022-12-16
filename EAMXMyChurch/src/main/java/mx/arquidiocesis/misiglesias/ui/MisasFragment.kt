package mx.arquidiocesis.misiglesias.ui

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.base.LocationPermissionHelper
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.misiglesias.databinding.FragmentMisasBinding
import mx.arquidiocesis.misiglesias.adapters.MassesAdapter
import mx.arquidiocesis.misiglesias.model.MassesModelM
import mx.arquidiocesis.misiglesias.repository.RepositoryMeasses
import mx.arquidiocesis.misiglesias.viewmodel.MisasViewModel

class MisasFragment(val context: FragmentActivity) : FragmentBase() {
    companion object {
        fun newInstance(context: FragmentActivity) = MisasFragment(context)
    }

    lateinit var binding: FragmentMisasBinding
    private val viewModel: MisasViewModel by lazy {
        getViewModel {
            MisasViewModel(RepositoryMeasses(requireContext()))
        }
    }
    private var locacion: Location? = null
    private var recargar: Boolean = false
    lateinit var massAdapter: MassesAdapter
    var list: MutableList<MassesModelM> = mutableListOf<MassesModelM>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMisasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!LocationPermissionHelper.hasPermission(requireActivity())) {
            LocationPermissionHelper.requestPermission(requireActivity())
            return iniciar()
        } else {
            iniciar()
        }
    }

    private fun initObservers() {
        viewModel.response.observe(viewLifecycleOwner) {
            list = it as MutableList<MassesModelM>
            massAdapter.updateList(it as MutableList<MassesModelM>)
            viewModel.getLocation(requireContext())
            binding.rcMisas.apply {
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                adapter = massAdapter
            }
            hideLoader()
        }
        viewModel.buscarRespose.observe(viewLifecycleOwner) {
            massAdapter.updateList(it as MutableList<MassesModelM>)
            hideLoader()
        }
        viewModel.locationResponse.observe(viewLifecycleOwner) {
            locacion=it
            if (!recargar) {
                recargar=true
                viewModel.misasList(it)
            }
        }

        viewModel.errorResponse.observe(viewLifecycleOwner) {
            println("ErrorResponse")
            hideLoader()
        }
    }

    private fun iniciar() {
        massAdapter = MassesAdapter(
            list,
            requireContext(),
            binding.rcMisas
        ) {
          /*  val intent = Intent(context, DetailChurchActivity::class.java).apply {
                putExtra("idIglesia", it.id)
            }
            startActivity(intent)*/
        }
        binding.ibBusacar.setOnClickListener {
            showLoader()
            viewModel.buscarList(binding.etBusarMisas.text.toString(),locacion)

        }
        viewModel.getLocation(requireContext())
        showLoader()
        initObservers()
    }
}
