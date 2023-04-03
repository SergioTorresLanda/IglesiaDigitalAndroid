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
import mx.arquidiocesis.eamxevent.adapter.VolunteerAllAdapter
import mx.arquidiocesis.eamxevent.databinding.FragmentEventVolunteerBinding
import mx.arquidiocesis.eamxevent.model.ViewModelEvent
import mx.arquidiocesis.eamxevent.model.VolunteerResponse
import mx.arquidiocesis.eamxevent.repository.RepositoryEvent

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM = "diner_id"
private const val STATUS = "STATUS"

/**
 * A simple [Fragment] subclass.
 * Use the [EventVolunteerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EventVolunteerFragment : FragmentBase() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentEventVolunteerBinding
    private var diner_id: Int = 0
    private lateinit var adapter: VolunteerAllAdapter
    private var list: ArrayList<VolunteerResponse> = arrayListOf()
    private lateinit var viewmodel: ViewModelEvent
    private val TAG_LOADER: String = "EventVolunteerFragment"

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
        binding = FragmentEventVolunteerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "CrearDonador_ListaVoluntarios")
            })
        }
        callBack.showToolbar(true, AppMyConstants.voluntarios)
        adapter = VolunteerAllAdapter(requireContext(), list, binding.rvEventVolunteer)
        binding.rvEventVolunteer.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        binding.rvEventVolunteer.adapter = adapter
        initObservers()
        showLoader()
        click()
        viewModelEvent.requestAllVolunteerbyDiner(diner_id, "VOLUNTARIOS")
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
        viewModelEvent.responseAllVol.observe(viewLifecycleOwner) { item ->
            adapter.items.addAll(if (item.isNullOrEmpty()) arrayListOf(VolunteerResponse()) else item)
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param callBack Parameter 1.
         * @return A new instance of fragment EventVolunteerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(callBack: EAMXHome): EventVolunteerFragment {
            val fragment = EventVolunteerFragment()
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