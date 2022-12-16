package mx.arquidiocesis.servicios.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_services_form.*
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.servicios.R
import mx.arquidiocesis.servicios.model.ServiceModel
import mx.arquidiocesis.servicios.repository.Repository
import mx.arquidiocesis.servicios.viewModel.ServiciosViewModel


class ServicesFormFragment(val serviceModel: ServiceModel) : FragmentBase() {

    //lateinit var callBack: EAMXHome
    private var FLAG_COMMUNION_BLESSING = false
    private val viewModel: ServiciosViewModel by lazy {
        getViewModel {
            ServiciosViewModel(Repository(requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_services_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //  callBack.showToolbar(true, AppMyConstants.servicios)
        initData()
        initListener()
        initObservers()
        if(serviceModel.type?.name.equals("BLESSING")){
            (requireActivity() as EAMXHome).showToolbar(true, AppMyConstants.bendiciones_title)
        }

    }

    private fun initListener() {
        btnSearchChurch.setOnClickListener { showSerchChurch() }
    }

    private fun initObservers() {
        viewModel.zipCodeResponse.observe(viewLifecycleOwner) {
            etSuburb.isEnabled = true
            val list: ArrayList<String> = ArrayList()
            list.add("Selecciona tu colonia")
            for (codigo in it.data) {
                list.add(codigo.name)
            }
            val adapterSpinner = ArrayAdapter(
                requireContext(),
                R.layout.custom_spinner_zip_code, list
            )
            etSuburb.adapter = adapterSpinner
            hideLoader()
        }

        viewModel.errorResponse.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert
                .Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
        }

    }

    private fun showSerchChurch() {

        if (etDirectionBlessings.text.isEmpty() ) {
            etDirectionBlessings.error = "Campo obligatorio"
            return
        } else {
            etDirectionBlessings.error = null
        }
        if (etSurnamesBlessings.text.isEmpty()) {
            etSurnamesBlessings.error = "Campo obligatorio"
            return
        } else {
            etSurnamesBlessings.error = null
        }
        if (etSuburb.selectedItemPosition <= 0 || !etPostalCode.query.toString().isNotEmpty()) {
            hideLoader()
            UtilAlert
                .Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage("Seleccione un código postal y colonia validados.")
                .build()
                .show(childFragmentManager, "")
            return
        }

        val transaction =
            requireActivity().supportFragmentManager.beginTransaction()
        val fragment = IglesiasFragment(serviceModel)
        val bundle = Bundle()
        bundle.putString("SURNAME_FULL_NAME", etSurnamesBlessings.text.toString())
        bundle.putString("ADDRESS_BLESSING", etDirectionBlessings.text.toString())
        bundle.putString("SUBURB_BLESSING", etSuburb.selectedItem.toString())
        bundle.putString("POSTAL_CODE", etPostalCode.query.toString())
        bundle.putBoolean("FLAG_COMMUNION_BLESSING", FLAG_COMMUNION_BLESSING)
        if (!FLAG_COMMUNION_BLESSING) {
            bundle.putString("JUSTIFICATION", etJustification.text.toString())
        }
        fragment.arguments = bundle
        transaction.replace((requireView().parent as ViewGroup).id, fragment).addToBackStack(tag)
            .commit()
    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        FLAG_COMMUNION_BLESSING = requireArguments().getBoolean("FLAG_COMMUNION_BLESSING")
        // true - Bendicion false - comunion a enfermos
        etSuburb.isEnabled = false
        val list: ArrayList<String> = ArrayList()
        list.add("Nombre")
        val adapterSpinner = object : ArrayAdapter<String>(
            requireContext(),
            R.layout.custom_spinner_zip_code, list){
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
                //set the color of first item in the drop down list to gray
                if(position == 0) {
                    view.setTextColor(ContextCompat.getColor(requireContext(), R.color.edittext_text_hint_color))
                }
                return view
            }
        }

        etSuburb.adapter = adapterSpinner

        if (FLAG_COMMUNION_BLESSING) {
            tvTitleBlessings.text = getString(R.string.txt_title_house_blessing)
            tvTitleFamily.text = getString(R.string.txt_family)
            etSurnamesBlessings.hint = getString(R.string.txt_surnames_blessings)
        } else {
            tvTitleBlessings.text = getString(R.string.txt_help_you)
            tvTitleFamily.text = getString(R.string.txt_sick_name)
            etSurnamesBlessings.hint = getString(R.string.txt_hint_sick_name)
            tvTitleJustification.visibility = View.VISIBLE
            etJustification.visibility = View.VISIBLE
        }

        val name = eamxcu_preferences.getData(
            EAMXEnumUser.USER_NAME.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String
        val lastName = eamxcu_preferences.getData(
            EAMXEnumUser.USER_LAST_NAME.name, EAMXTypeObject.STRING_OBJECT
        ) as String
        val middleName = eamxcu_preferences.getData(
            EAMXEnumUser.USER_MIDDLE_NAME.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String

        val email = eamxcu_preferences.getData(
            EAMXEnumUser.USER_EMAIL.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String

        val telephone = eamxcu_preferences.getData(
            EAMXEnumUser.USER_PHONE.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String

        tvNameApplicant.text = "$name $lastName $middleName "
        tvShowEmail.text = email
        tvShowTelephone.text = telephone
        etPostalCode.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (etPostalCode.query.isNotEmpty()) {
                    showLoader()
                    viewModel.getZipCode(etPostalCode.query.toString())
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.length == 5) {
                    showLoader()
                    viewModel.getZipCode(etPostalCode.query.toString())
                }
                if (newText!!.length > 5) {

                    etPostalCode.setQuery(newText!!.subSequence(1, 6), false)
                }

                return false
            }


        })


    }
}