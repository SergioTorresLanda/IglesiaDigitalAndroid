package mx.arquidiocesis.misiglesias.ui

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.item_edit.*
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.misiglesias.R
import mx.arquidiocesis.misiglesias.databinding.FragmentRedBinding
import mx.arquidiocesis.misiglesias.model.ChurchDetaillModel
import mx.arquidiocesis.misiglesias.utils.PublicFunctions

open class BottomSheetRedes(var church: MutableLiveData<ChurchDetaillModel>) :
    BottomSheetDialogFragment() {
    lateinit var binding: FragmentRedBinding
    lateinit var model: ChurchDetaillModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRedBinding.inflate(inflater, container, false)
        model = church.value!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniciar()
    }

    private fun iniciar() {

        binding.apply {
            iRed.apply {
                val list: ArrayList<String> = ArrayList()
                etItemEdit.visibility = View.GONE
                tvItemTitulo.text = "Red social"

                list.add("Selecciona una red")
                list.add(getString(R.string.web))
                list.add(getString(R.string.twitter))
                list.add(getString(R.string.facebook))
                list.add(getString(R.string.transmision))
                list.add(getString(R.string.instagram))
                val adapterSpinner = ArrayAdapter(
                    requireContext(),
                    R.layout.custom_spinner_item, list
                )
                spItemSpi.adapter = adapterSpinner
                spItemSpi.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        iSoc.apply {
                            etItemEdit.hint="https://www.iglesia.com/miiglesia"
                            etItemEdit.setText("")
                            PublicFunctions().maxminEdit(etItemEdit, 100)
                            when (position) {
                                1 -> {
                                    tvItemTitulo.text = getString(R.string.web)
                                    etItemEdit.visibility = View.VISIBLE
                                    if (!model.website.isNullOrEmpty()) {
                                        etItemEdit.setText(model.website)
                                    }
                                }
                                2 -> {
                                    tvItemTitulo.text = getString(R.string.twitter)
                                    etItemEdit.visibility = View.VISIBLE
                                    if (!model.twitter.isNullOrEmpty()) {
                                        etItemEdit.setText(model.twitter)
                                    }
                                }
                                3 -> {
                                    tvItemTitulo.text = getString(R.string.facebook)
                                    etItemEdit.visibility = View.VISIBLE
                                    if (!model.facebook.isNullOrEmpty()) {
                                        etItemEdit.setText(model.facebook)
                                    }
                                }
                                4 -> {
                                    tvItemTitulo.text = getString(R.string.transmision)
                                    etItemEdit.visibility = View.VISIBLE
                                    if (!model.website.isNullOrEmpty()) {
                                        etItemEdit.setText(model.stream)
                                    }
                                }
                                5 -> {
                                    tvItemTitulo.text = getString(R.string.instagram)
                                    etItemEdit.visibility = View.VISIBLE
                                    if (!model.website.isNullOrEmpty()) {
                                        etItemEdit.setText(model.instagram)
                                    }
                                }
                            }
                        }
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                })
            }
            tvAddRed.setOnClickListener {
                val nombre = iSoc.etItemEdit.text.toString()
               if(!add(nombre)){
                   vacio()
               }else{
                   church.value = model
               }
            }
            btnListo.setOnClickListener {
                val nombre = iSoc.etItemEdit.text.toString()
                if(add(nombre)){
                    church.value = model
                }else{
                    vacio()
                }
                dismiss()
            }
            initObservers()
        }
    }

    fun initObservers() {


    }

    fun add(nombre: String): Boolean {
        var valido = true
        if (!nombre.isNullOrEmpty()) {
            if (Patterns.WEB_URL.matcher(nombre).matches().not()) {
                valido =false
            }else{
                when (binding.iRed.spItemSpi.selectedItemPosition) {
                    1 -> {
                        model.website = nombre
                        agregada()
                    }
                    2 -> {
                        model.twitter = nombre
                        agregada()

                    }
                    3 -> {
                        model.facebook = nombre
                        agregada()
                    }
                    4 -> {
                        model.stream = nombre
                        agregada()
                    }
                    5 -> {
                        model.instagram = nombre
                        agregada()
                    }
                    else -> {
                        valido = false
                    }
                }
            }

        } else {
            valido = false
        }
        return valido
    }

    fun vacio() {
        UtilAlert
            .Builder()
            .setTitle("Aviso")
            .setMessage("Ingresa la url como se muestra en el ejemplo")
            .build()
            .show(childFragmentManager, "")
    }

    fun agregada() {
        binding.iRed.spItemSpi.setSelection(0)
        //Toast.makeText(requireContext(), "Red agregada", Toast.LENGTH_SHORT).show()
    }
}