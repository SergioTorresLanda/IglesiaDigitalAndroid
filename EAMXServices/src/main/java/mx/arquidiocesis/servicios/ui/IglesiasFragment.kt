package mx.arquidiocesis.servicios.ui

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_iglesias.*
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.permission.UtilValidPermission
import mx.arquidiocesis.eamxmaps.ui.MapFragment
import mx.arquidiocesis.servicios.R
import mx.arquidiocesis.servicios.adapter.MassesAdapter
import mx.arquidiocesis.servicios.model.IglesiasModel
import mx.arquidiocesis.servicios.model.ServiceModel
import mx.arquidiocesis.servicios.repository.Repository
import mx.arquidiocesis.servicios.viewModel.MisasViewModel


class IglesiasFragment(val serviceModel: ServiceModel) : FragmentBase() {

    private val viewModel: MisasViewModel by lazy {
        getViewModel {
            MisasViewModel(Repository(requireContext()))
        }
    }
    private lateinit var fragmentMap: MapFragment

    var list: MutableList<IglesiasModel> = mutableListOf<IglesiasModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_iglesias, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniciar()
    }

    private fun initObservers() {
        viewModel.postServicesResponse.observe(viewLifecycleOwner) {
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setIsCancel(false)
                .setMessage("Servicio registrado.")
                .setListener { requireActivity().onBackPressed() }
                .build()
                .show(childFragmentManager, "")

            hideLoader()
        }
        viewModel.errorResponse.observe(viewLifecycleOwner) {
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .setIsCancel(false)
                .setListener { requireActivity().onBackPressed() }
                .build()
                .show(childFragmentManager, "")

            hideLoader()
        }
    }


    private fun iniciar() {
        initObservers()
        changeMap()
    }

    private fun changeMap() {
        fragmentMap = MapFragment(true) { item, location ->
            val family = requireArguments().getString("SURNAME_FULL_NAME")
            val adrees = requireArguments().getString("ADDRESS_BLESSING")
            val zipCode = requireArguments().getString("POSTAL_CODE")
            val colonia = requireArguments().getString("SUBURB_BLESSING")
            val description = requireArguments().getString("JUSTIFICATION")
            showLoader()
            if (location != null) {
                viewModel.setService(
                    serviceModel,
                    item.id,
                    family!!,
                    adrees!!,
                    zipCode!!,
                    colonia!!,
                    location!!.longitude,
                    location!!.latitude,
                    description

                )
            } else {
                UtilAlert
                    .Builder()
                    .setTitle("Aviso")
                    .setMessage("No se pudo obtener su localización.")
                    .setListener { requireActivity().onBackPressed() }
                    .setIsCancel(false)
                    .build()
                    .show(childFragmentManager, "")

                hideLoader()

            }

        }

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flMisas, fragmentMap).disallowAddToBackStack()
            .commit()
    }
}
