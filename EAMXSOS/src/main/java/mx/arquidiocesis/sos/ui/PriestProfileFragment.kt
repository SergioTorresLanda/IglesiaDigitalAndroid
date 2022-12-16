package mx.arquidiocesis.sos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.upax.eamxsos.databinding.FragmentPriestProfileBinding
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.sos.viewmodel.SOSServicesPriestViewModel
import mx.arquidiocesis.sos.adapters.ViewPagerAdapter
import mx.arquidiocesis.sos.repository.SOSRepository
import mx.arquidiocesis.sos.utils.Constants

class PriestProfileFragment : FragmentBase() {

    companion object {
        fun newInstance(): PriestProfileFragment {
            val fragment = PriestProfileFragment()
            return fragment
        }
    }

    private val viewModel: SOSServicesPriestViewModel by lazy {
        getViewModel {
            SOSServicesPriestViewModel(SOSRepository(requireContext()))
        }
    }
    lateinit var binding: FragmentPriestProfileBinding
    var idUser = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPriestProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //callBack.showToolbar(true, AppMyConstants.sosServicios)

        var adapter = ViewPagerAdapter(this){
            val bundle = Bundle().apply {
                putInt(ID, it.id)
                putInt(USER_ID,idUser)

            }
            when (it.status) {
                Constants.Status.COMPLETED -> {
                    NavigationFragment.Builder()
                        .setActivity(requireActivity())
                        .setView(requireView().parent as ViewGroup)
                        .setFragment(SOSDetalleFragment.newInstance())
                        .setBundle(bundle)
                        .setAllowStack(true)
                        .build().nextWithReplace()

                }
                Constants.Status.CANCELLED -> {
                    NavigationFragment.Builder()
                        .setActivity(requireActivity())
                        .setView(requireView().parent as ViewGroup)
                        .setFragment(SOSDetalleFragment.newInstance())
                        .setBundle(bundle)
                        .setAllowStack(true)
                        .build().nextWithReplace()
                }
                else -> {
                    NavigationFragment.Builder()
                        .setActivity(requireActivity())
                        .setView(requireView().parent as ViewGroup)
                        .setFragment(SOSNotificationFaithfulFragment.newInstance())
                        .setBundle(bundle)
                        .setAllowStack(true)
                        .build().nextWithReplace()
                }
            }

        }
        binding.vpNotification.adapter = adapter
        TabLayoutMediator(binding.tlNotification, binding.vpNotification) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Solicitudes"
                    // tab.icon = drawable1
                }
                1 -> {
                    tab.text = "Historial de solicitudes"
                    // tab.icon = drawable1
                }
            }

        }.attach()

    }
}