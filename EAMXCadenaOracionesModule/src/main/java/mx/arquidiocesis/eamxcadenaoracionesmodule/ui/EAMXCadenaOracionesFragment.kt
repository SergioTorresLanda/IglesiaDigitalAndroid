package mx.arquidiocesis.eamxcadenaoracionesmodule.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mx.arquidiocesis.eamxcadenaoracionesmodule.adapter.EAMXPrayChainAdapter
import mx.arquidiocesis.eamxcadenaoracionesmodule.databinding.EamxCadenaOracionesFragmentBinding
import mx.arquidiocesis.eamxcadenaoracionesmodule.model.EAMXSendPrayStatus
import mx.arquidiocesis.eamxcadenaoracionesmodule.repository.EAMXCadenaOracionesRepository
import mx.arquidiocesis.eamxcadenaoracionesmodule.viewmodel.EAMXCadenaOracionesViewModel
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.*

class EAMXCadenaOracionesFragment : FragmentBase() {

    companion object {
        @JvmStatic
        fun newInstance(callBack: EAMXHome): EAMXCadenaOracionesFragment {
            val cadenaOracionesFragment = EAMXCadenaOracionesFragment()
            cadenaOracionesFragment.callBack = callBack
            return cadenaOracionesFragment
        }
    }

    private val userId: Int by lazy {
        eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID.name,
            EAMXTypeObject.INT_OBJECT
        ) as Int
    }

    private val viewModel: EAMXCadenaOracionesViewModel by lazy {
        getViewModel {
            EAMXCadenaOracionesViewModel(
                EAMXCadenaOracionesRepository(requireContext())
            )
        }
    }

    private val adapter: EAMXPrayChainAdapter by lazy {
        EAMXPrayChainAdapter { first,second, pbOracion ->
            if (!msgGuest("interactuar con el contenido")) {
                pbOracion.visibility = View.VISIBLE
                val username = eamxcu_preferences.getData(
                    EAMXEnumUser.USER_EMAIL.toString(),
                    EAMXTypeObject.STRING_OBJECT
                ) as String
                viewModel.prayingOration(first, EAMXSendPrayStatus(userId, username, second))
            }
        }
    }

    lateinit var binding: EamxCadenaOracionesFragmentBinding


    val userLocal =
        "${
            eamxcu_preferences.getData(
                EAMXEnumUser.USER_NAME.name,
                EAMXTypeObject.STRING_OBJECT
            )
        } ${
            eamxcu_preferences.getData(
                EAMXEnumUser.USER_LAST_NAME.name,
                EAMXTypeObject.STRING_OBJECT
            )
        }"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EamxCadenaOracionesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "Home_CadenaOracion")
            })
        }
        initView()
        initObservers()
    }

    private fun initObservers() {
        viewModel.prayListResponse.observe(viewLifecycleOwner) { it ->
            if (it == null) return@observe
            eamxLog("Users oraciones", userLocal)

            val myPrays = it.data.filter { it.fiel_name == userLocal }
            val otherPrays = it.data.filter { it.fiel_name != userLocal }
            val prayList = myPrays + otherPrays

            if (viewModel.prayId >= 0) {
                adapter.updateList(prayList.sortedBy { item -> item.id }.asReversed())
                binding.rvCadenaOraciones.adapter = adapter

                //adapter.setUpdateData(prayList, viewModel.prayId)

            } else {
                if (prayList.isNotEmpty()) {
                    binding.apply {
                        eamxLog(
                            "Users oraciones",
                            prayList.map { "${it.id} ${it.fiel_name} \n" }.toString()
                        )
                        adapter.updateList(prayList.sortedBy { item -> item.id }.asReversed())
                        binding.rvCadenaOraciones.adapter = adapter
                    }
                }
            }
            binding.swiperefresh.isRefreshing = false
            hideLoader()
        }

        viewModel.sendPrayResponse.observe(viewLifecycleOwner) {
            showLoader("LOADER")
            viewModel.getPrayers(userId)
        }

        viewModel.prayingResponse.observe(viewLifecycleOwner) {
//            showLoader("LOADER")
            viewModel.getPrayers(userId)
        }

        viewModel.errorResponse.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
        }
    }

    private fun initView() {
        initElements(binding, activity, viewModel, callBack, { msgGuest("crear una oración") }) {
            showLoader("lOADER")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callBack.restoreToolbar()
    }
}