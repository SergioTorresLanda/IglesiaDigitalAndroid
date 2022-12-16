package mx.arquidiocesis.servicios.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.servicios.databinding.FragmentServicesAdminMainBinding
import mx.arquidiocesis.servicios.ui.SacramentsFragment

class ServiceAdminMainFragment : FragmentBase() {

    private lateinit var binding: FragmentServicesAdminMainBinding

    companion object {
        @JvmStatic
        fun newInstance(callBack: EAMXHome) =
            ServiceAdminMainFragment().apply {
                this.callBack = callBack
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as EAMXHome).showToolbar(true, AppMyConstants.servicios)
        binding = FragmentServicesAdminMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.apply {

            cvIntentionsChurch.setOnClickListener {
                NavigationFragment.Builder()
                    .setActivity(requireActivity())
                    .setView(requireView().parent as ViewGroup)
                    .setFragment(ServicesAdminIntentionsChurchFragment.newInstance(callBack))
                    .setAllowStack(true)
                    .build().nextWithReplace()
            }

            cvIntentionsCommunity.visibility = View.GONE
            cvIntentionsCommunity.setOnClickListener {
                NavigationFragment.Builder()
                    .setActivity(requireActivity())
                    .setView(requireView().parent as ViewGroup)
                    .setFragment(ServicesAdminIntentionsCommunityFragment.newInstance(callBack))
                    .setAllowStack(true)
                    .build().nextWithReplace()
            }

            cvServices.setOnClickListener {
                NavigationFragment.Builder()
                    .setActivity(requireActivity())
                    .setView(requireView().parent as ViewGroup)
                    .setFragment(ServiceAdminTabFragment.newInstance(callBack))
                    .setAllowStack(true)
                    .build().nextWithReplace()
            }

            cvSacraments.setOnClickListener {
                NavigationFragment.Builder()
                    .setActivity(requireActivity())
                    .setView(requireView().parent as ViewGroup)
                    .setFragment(SacramentsFragment.newInstance(callBack))
                    .setAllowStack(true)
                    .build().nextWithReplace()
            }
        }
    }
}