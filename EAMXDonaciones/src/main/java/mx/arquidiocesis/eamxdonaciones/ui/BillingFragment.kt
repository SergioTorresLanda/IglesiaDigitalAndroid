package mx.arquidiocesis.eamxdonaciones.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.example.eamxdonaciones.R
import com.example.eamxdonaciones.databinding.FragmentBillingBinding
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxdonaciones.Repository.RepositoryDonation
import mx.arquidiocesis.eamxdonaciones.model.BillingModel
import mx.arquidiocesis.eamxdonaciones.viewmodel.DonacionesViewModel


class BillingFragment : FragmentBase() {

    lateinit var binding: FragmentBillingBinding
    var model = MutableLiveData<BillingModel>()
    lateinit var fragment: BillingItemFragment
    val viewmodel: DonacionesViewModel by lazy {
        getViewModel {
            DonacionesViewModel(RepositoryDonation(requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initObservers()
        binding = FragmentBillingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initObservers() {
        viewmodel.postResponse.observe(viewLifecycleOwner) {
            hideLoader()
            addItemFragment(false)
            binding.tvCambiar.visibility = View.VISIBLE
            binding.btnGuardar.visibility = View.GONE
        }
        viewmodel.getResponse.observe(viewLifecycleOwner) {
            hideLoader()
            it?.let {d->
                model.value = d
                mostarData()
                binding.tvCambiar.visibility = View.VISIBLE
            }
        }
        viewmodel.errorResponse.observe(viewLifecycleOwner) {

            hideLoader()
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .setIsCancel(false)
                .build()
                .show(childFragmentManager, "")
        }
    }

    fun initView() {
        showLoader()
        viewmodel.getBilling()
        binding.apply {
            tvAddBilling.setOnClickListener {
                mostarData()
                btnGuardar.visibility = View.VISIBLE
            }
            tvCambiar.setOnClickListener {
                addItemFragment(true)
                binding.tvCambiar.visibility = View.GONE
                binding.btnGuardar.visibility = View.VISIBLE
            }
            btnGuardar.setOnClickListener {
                if (fragment.valida()) {
                    if(fragment.edit){
                        showLoader()
                        viewmodel.updateBilling(model.value!!.id!!,model.value!!)
                    }else{
                        showLoader()
                        viewmodel.setPost(model.value!!)
                    }
                }
            }
        }

    }

    fun mostarData() {
        binding.apply {
            tvAddBilling.visibility = View.GONE
            clBilling.visibility = View.VISIBLE
            addItemFragment(false)
        }
    }

    fun addItemFragment(edit: Boolean) {
        fragment = BillingItemFragment(model, edit)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.clBilling, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }


}