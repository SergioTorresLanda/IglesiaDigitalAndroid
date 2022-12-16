package mx.arquidiocesis.eamxprofilemodule.customviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SearchView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import mx.arquidiocesis.eamxcommonutils.base.FragmentDialogBase
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxprofilemodule.R
import mx.arquidiocesis.eamxprofilemodule.adapter.CongregationAdapter
import mx.arquidiocesis.eamxprofilemodule.databinding.FragmentCongregationDialogBinding
import mx.arquidiocesis.eamxprofilemodule.model.CongregationModel
import mx.arquidiocesis.eamxprofilemodule.repository.RepositoryProfile
import mx.arquidiocesis.eamxprofilemodule.viewmodel.EAMXViewModelProfile

class CongregationDialogFragment : FragmentDialogBase() {

    private lateinit var binding: FragmentCongregationDialogBinding

    private val viewModelProfile: EAMXViewModelProfile by lazy {
        getViewModel {
            EAMXViewModelProfile(
                    RepositoryProfile(requireContext())
            )
        }
    }
    var congregations: List<CongregationModel> = ArrayList()
    lateinit var congregationAdapter: CongregationAdapter

    var congregationSelected: ((congregation: CongregationModel) -> Unit)? = null

    companion object {
        fun newInstance(): CongregationDialogFragment =
            CongregationDialogFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_congregation_dialog,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        viewModelProfile.getCongregations()
        showLoader()

        binding.ivClose.setOnClickListener {
            dialog?.dismiss()
        }

        binding.svCongregation.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean = false

            override fun onQueryTextChange(p0: String?): Boolean {
                congregationAdapter.filter.filter(p0)
                return false
            }
        })
    }

    fun initObservers() {
        viewModelProfile.congregationsResponse.observe(this, {

            congregations = it.data
            congregationAdapter = CongregationAdapter(congregations) {
                congregationSelected?.invoke(it)
                dialog?.dismiss()
            }


            binding.rvCongregation.apply {
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                adapter = congregationAdapter
            }

            hideLoader()
        })

        viewModelProfile.errorResponse.observe(this, {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            hideLoader()
        })
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }
}