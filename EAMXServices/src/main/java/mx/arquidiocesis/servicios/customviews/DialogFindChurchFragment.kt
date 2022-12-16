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
import mx.arquidiocesis.servicios.adapter.ChurchFindAdapter
import mx.arquidiocesis.servicios.databinding.FragmentDialogFindChurchBinding
import mx.arquidiocesis.servicios.model.IgleciasModel
import mx.arquidiocesis.servicios.repository.Repository
import mx.arquidiocesis.servicios.viewModel.ServiciosViewModel

class DialogFindChurchFragment : FragmentDialogBase() {

    lateinit var binding: FragmentDialogFindChurchBinding
    private val viewModel: ServiciosViewModel by lazy {
        getViewModel {
            ServiciosViewModel(Repository(requireContext()))
        }
    }
    var churchSelected: ((congregation: IgleciasModel) -> Unit)? = null
    lateinit var churchFindAdapter: ChurchFindAdapter

    companion object {
        fun newInstance(): DialogFindChurchFragment =
            DialogFindChurchFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_dialog_find_church,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        viewModel.getAllChurches()
        showLoader()

        binding.ivClose.setOnClickListener {
            dialog?.dismiss()
        }

        binding.svChurch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean = false

            override fun onQueryTextChange(p0: String?): Boolean {
                churchFindAdapter.filter.filter(p0)
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
        viewModel.churchesResponse.observe(this, {
            churchFindAdapter = ChurchFindAdapter(it) {
                churchSelected?.invoke(it)
                dialog?.dismiss()
            }

            binding.rvChurches.apply {
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                adapter = churchFindAdapter
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