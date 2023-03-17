package mx.arquidiocesis.eamxevent.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_event_detail.*
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxcommonutils.util.urlValidator
import mx.arquidiocesis.eamxevent.R
import mx.arquidiocesis.eamxevent.databinding.FragmentEventBinding
import mx.arquidiocesis.eamxevent.model.DinerAllAdapter
import mx.arquidiocesis.eamxevent.model.DinerResponse
import mx.arquidiocesis.eamxevent.model.Event
import mx.arquidiocesis.eamxevent.model.ViewModelEvent
import mx.arquidiocesis.eamxevent.repository.RepositoryEvent
import mx.arquidiocesis.eamxredsocialmodule.Repository.Repository
import mx.arquidiocesis.eamxredsocialmodule.adapter.EAMXPublicationsAllAdapter
import mx.arquidiocesis.eamxredsocialmodule.databinding.EamxRedSocialFragmentBinding
import mx.arquidiocesis.eamxredsocialmodule.model.PostModel
import mx.arquidiocesis.eamxredsocialmodule.news.detail.EAMXDetailFragment
import mx.arquidiocesis.eamxredsocialmodule.viewmodel.RedSocialViewModel

class EventFragment : FragmentBase() {

    lateinit var binding: FragmentEventBinding
    lateinit var viewmodel: ViewModelEvent
    lateinit var adapter: DinerAllAdapter
    companion object {
        fun newInstance(callBack: EAMXHome): EventFragment {
            var fragment = EventFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewmodel = ViewModelEvent(RepositoryEvent(requireContext()))
        binding = FragmentEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callBack.showToolbar(true, AppMyConstants.evento)
        binding.tvNewActivity.setOnClickListener {
            NavigationFragment.Builder()
                .setActivity(requireActivity())
                .setView(requireView().parent as ViewGroup)
                .setFragment(EventDetailFragment.newInstance(callBack) as Fragment)
                .build().nextWithReplace()
        }
        initObservers()
    }

    private fun initObservers() {

        println("Hola get")
        getAllDiners()
        viewmodel.responseAllDin.observe(viewLifecycleOwner){ item ->
            item.let { i ->
                adapter.items.addAll(listOf(item))
                adapter.notifyDataSetChanged()
            }
            val prevSize = adapter.items.size
            if (prevSize != 0) {
                adapter.notifyItemRangeInserted(prevSize, adapter.items.count() - 1)
            }
        }

        viewmodel.errorResponse.observe(viewLifecycleOwner) {
            //showSkeleton(false)
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
        }
    }
    /*
    fun selectRow(item: DinerResponse) {
        if (item.fIZONA.isNotEmpty()) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.content)))
        } else {
            changeFragment(EAMXDetailFragment(), Bundle().apply {
                putParcelable(EAMXEnumUser.PUBLICATIONS.name, item)
            })
        }
    }

     */

    fun getAllDiners() {
        adapter =
            DinerAllAdapter(requireContext(), viewmodel.getFine())
        adapter.items = arrayListOf()
        viewmodel.requestAllDiner()
    }
}




