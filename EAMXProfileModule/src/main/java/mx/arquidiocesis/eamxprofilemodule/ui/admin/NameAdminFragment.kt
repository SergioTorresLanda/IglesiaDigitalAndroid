package mx.arquidiocesis.eamxprofilemodule.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnums
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.common.ModuleAdminEnabled
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxprofilemodule.R
import mx.arquidiocesis.eamxprofilemodule.adapter.AdminAdapter
import mx.arquidiocesis.eamxprofilemodule.databinding.FragmentNameAdminBinding
import mx.arquidiocesis.eamxprofilemodule.model.CollaboratorModel
import mx.arquidiocesis.eamxprofilemodule.repository.RepositoryProfile
import mx.arquidiocesis.eamxprofilemodule.viewmodel.CollaboratorViewModel
import java.lang.reflect.Type

class NameAdminFragment : FragmentBase() {

    lateinit var adminAdapter: AdminAdapter

    companion object {
        fun newInstance(): NameAdminFragment =
            NameAdminFragment()
    }

    private val viewModel: CollaboratorViewModel by lazy {
        getViewModel {
            CollaboratorViewModel(
                RepositoryProfile(requireContext())
            )
        }
    }

    lateinit var binding: FragmentNameAdminBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentNameAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        showLoader()
        val church=validaIglesias()
        if (church==0) {
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(getString(R.string.church_communiti_empty))
                .setListener {
                    activity?.onBackPressed()
                }
                .build().show(childFragmentManager, "")
        } else {
            viewModel.getCollaborators(church, "")
            binding.includeToolbar.toolbarTitle.text = "Nombrar Administradores"
            binding.includeToolbar.ivBack.setOnClickListener {
                activity?.onBackPressed()
            }

            binding.svCollaborator.isIconified = false

            binding.svCollaborator.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean = false

                override fun onQueryTextChange(p0: String?): Boolean {
                    adminAdapter.filter.filter(p0)
                    return false
                }
            })
        }
    }

    fun initObservers() {
        viewModel.getCollaboratorsResponse.observe(viewLifecycleOwner) { response ->

            hideLoader()

            if(response.isEmpty()){
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_warning))
                    .setMessage(getString(R.string.collaborator_is_empty))
                    .setListener {
                        hideLoader()
                    }
                    .build()
                    .show(childFragmentManager, "")
                return@observe
            }

            adminAdapter = AdminAdapter(response as ArrayList<CollaboratorModel>) {
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.contentFragmentProfile, MyModulesFragment.newInstance(it))
                transaction.addToBackStack(EAMXEnums.EMAIL.name)
                transaction.commit()
            }

            binding.rvAdmins.apply {
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                adapter = adminAdapter
            }
        }
    }
    fun validaIglesias():Int{
        val list = eamxcu_preferences.getData(
            EAMXEnumUser.USER_CHURCH_ALLOW_EDIT.name,
            EAMXTypeObject.STRING_OBJECT
        ).toString().replace("&quot;", "\"")
        if (list.isEmpty()) {
            return 0
        }
        return try {
            val collectionType: Type =
                object : TypeToken<Collection<ModuleAdminEnabled?>?>() {}.type
            val dataList: Collection<ModuleAdminEnabled> = Gson().fromJson(list, collectionType)
            return dataList.first().id
        } catch (ex: Exception) {
            0
        }
    }
}