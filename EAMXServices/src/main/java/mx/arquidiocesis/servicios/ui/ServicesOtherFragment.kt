package mx.arquidiocesis.servicios.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_menu_other_services.*
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.servicios.R
import mx.arquidiocesis.servicios.adapter.ExpandableAdapterServices
import mx.arquidiocesis.servicios.databinding.FragmentMenuOtherServicesBinding
import mx.arquidiocesis.servicios.model.ServiceModel
import mx.arquidiocesis.servicios.model.ServicesModel
import mx.arquidiocesis.servicios.model.TitleExpanderListModel
import mx.arquidiocesis.servicios.model.TypeModel
import mx.arquidiocesis.servicios.repository.Repository
import mx.arquidiocesis.servicios.viewModel.ServiciosViewModel

class ServicesOtherFragment : FragmentBase() {

    private val viewModel: ServiciosViewModel by lazy {
        getViewModel {
            ServiciosViewModel(Repository(requireContext()))
        }
    }
    private lateinit var binding: FragmentMenuOtherServicesBinding
    private lateinit var expandibleAdapter: ExpandableAdapterServices
    private var listServicesMain: MutableList<TitleExpanderListModel> = mutableListOf()
    private var listService: MutableList<ServicesModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as EAMXHome).showToolbar(true, AppMyConstants.otros_servicios)

        binding = FragmentMenuOtherServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        showLoader()
        if (listServicesMain.size == 0) {
            // listServicesMain.add(TitleExpanderListModel(1, "Celebraciones", listService))
            listServicesMain.add(TitleExpanderListModel(1, "Bendiciones", listService))
            listServicesMain.add(TitleExpanderListModel(2, "Otros servicios", listService))
            expandibleAdapter =
                ExpandableAdapterServices(listServicesMain, elvMenuServices, ::selectItem)

        }
        binding.elvMenuServices.apply {
            setAdapter(expandibleAdapter)
            listServicesMain.forEachIndexed { i, _ -> expandGroup(i) }
        }
        viewModel.getBlessig()
    }

    private fun initObservers() {
       /* viewModel.celebrationResponse.observe(viewLifecycleOwner) {
            expandibleAdapter.updateReceiptsItem(0, it as MutableList<ServicesModel>)
            viewModel.getBlessig()
        }*/
        viewModel.blessigResponse.observe(viewLifecycleOwner) {
            expandibleAdapter.updateReceiptsItem(0, it as MutableList<ServicesModel>)
            viewModel.getOtherServices()

        }
        viewModel.otherServicesResponse.observe(viewLifecycleOwner) {
            expandibleAdapter.updateReceiptsItem(1, it as MutableList<ServicesModel>)
            hideLoader()

        }

        viewModel.errorResponse.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert
                .Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
        }

    }


    private fun selectItem(servicesModel: ServicesModel) {
        val action=servicesModel.action
        when (action) {
            "FORM_BLESSING_HOME" -> {
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                val fragment = ServicesFormFragment(ServiceModel(TypeModel(servicesModel.id,"BLESSING"), null))
                val bundle = Bundle()
                bundle.putBoolean("FLAG_COMMUNION_BLESSING", true)

                fragment.arguments = bundle
                transaction.replace((requireView().parent as ViewGroup).id, fragment)
                    .addToBackStack(tag).commit()
            }
            "FORM_COMMUNION_OF_SICK" -> {
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                val fragment = ServicesFormFragment(ServiceModel(TypeModel(servicesModel.id,"COMMUNION_OF_THE_SICK"), null))
                val bundle = Bundle()
                bundle.putBoolean("FLAG_COMMUNION_BLESSING", false)
                fragment.arguments = bundle
                transaction.replace((requireView().parent as ViewGroup).id, fragment)
                    .addToBackStack(tag).commit()
            }
            "NONE" -> {
                UtilAlert
                    .Builder()
                    .setTitle( servicesModel.name)
                    .setMessage(servicesModel.description)

                    .build()
                    .show(childFragmentManager, "")
            }

        }
    }

}