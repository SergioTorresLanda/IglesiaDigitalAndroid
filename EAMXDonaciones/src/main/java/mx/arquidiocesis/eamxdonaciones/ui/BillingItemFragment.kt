package mx.arquidiocesis.eamxdonaciones.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.lifecycle.MutableLiveData
import com.example.eamxdonaciones.R
import com.example.eamxdonaciones.databinding.ItemBillingBinding
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.isValidEmail
import mx.arquidiocesis.eamxcommonutils.util.visibility
import mx.arquidiocesis.eamxdonaciones.Repository.RepositoryDonation
import mx.arquidiocesis.eamxdonaciones.model.BillingModel
import mx.arquidiocesis.eamxdonaciones.viewmodel.DonacionesViewModel
import java.util.regex.Pattern


class BillingItemFragment(var billing: MutableLiveData<BillingModel>, val edit: Boolean = false) :
    FragmentBase() {

    private val regexRfc =
        "^([A-Z,Ñ,&]{3,4}([0-9]{2})(0[1-9]|1[0-2])(0[1-9]|1[0-9]|2[0-9]|3[0-1])[A-Z|\\d]{2,3})\$".toRegex()



    lateinit var binding: ItemBillingBinding
    lateinit var model: BillingModel

    val viewmodel: DonacionesViewModel by lazy {
        getViewModel {
            DonacionesViewModel(RepositoryDonation(requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initObservers()
        binding = ItemBillingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

    }

    fun initObservers() {
        viewmodel.zipCodeResponse.observe(viewLifecycleOwner) {
            binding.etCL.adapter = ArrayAdapter(
                requireContext(),
                R.layout.custom_spinner_zip_code,
                listOf("Selecciona tu colonia", *it.data.map { c -> c.name }.toTypedArray())
            )
            binding.etCL.setSelection(it.data.indexOfFirst { it.name.lowercase() == billing.value?.neighborhood?.lowercase() } + 1)
            hideLoader()
        }


    }

    private fun initView() {
        binding.apply {
//            rbFA.setOnClickListener {
//                rbFA.isChecked = !rbFA.isChecked
//            }
            etCp.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = false
                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText?.length == 5) {
                        showLoader()
                        viewmodel.getZipCode(etCp.query.toString())
                    }
                    return true
                }
            })
        }
        model = billing.value ?: BillingModel()
        setBilling(billing.value ?: return)
    }

    fun valida(): Boolean {
        var message: String? = null
        var valida = true
        binding.apply {

            if (etRz.text.isNullOrEmpty()) {
                etRz.error = "Ingresa tu razón social"
                valida = false
            }
            if (etCE.text.isNullOrEmpty()) {
                etCE.error = "Ingresa tu correo electronco"
                valida = false
            }
            if (!etCE.text.toString().trim().isValidEmail()) {
                etCE.error = "Ingresa un correo válido."
                valida = false
            }
            if (etDr.text.isNullOrEmpty()) {
                etDr.error = "Ingresa tu dirección"
                valida = false
            }
            if (etAc.text.isNullOrEmpty()) {
                etAc.error = "Ingresa tu alcaldía"
                valida = false
            }
            if (!regexRfc.matches(etRfc.text.toString())) {
                etRfc.error = "Formato de RFC incorrecto"
                valida = false
            }
            if (etRfc.text.isNullOrEmpty()) {
                etRfc.error = "Campo de RFC incorrecto"
                valida = false
            }

            if (etCp.query.toString().isEmpty()) {
                valida = false
                message = "Ingresa tu C.P."
            }

            if (etCL.selectedItem == null) {
                valida = false
                if(message == null){
                    message = "Ingresa tu colonia"
                }else{
                    message = "$message\n Ingresa tu colonia"
                }

            }

            message?.let { showAlert(it) }
            if (valida) {
                model.business_Name = etRz.text.toString()
                model.rfc = etRfc.text.toString()
                model.address = etDr.text.toString()
                model.neighborhood = etCL.selectedItem.toString()
                model.email = etCE.text.toString()
                model.zipcode = etCp.query.toString()
                model.municipality = etAc.text.toString()
                model.automatic_invoicing = rbFA.isChecked
                billing.value = model
            }
            return valida
        }

    }

    private fun setBilling(billingModel: BillingModel) {
        binding.apply {
            item = billingModel
            etRz.visibility(edit)
            etAc.visibility(edit)
            etCE.visibility(edit)
            etCL.visibility(edit)
            etCp.visibility(edit)
            etDr.visibility(edit)
            etRfc.visibility(edit)
            tvRzif.visibility(!edit)
            tvAcif.visibility(!edit)
            tvCEif.visibility(!edit)
            tvClif.visibility(!edit)
            tvCPif.visibility(!edit)
            tvDrif.visibility(!edit)
            tvRfcif.visibility(!edit)
            rbFA.isEnabled = edit
            if (edit) etCp.setQuery(billingModel.zipcode, true)
            rbFA.isChecked = billingModel.automatic_invoicing == true
        }
    }



    private fun showAlert(message: String) {
        UtilAlert
            .Builder()
            .setTitle("Aviso")
            .setMessage(message)
            .build()
            .show(childFragmentManager, "")
    }

}