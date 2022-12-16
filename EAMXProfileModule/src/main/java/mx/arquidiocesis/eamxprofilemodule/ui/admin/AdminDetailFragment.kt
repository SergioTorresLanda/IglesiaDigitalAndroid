package mx.arquidiocesis.eamxprofilemodule.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.customui.loader.UtilLoader
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxprofilemodule.R
import mx.arquidiocesis.eamxprofilemodule.adapter.ModuleDetailAdapter
import mx.arquidiocesis.eamxprofilemodule.adapter.ServiceDetailAdapter
import mx.arquidiocesis.eamxprofilemodule.databinding.FragmentAdminDetailBinding
import mx.arquidiocesis.eamxprofilemodule.model.CollaboratorDetailModel
import mx.arquidiocesis.eamxprofilemodule.model.CollaboratorModel
import mx.arquidiocesis.eamxprofilemodule.model.Module
import mx.arquidiocesis.eamxprofilemodule.model.ServiceModel
import mx.arquidiocesis.eamxprofilemodule.repository.RepositoryProfile
import mx.arquidiocesis.eamxprofilemodule.viewmodel.CollaboratorViewModel

class AdminDetailFragment : Fragment() {

    private val loader by lazy { UtilLoader() }
    lateinit var collaborator: CollaboratorModel
    lateinit var collaboratorDetail: CollaboratorDetailModel
    lateinit var binding: FragmentAdminDetailBinding

    companion object {
        fun newInstance(collaborator: CollaboratorModel): AdminDetailFragment {
            val fragment = AdminDetailFragment()
            fragment.collaborator = collaborator
            return fragment
        }
    }

    private val viewModel: CollaboratorViewModel by lazy {
        getViewModel {
            CollaboratorViewModel(
                    RepositoryProfile(requireContext())
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAdminDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()

        binding.toolbarTitle.text = "Nombrar Administradores"
        binding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.tvName.text =
            "${collaborator.name} ${collaborator.firstSurname} ${collaborator.secondSurname}"

        collaborator.isSuperAdmin?.let {
            if (it) binding.ivStar.visibility = View.VISIBLE
        }

        collaborator.id?.let {
            showLoader()
            val church =  (eamxcu_preferences.getData(EAMXEnumUser.CHURCH.name, EAMXTypeObject.STRING_OBJECT) as String).toInt()
            viewModel.getCollaboratorDetail(church, it)
        }

        binding.ivDoAdmin.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.contentFragmentProfile, MyModulesFragment.newInstanceWithCollaborator(collaborator, collaboratorDetail))
            transaction.addToBackStack("")
            transaction.commit()
        }

        /*binding.tvDoAdmin.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.contentFragmentPerfil,
                MyModulesFragment.newInstanceWithCollaborator(collaborator, collaboratorDetail))
            transaction.addToBackStack("")
            transaction.commit()
        }*/
    }

    fun initObservers() {
        viewModel.collaboratorDetailResponse.observe(viewLifecycleOwner) { response ->

            collaboratorDetail = response

            binding.apply {
                tetStyleLife.setText(response.lifeStatus)
                tetChurch.setText(response.location?.name)
                tetEmail.setText(response.email)
                tetJob.setText(response.serviceModels?.get(0)?.name)

                response.serviceModels?.let {
                    if (it.isEmpty()) {
                        cvSinServicios.visibility = View.VISIBLE
                        rvServices.visibility = View.GONE
                    } else {

                        cvSinServicios.visibility = View.GONE
                        rvServices.visibility = View.VISIBLE

                        rvServices.apply {
                            layoutManager =
                                GridLayoutManager(activity, 2, GridLayoutManager.HORIZONTAL, false)
                            adapter =
                                ServiceDetailAdapter(response.serviceModels as ArrayList<ServiceModel>)
                        }
                    }
                }


                val modules = response.modules?.filter {
                    it.enabled == true
                }

                modules?.let {
                    if (it.isEmpty()) {
                        cvSinModulos.visibility = View.VISIBLE
                        rvModules.visibility = View.GONE
                    } else {

                        cvSinModulos.visibility = View.GONE
                        rvModules.visibility = View.VISIBLE

                        rvModules.apply {
                            layoutManager =
                                GridLayoutManager(activity, 2, GridLayoutManager.HORIZONTAL, false)
                            adapter = ModuleDetailAdapter(modules as ArrayList<Module>)
                        }
                    }
                }
            }

            hideLoader()
        }

        viewModel.errorResponse.observe(viewLifecycleOwner) { response ->
            UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_warning))
                    .setMessage("Ocurrio un error, intentalo más tarde")
                    .build().show(childFragmentManager, "")

            binding.apply {
                //tvDoAdmin.isEnabled = false
                ivDoAdmin.isEnabled = false
            }

            hideLoader()
        }
        viewModel.collaboratorDetailResponse.observe(viewLifecycleOwner) { response ->

            collaboratorDetail = response

            binding.apply {
                tetStyleLife.setText(response.lifeStatus)
                tetChurch.setText(response.location?.name)
                tetEmail.setText(response.email)

                response.serviceModels?.let {
                    if (it.isEmpty()) {
                        cvSinServicios.visibility = View.VISIBLE
                        rvServices.visibility = View.GONE
                    } else {

                        cvSinServicios.visibility = View.GONE
                        rvServices.visibility = View.VISIBLE

                        rvServices.apply {
                            layoutManager =
                                GridLayoutManager(activity, 2, GridLayoutManager.HORIZONTAL, false)
                            adapter =
                                ServiceDetailAdapter(response.serviceModels as ArrayList<ServiceModel>)
                        }
                    }
                }


                val modules = response.modules?.filter {
                    it.enabled == true
                }

                modules?.let {
                    if (it.isEmpty()) {
                        cvSinModulos.visibility = View.VISIBLE
                        rvModules.visibility = View.GONE
                    } else {

                        cvSinModulos.visibility = View.GONE
                        rvModules.visibility = View.VISIBLE

                        rvModules.apply {
                            layoutManager =
                                GridLayoutManager(activity, 2, GridLayoutManager.HORIZONTAL, false)
                            adapter = ModuleDetailAdapter(modules as ArrayList<Module>)
                        }
                    }
                }
            }

            hideLoader()
        }

        viewModel.errorResponseExit.observe(viewLifecycleOwner) { response ->
            UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_warning))
                    .setMessage(response)
                    .setListener {
                        activity?.onBackPressed()
                    }
                    .build().show(childFragmentManager, "")

            binding.apply {
                //tvDoAdmin.isEnabled = false
                ivDoAdmin.isEnabled = false
            }

            hideLoader()
        }
    }

    protected fun showLoader(tag: String = "") {
        if (!loader.isAdded) {
            loader.show(childFragmentManager, tag)
        }
    }

    protected fun hideLoader() {
        if (loader.isAdded) {
            loader.dismissAllowingStateLoss()
        }
    }
}