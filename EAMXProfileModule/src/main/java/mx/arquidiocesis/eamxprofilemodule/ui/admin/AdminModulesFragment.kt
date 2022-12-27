package mx.arquidiocesis.eamxprofilemodule.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.*
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
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

                /*val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.contentFragmentProfile, MyModulesFragment.newInstance())
                transaction.addToBackStack("")
                transaction.commit()*/
            }

            cvNameAdmin.visibility = if (showAdminSection) View.VISIBLE else View.GONE

            cvNameAdmin.setOnClickListener {
                NavigationFragment.Builder()
                    .setActivity(requireActivity())
                    .setView(requireView().parent as ViewGroup)
                    .setFragment(NameAdminFragment.newInstance())
                    .setAllowStack(false)
                    .build().nextWithReplace()
                /*val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.contentFragmentProfile, NameAdminFragment.newInstance())
                transaction.addToBackStack("")
                transaction.commit()
            */
            }

            cvLogOut.setOnClickListener {
                (requireActivity() as EAMXSignOut).signOut(true)
                /*MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Aviso")
                    .setMessage("Se borrará tu información de acceso.")
                    .setPositiveButton("Eliminar") { _, _ ->
                        showLoader()
                       eamxcu_preferences.saveData("USER_PASSWORD","")
                        (requireActivity() as EAMXSignOut).signOut(true)
                    }.setNegativeButton("Cancelar") { _, _ ->

                    }.show()
                */
            }
        }
    }
}