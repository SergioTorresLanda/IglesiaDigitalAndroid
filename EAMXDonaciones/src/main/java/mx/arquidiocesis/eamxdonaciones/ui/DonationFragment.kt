package mx.arquidiocesis.eamxdonaciones.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.example.eamxdonaciones.databinding.FragmentDonationBinding
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_donation.*
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.eamxLog
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.encryptData
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxdonaciones.Repository.RepositoryDonation
import mx.arquidiocesis.eamxdonaciones.congig.WebConfig
import mx.arquidiocesis.eamxdonaciones.model.BillingModel
import mx.arquidiocesis.eamxdonaciones.model.DonationModel
import mx.arquidiocesis.eamxdonaciones.model.ModelWebView
import mx.arquidiocesis.eamxdonaciones.utils.toNumber
import mx.arquidiocesis.eamxdonaciones.viewmodel.DonacionesViewModel
import mx.arquidiocesis.eamxpagos.config.OriginType
import mx.arquidiocesis.eamxpagos.fragment.ComponentFragment
import mx.arquidiocesis.misiglesias.R
import java.net.URLEncoder


class DonationFragment(val item: DonationModel) : FragmentBase() {
    private val gson by lazy {
        Gson()
    }
    private val viewmodel: DonacionesViewModel by lazy {
        getViewModel {
            DonacionesViewModel(RepositoryDonation(requireContext()))
        }
    }
    lateinit var binding: FragmentDonationBinding
    var model = MutableLiveData<BillingModel>()
    var addBinding = false
    lateinit var fragment: BillingItemFragment
    private var amount: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDonationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvTitulo.text = item.name
            if (item.image_url.isNullOrEmpty()) {
                Glide.with(requireContext())
                    .load(R.drawable.defaultimage)
                    .into(binding.ivChurch)
            } else {
                Glide.with(requireContext())
                    .load(Uri.parse(item.image_url))
                    .into(binding.ivChurch)
            }
            rgBilling.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    rbSi.id -> {
                        if (addBinding)
                            tlBilling.visibility = View.VISIBLE
                    }
                    rbNo.id -> {
                        tlBilling.visibility = View.GONE
                    }

                }
            }

            btnPublicar.setOnClickListener {
                if (rbSi.isChecked && addBinding) {
                    if (fragment.valida()) {
                        showLoader()
                        viewmodel.setPost(model.value!!)
                    }
                } else {
                    if (validaMonto()) {
                        changeFragment(setLink())
                    }
                }
            }
            btnCancel.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
        setSpiner()
        initObservers()
        viewmodel.getBilling()
    }

    @SuppressLint("SetTextI18n")
    fun setSpiner() {
        binding.apply {
            val list: ArrayList<String> = ArrayList()
            list.add("Selecciona concepto")
            list.add("Ofrenda")
            list.add("Diezmo")
            list.add("Limosna")
            list.add("Pago de una intención")
            list.add("Pago de un servicio")
            list.add("Otro")
            val adapterSpinner = ArrayAdapter(
                requireContext(),
                com.example.eamxdonaciones.R.layout.custom_spinner_zip_code, list
            )
            spCop.adapter = adapterSpinner
            spCop.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long,
                    ) {
                        if (position + 1 == list.size) {
                            etCop.visibility = View.VISIBLE
                        } else {
                            etCop.visibility = View.GONE
                        }
                    }
                }
            val listMonto: ArrayList<String> = ArrayList()
            listMonto.add("Selecciona monto")
            listMonto.add("$50.00")
            listMonto.add("$100.00")
            listMonto.add("$200.00")
            listMonto.add("$300.00")
            listMonto.add("$400.00")
            listMonto.add("$500.00")
            listMonto.add("$1,000.00")
            listMonto.add("Otro")
            val adapterSpinnerMonto = ArrayAdapter(
                requireContext(),
                com.example.eamxdonaciones.R.layout.custom_spinner_zip_code, listMonto
            )
            customSpinner.setAdapter(adapterSpinnerMonto)
        }
    }

    fun addItemFragment(edit: Boolean) {
        fragment = BillingItemFragment(model, edit)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(binding.flBilling.id, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    fun initObservers() {
        viewmodel.postResponse.observe(viewLifecycleOwner) {
            hideLoader()
            addBinding = false
            if (validaMonto()) {
                changeFragment(setLink())
            }
        }
        viewmodel.getResponse.observe(viewLifecycleOwner) {
            hideLoader()
            it?.let { d ->
                model.value = d
                addItemFragment(false)
                addBinding = false
                d.automatic_invoicing?.let { a ->
                    if (a) {
                        rbSi.isChecked = true
                    } else {
                        rbNo.isChecked = true
                    }
                } ?: run {
                    rbNo.isChecked = true
                }
            } ?: run {
                addBinding = true
                addItemFragment(true)
            }
        }
        viewmodel.errorResponse.observe(viewLifecycleOwner) {
            if (it == null) return@observe
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

    fun validaMonto(): Boolean {
        val valida = true
        binding.apply {

            if (customSpinner.spMonto.selectedItemPosition <= 0) {
                error("Seleccione un monto.")
                return false
            } else if (customSpinner.spMonto.selectedItemPosition == 8 && customSpinner.etMonto.text.toString().isEmpty()
            ) {
                error("Seleccione un monto.")
                return false
            }
            if (customSpinner.spMonto.selectedItemPosition == 8) {
                try {
                    if (customSpinner.etMonto.text.toString().toNumber() < 50.00) {
                        error("¡Gracias! Desafortunadamente no podemos recibir ofrendas menores a $50.00 pesos.")
                        return false
                    }
                    if (customSpinner.etMonto.text.toString().toNumber() > 10000.00) {
                        error("No es posible recibir ofrendas superiores a $10,000.00 pesos. Cualquier duda favor de comunicarte a contacto@miofrenda.mx")
                        return false
                    }
                } catch (e: Error) {
                    error("Seleccione un monto válido.")
                    return false
                }
            }
            if (spCop.selectedItemPosition <= 0) {
                error("Seleccione un concepto")
                return false
            }
        }
        return valida
    }

    fun setLink(): String {
        val monto = if (customSpinner.spMonto.selectedItemPosition == 8) {
            customSpinner.etMonto.text.toString().toNumber()
        } else {
            customSpinner.spMonto.selectedItem.toString().toNumber()
        }
        val userName =
            eamxcu_preferences.getData(
                EAMXEnumUser.USER_EMAIL.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String
        val phoneNumber = eamxcu_preferences.getData(
            EAMXEnumUser.USER_PHONE.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String
        val name =
            eamxcu_preferences.getData(
                EAMXEnumUser.USER_NAME.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String
        val surnames =
            eamxcu_preferences.getData(
                EAMXEnumUser.USER_LAST_NAME.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String
        val modelWV =
            ModelWebView(
                monto.toString(),
                model.value?.email ?: userName,
                item.id.toString(),
                name,
                "68844",
                phoneNumber,
                surnames,
                if (rbSi.isChecked) model.value?.rfc else null,
                if (rbSi.isChecked) model.value?.business_Name else null,
                if (rbSi.isChecked) model.value?.address else null,
                if (rbSi.isChecked) model.value?.neighborhood else null,
                if (rbSi.isChecked) model.value?.municipality else null,
                if (rbSi.isChecked) model.value?.zipcode else null,
            )
        return "${WebConfig.URLDONATION}pagos/data/v2?data=" + URLEncoder.encode(
            gson.toJson(modelWV).apply {
                eamxLog(this)
            }.encryptData().apply(::eamxLog)
        )

    }

    fun error(s: String) {
        UtilAlert
            .Builder()
            .setTitle("Aviso")
            .setMessage(s)
            .setIsCancel(false)
            .build()
            .show(childFragmentManager, "")
    }

    private fun changeFragment(link: String) {
        val bundle = Bundle()
        bundle.putString(
            "concepto", getConcepto()
        )
        bundle.putString("monto", getAmount() + " M. N.")
        NavigationFragment.Builder()
            .setActivity(requireActivity())
            .setView(requireView().parent as ViewGroup)
            .setBundle(bundle)
            .setFragment(
                ComponentFragment(
                    link, OriginType.OFRENDA
                )
            )
            .setAllowStack(true)
            .build().nextWithReplace()
    }

    private fun getConcepto(): String {
        var concepto = ""
        if (validaMonto()) {
            if (binding.spCop.selectedItemPosition == 6 && binding.etCop.text.toString()
                    .isNotEmpty()
            ) {
                concepto = binding.etCop.text.toString()
            } else {
                concepto = binding.spCop.selectedItem.toString()
            }
        }
        return concepto
    }

    private fun getAmount(): String? {
        if (validaMonto()) {
            amount = if (binding.customSpinner.spMonto.selectedItemPosition == 8
                && binding.customSpinner.etMonto.text.toString().isNotEmpty()
            ){
                "$" + binding.customSpinner.etMonto.text.toString() + ".00"
            }else{
                binding.customSpinner.spMonto.selectedItem.toString()
            }
        }
        return amount
    }
}