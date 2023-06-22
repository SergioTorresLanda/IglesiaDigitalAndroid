package mx.arquidiocesis.servicios.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.servicios.adapter.ServiceAdapter
import mx.arquidiocesis.servicios.databinding.FragmentServicesMenuBinding
import mx.arquidiocesis.servicios.model.ServiceMenuMainModel

class ServicesMenuFragment : FragmentBase() {

    companion object {
        @JvmStatic
        fun newInstance(callBack: EAMXHome) =
            ServicesMenuFragment().apply {
                this.callBack = callBack
            }
    }

    private lateinit var binding: FragmentServicesMenuBinding
    private var listServicesMain: MutableList<ServiceMenuMainModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as EAMXHome).showToolbar(true, AppMyConstants.servicios)
        binding = FragmentServicesMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "Home_Servicios")
            })
        }
        initView()
        println("SERvIces MEnu:::")
    }

    private fun initView() {
        listServicesMain.clear()
        listServicesMain.add(
            ServiceMenuMainModel(
                id = 1,
                title = "Intenciones",
                desc = "Menciones en la misa",
                img = "Intentions"
            )
        )
        listServicesMain.add(
            ServiceMenuMainModel(
                id = 3,
                title = "Otros servicios",
                desc = "Bendiciones, celebraciones y otros",
                img = "OtherServices"
            )
        )
        listServicesMain.add(
            ServiceMenuMainModel(
                id = 0,
                title = "Sacramentos",
                desc = "Requisitos",
                img = "Sacrament"
            )
        )

        binding.rvServices.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = ServiceAdapter(requireContext(), listServicesMain) {
                selectItem(it.id)
            }
        }
    }

    private fun selectItem(id: Int) {
        println("selectedITEM :: "+id)
        when (id) {
            0 -> {
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                val fragment = SacramentsFragment.newInstance(this)
                transaction.replace((requireView().parent as ViewGroup).id, fragment)
                    .addToBackStack(tag).commit()
            }
            1 -> {
                if (!msgGuest("solicitar una intención.")) {
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    val fragment = MyChurchesMentionFragment()
                    transaction.replace((requireView().parent as ViewGroup).id, fragment)
                        .addToBackStack(tag).commit()
                }
            }
            2 -> {
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                val fragment = MyCommunitiesMentionFragment()
                transaction.replace((requireView().parent as ViewGroup).id, fragment)
                    .addToBackStack(tag).commit()
            }
            3 -> {
                if (!msgGuest("solicitar otros servicios.")) {
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    val fragment = ServicesOtherFragment()
                    transaction.replace((requireView().parent as ViewGroup).id, fragment)
                        .addToBackStack(tag).commit()
                }
            }
        }
    }
}