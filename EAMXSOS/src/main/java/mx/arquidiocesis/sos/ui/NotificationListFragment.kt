package mx.arquidiocesis.sos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.upax.eamxsos.R
import com.upax.eamxsos.databinding.FragmentNotificationListBinding
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.sos.adapters.ChildNotificationFaithfulAdapter
import mx.arquidiocesis.sos.model.ServicesPriestModel
import mx.arquidiocesis.sos.repository.SOSRepository
import mx.arquidiocesis.sos.viewmodel.SOSServicesPriestViewModel


class NotificationListFragment(val status: String, val listener: (ServicesPriestModel) -> Unit) :
    FragmentBase() {
    lateinit var binding: FragmentNotificationListBinding
    private val viewModel: SOSServicesPriestViewModel by lazy {
        getViewModel {
            SOSServicesPriestViewModel(SOSRepository(requireContext()))
        }
    }
    var idUser = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationListBinding.inflate(layoutInflater, container, false)
        showLoader()
        idUser = eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID.name,
            EAMXTypeObject.INT_OBJECT
        ) as Int
        viewModel.getPriestServices(idUser, status)
        initObservers()
        return binding.root
    }

    private fun initObservers() {
        viewModel.priestServices.observe(viewLifecycleOwner) {
            var list: MutableList<ServicesPriestModel> = mutableListOf()
            list = it.sortedByDescending {it.id} as MutableList<ServicesPriestModel>
            binding.rvServices.apply {
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = ChildNotificationFaithfulAdapter(list, listener)

            }

            hideLoader()
        }

        viewModel.errorResponse.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert
                .Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(it)
                .build()
                .show(childFragmentManager, tag)
        }
    }


}