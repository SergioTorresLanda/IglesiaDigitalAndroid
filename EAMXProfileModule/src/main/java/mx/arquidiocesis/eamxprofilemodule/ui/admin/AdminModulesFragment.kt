package mx.arquidiocesis.eamxprofilemodule.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.*
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxcommonutils.util.userAllowAccessAsAdmin
import mx.arquidiocesis.eamxprofilemodule.databinding.FragmentAdminModulesBinding

class AdminModulesFragment : FragmentBase() {

    lateinit var binding: FragmentAdminModulesBinding

    companion object {
        fun newInstance(): AdminModulesFragment {
            return AdminModulesFragment()
        }
    }

    var showAdminSection = false

    init {
        showAdminSection =
            userAllowAccessAsAdmin(EAMXEnumUser.USER_PERMISSION_ADMIN_MANAGEMENT.name)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAdminModulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            (requireActivity() as EAMXHome).showToolbar(false, "")
            includeToolbar.toolbarTitle.text = "Administrar módulos"
            includeToolbar.ivBack.setOnClickListener {
                activity?.onBackPressed()
            }
            cvAdminModules.visibility = View.GONE
            cvAdminModules.setOnClickListener {
                NavigationFragment.Builder()
                    .setActivity(requireActivity())
                    .setView(requireView().parent as ViewGroup)
                    .setFragment(MyModulesFragment.newInstance())
                    .setAllowStack(false)
                    .build().nextWithReplace()
            }
            cvNameAdmin.visibility = if (showAdminSection) View.VISIBLE else View.GONE
            cvNameAdmin.setOnClickListener {
                NavigationFragment.Builder()
                    .setActivity(requireActivity())
                    .setView(requireView().parent as ViewGroup)
                    .setFragment(NameAdminFragment.newInstance())
                    .setAllowStack(false)
                    .build().nextWithReplace()
            }
            cvLogOut.setOnClickListener {
                (requireActivity() as EAMXSignOut).signOut(true)
            }
        }
    }
}