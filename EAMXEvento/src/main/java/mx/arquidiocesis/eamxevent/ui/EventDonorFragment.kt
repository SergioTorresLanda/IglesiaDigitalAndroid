package mx.arquidiocesis.eamxevent.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxevent.R
import mx.arquidiocesis.eamxevent.adapter.DonorAllAdapter
import mx.arquidiocesis.eamxevent.databinding.FragmentEventDonorBinding
import mx.arquidiocesis.eamxevent.model.DonorResponse
import mx.arquidiocesis.eamxevent.model.ViewModelEvent
import mx.arquidiocesis.eamxevent.model.VolunteerResponse
import mx.arquidiocesis.eamxevent.repository.RepositoryEvent

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM = "diner_id"
private const val STATUS = "STATUS"

/**
 * A simple [Fragment] subclass.
 * Use the [EventDonorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EventDonorFragment : FragmentBase() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentEventDonorBinding
    private var diner_id: Int = 0
    private lateinit var adapter: DonorAllAdapter
    private var list: ArrayList<DonorResponse> = arrayListOf()
    private lateinit var viewmodel: ViewModelEvent
    private val TAG_LOADER: String = "EventDonorFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            diner_id = it.getInt(ARG_PARAM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        viewmodel = ViewModelEvent(RepositoryEvent(requireContext()))
        // Inflate thelayout for this fragment
        binding = FragmentEventDonorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "CrearComedor_ListaDonadores")
            })
        }
        callBack.showToolbar(true, AppMyConstants.donadores)
        adapter = DonorAllAdapter(requireContext(), list, binding.rvEventDonor)
        binding.rvEventDonor.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        binding.rvEventDonor.adapter = adapter
        initObservers()
        showLoader()
        click()
        viewModelEvent.requestAllDonorbyDiner(diner_id, "DONADORES")
    }

    fun click() {
        adapter.onItemClickListener = { item, Etiqueta ->
            when (Etiqueta) {
                STATUS -> {
                }
            }
        }
    }

    private fun initObservers() {
        viewModelEvent.responseAllDon.observe(viewLifecycleOwner) { item ->
            adapter.items.addAll(if (item.isNullOrEmpty()) arrayListOf(DonorResponse()) else item)
            adapter.notifyDataSetChanged()
            hideLoader()
        }
        viewModelEvent.showLoaderView.observe(viewLifecycleOwner) {
            showLoader()
        }
        viewModelEvent.errorResponse.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(it)
                .setListener {
                    hideLoader()
                }
                .setIsCancel(false)
                .build()
                .show(childFragmentManager, TAG_LOADER)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(callBack: EAMXHome): EventDonorFragment {
            val fragment = EventDonorFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    private val viewModelEvent: ViewModelEvent by lazy {
        getViewModel {
            ViewModelEvent(
                RepositoryEvent(requireContext())
            )
        }
    }
}