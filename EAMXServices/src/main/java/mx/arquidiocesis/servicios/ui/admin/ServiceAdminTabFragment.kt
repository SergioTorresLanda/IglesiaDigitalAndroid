package mx.arquidiocesis.servicios.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.servicios.R
import mx.arquidiocesis.servicios.adapter.FragmentAdapter
import mx.arquidiocesis.servicios.databinding.FragmentServicesAdminTabBinding
import mx.arquidiocesis.servicios.model.admin.view.AdminServiceModel

class ServiceAdminTabFragment : FragmentBase() {

    private lateinit var binding: FragmentServicesAdminTabBinding

    companion object {
        @JvmStatic
        fun newInstance(callBack: EAMXHome) =
            ServiceAdminTabFragment().apply {
                this.callBack = callBack
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServicesAdminTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView(){
        callBack.showToolbar(true, AppMyConstants.servicios)

        binding.apply {
            vpTabs.adapter = FragmentAdapter(this@ServiceAdminTabFragment, callBack){
                val itemServices = it as AdminServiceModel

                val bundle = Bundle().apply {
                    putInt(ServiceAdminServicesFragment.SERVICE_IDENTIFIER, itemServices.id)
                }

                binding.root.visibility = View.GONE
                NavigationFragment.Builder()
                    .setActivity(requireActivity())
                    .setView(requireView().parent as ViewGroup)
                    .setBundle(bundle)
                    .setFragment(ServiceAdminDetailRequestFragment.newInstance(callBack))
                    .setAllowStack(true)
                    .build().nextWithReplace()
            }
            TabLayoutMediator(tbTabs, vpTabs) { tab, position ->
                tab.text = if(position == 0) getString(R.string.tab_services) else getString(R.string.tab_history)
            }.attach()
        }
    }
}