package mx.arquidiocesis.eamxdonaciones.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eamxdonaciones.R
import mx.arquidiocesis.eamxdonaciones.adapters.OfrendaViewPagerAdapter
import com.example.eamxdonaciones.databinding.FragmentOfrendaBinding
import mx.arquidiocesis.eamxdonaciones.viewmodel.DonacionesViewModel
import com.google.android.material.tabs.TabLayoutMediator
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxdonaciones.Repository.RepositoryDonation

class OfrendaFragment : FragmentBase() {

    companion object {
        fun newInstance(): OfrendaFragment {
            val fragment = OfrendaFragment()
            return fragment
        }
    }

    private val viewModel: DonacionesViewModel by lazy {
        getViewModel {
            DonacionesViewModel(RepositoryDonation(requireContext()))
        }
    }
    lateinit var binding: FragmentOfrendaBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOfrendaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var adapter = OfrendaViewPagerAdapter(this)
        binding.vpOfrenda.isUserInputEnabled = false;
        binding.vpOfrenda.adapter = adapter
        TabLayoutMediator(binding.tlOfrenda, binding.vpOfrenda) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Ofrenda"
                     tab.setIcon(R.drawable.ic_donation)
                }
                1 -> {
                    tab.text = "Facturar"
                    tab.setIcon(R.drawable.ic_billing)
                }
            }
        }.attach()

    }
}