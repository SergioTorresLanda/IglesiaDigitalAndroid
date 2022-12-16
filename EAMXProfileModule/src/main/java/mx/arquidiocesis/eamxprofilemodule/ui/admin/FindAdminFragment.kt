package mx.arquidiocesis.eamxprofilemodule.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.*
import mx.arquidiocesis.eamxcommonutils.customui.loader.UtilLoader
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxprofilemodule.adapter.AdminAdapterCb
import mx.arquidiocesis.eamxprofilemodule.databinding.FragmentFindAdminBinding
import mx.arquidiocesis.eamxprofilemodule.model.CollaboratorModel
import mx.arquidiocesis.eamxprofilemodule.repository.RepositoryProfile
import mx.arquidiocesis.eamxprofilemodule.viewmodel.CollaboratorViewModel

lateinit var adminAdapterCb: AdminAdapterCb

class FindAdminFragment : FragmentBase() {

    companion object {
        fun newInstance(): FindAdminFragment =
            FindAdminFragment()
    }

    private val viewModel: CollaboratorViewModel by lazy {
        getViewModel {
            CollaboratorViewModel(
                RepositoryProfile(requireContext())
            )
        }
    }

    var location : Int

    init{
        val church = eamxcu_preferences.getData(EAMXEnumUser.CHURCH.name, EAMXTypeObject.INT_OBJECT) as Int
        val community = eamxcu_preferences.getData(EAMXEnumUser.USER_COMMUNITY_ID.name, EAMXTypeObject.INT_OBJECT) as Int
        val user = eamxcu_preferences.getData(EAMXEnumUser.USER_PROFILE.name, EAMXTypeObject.STRING_OBJECT) as String
        location = when(user){
            EAMXProfile.Priest.rol,
            EAMXProfile.DeanPriest.rol,
            EAMXProfile.PriestAdmin.rol -> church
            EAMXProfile.CommunityResponsible.rol -> community
            else -> 0
        }
    }

    lateinit var binding: FragmentFindAdminBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFindAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        showLoader()

        viewModel.getCollaborators(location, "")

        binding.includeToolbar.toolbarTitle.text = "Nombrar Administradores"
        binding.includeToolbar.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.svCollaborator.isIconified = false

        binding.svCollaborator.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean = false

            override fun onQueryTextChange(p0: String?): Boolean {
                adminAdapterCb.filter.filter(p0)
                return false
            }
        })

    }

    fun initObservers() {
        viewModel.getCollaboratorsResponse.observe(viewLifecycleOwner) { response ->
            hideLoader()
            adminAdapterCb = AdminAdapterCb(response as ArrayList<CollaboratorModel>) {
                /*val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.contentFragmentPerfil, AdminDetailFragment.newInstance(it))
                transaction.addToBackStack(EAMXEnums.EMAIL.name)
                transaction.commit()*/
            }

            binding.rvAdmins.apply {
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                adapter = adminAdapterCb
            }
        }
    }
}