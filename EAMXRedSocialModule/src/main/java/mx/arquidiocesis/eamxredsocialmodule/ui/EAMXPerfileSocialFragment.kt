package mx.arquidiocesis.eamxredsocialmodule.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxredsocialmodule.databinding.EamxPerfilFragmentBinding
import mx.arquidiocesis.eamxredsocialmodule.news.EAMXSocialNetworkViewModel
import mx.arquidiocesis.eamxredsocialmodule.Repository.Repository

class EAMXPerfileSocialFragment : FragmentBase() {

    lateinit var binding: EamxPerfilFragmentBinding
    val viewmodel: EAMXSocialNetworkViewModel by lazy {
        getViewModel {
            EAMXSocialNetworkViewModel(Repository(requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initObservers()
        binding = EamxPerfilFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

    }

    fun iniSegir() {
        binding.apply {
            clPublications.visibility = View.GONE
            clSeguir.visibility = View.VISIBLE
            iFollewr.apply {
                TabLayoutMediator(tlFollower, vpFollower) { tab, position ->
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
                }
            }
        }

    }
     fun initObservers() {


    }

     fun initView() {

    }



}