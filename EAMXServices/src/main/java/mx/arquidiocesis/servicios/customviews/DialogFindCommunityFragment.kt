package mx.arquidiocesis.servicios.customviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import mx.arquidiocesis.eamxcommonutils.base.FragmentDialogBase
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.servicios.R
import mx.arquidiocesis.servicios.adapter.CommunityFindAdapter
import mx.arquidiocesis.servicios.databinding.FragmentDialogFindCommunityBinding
import mx.arquidiocesis.servicios.model.CommunityResponse
import mx.arquidiocesis.servicios.repository.Repository
import mx.arquidiocesis.servicios.viewModel.ServiciosViewModel

class DialogFindCommunityFragment : FragmentDialogBase() {
    lateinit var binding: FragmentDialogFindCommunityBinding

    private val viewModel: ServiciosViewModel by lazy {
        getViewModel {
            ServiciosViewModel(Repository(requireContext()))
        }
    }

    var communitySelected: ((communityResponse: CommunityResponse) -> Unit)? = null
    lateinit var communityFindAdapter: CommunityFindAdapter

    companion object {
        fun newInstance(): DialogFindCommunityFragment =
            DialogFindCommunityFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_dialog_find_community,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()

        viewModel.getAllCommunities()
        showLoader()

        binding.ivClose.setOnClickListener {
            dialog?.dismiss()
        }

        binding.svCommunity.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean = false

            override fun onQueryTextChange(p0: String?): Boolean {
                communityFindAdapter.filter.filter(p0)
                return false
            }
        })
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    fun initObservers() {
        viewModel.communitiesResponse.observe(this, {
            communityFindAdapter = CommunityFindAdapter(it) {
                communitySelected?.invoke(it)
                dialog?.dismiss()
            }

            binding.rvCommunities.apply {
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                adapter = communityFindAdapter
            }
            hideLoader()
        })

        viewModel.errorResponse.observe(this, {
            hideLoader()
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
        })
    }
}