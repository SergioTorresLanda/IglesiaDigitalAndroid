package mx.arquidiocesis.registrosacerdote.customviews

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
import mx.arquidiocesis.registrosacerdote.R
import mx.arquidiocesis.registrosacerdote.adapter.CongregationAdapter
import mx.arquidiocesis.registrosacerdote.databinding.FragmentCongregationDialogPriestBinding
import mx.arquidiocesis.registrosacerdote.model.catalog.DataWithDescription
import mx.arquidiocesis.registrosacerdote.repository.Repository
import mx.arquidiocesis.registrosacerdote.viewmodel.PriestRegisterViewModel

class CongregationDialogFragment : FragmentDialogBase(){

    lateinit var binding: FragmentCongregationDialogPriestBinding
    private val priestRegisterViewModel : PriestRegisterViewModel by lazy {
        getViewModel {
            PriestRegisterViewModel(Repository(context = requireContext()))
        }
    }
    var congregations: List<DataWithDescription> = ArrayList()
    lateinit var congregationAdapter: CongregationAdapter
    var congregationSelected: ((congregation: DataWithDescription) -> Unit)? = null

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
            R.layout.fragment_congregation_dialog_priest,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        priestRegisterViewModel.getCongregations()
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
        priestRegisterViewModel.congregationsResponse.observe(this) {

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
        }

        priestRegisterViewModel.errorResponse.observe(this) {
            hideLoader()
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }
}