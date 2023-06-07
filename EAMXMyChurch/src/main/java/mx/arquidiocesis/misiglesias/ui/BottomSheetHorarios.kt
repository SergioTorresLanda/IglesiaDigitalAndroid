package mx.arquidiocesis.misiglesias.ui

import mx.arquidiocesis.misiglesias.databinding.FragmentHorariosBinding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.misiglesias.R
import mx.arquidiocesis.misiglesias.model.*
import mx.arquidiocesis.misiglesias.utils.PublicFunctions
import mx.arquidiocesis.misiglesias.viewmodel.EditarIglesiaViewModel

open class BottomSheetHorarios(
    val IsMasse: Boolean,
    val viewModel: EditarIglesiaViewModel,
    var church: MutableLiveData<ChurchDetaillModel>
) : BottomSheetDialogFragment() {
    lateinit var binding: FragmentHorariosBinding
    lateinit var model: ChurchDetaillModel
    lateinit var type: TypeModel
    lateinit var listHorarios: MutableList<HoraryModelItem>
    lateinit var listService: MutableList<ServiceModel>

    lateinit var horaryModelItem: HoraryModelItem
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHorariosBinding.inflate(inflater, container, false)
        model = church.value!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (IsMasse) {
            listHorarios = model.masses as MutableList<HoraryModelItem>
        } else {
            listService = model.services as MutableList<ServiceModel>
        }
        horaryModelItem = HoraryModelItem(PublicFunctions().getDays(), "00:00", "00:00")
        iniciar()
        initObservers()
    }

    private fun iniciar() {

        binding.apply {
            if (IsMasse) {
                iSer.apply {
                    clItem.visibility = View.GONE
                }

                iDir.apply {
                    clItem.visibility = View.GONE

                }
                iDes.apply {
                    clItem.visibility = View.GONE

                }
            } else {
                iSer.apply {
                    tvItemTitulo.text = "Nombre del Servicio/Actividad*"
                    viewModel.getCatalogServicios()

                }
                iDir.apply {
                    tvItemTitulo.text = "Dirigido a*"

                }
                iDes.apply {
                    tvItemTitulo.text = "Descripción"

                }
            }

            iHor.apply {
                etHoras.isFocusable = false
                etDias.isFocusable = false
                tvItemTitulo.text = "Horario"
                var dias = MutableLiveData<MutableList<Day>>()

                dias.value = PublicFunctions().getDays()
                var hora = MutableLiveData<String>()
                var first = true

                etDias.setOnClickListener {
                    getDays(dias)
                }
                ivCalendario.setOnClickListener {
                    getDays(dias)
                }
                etHoras.setOnClickListener {
                    first = true
                    getHora(hora)
                }
                ivRelog.setOnClickListener {
                    first = true
                    getHora(hora)
                }
                dias.observe(viewLifecycleOwner) {
                    horaryModelItem.days = it
                    etDias.setText(PublicFunctions().obtenerDias(it))
                }
                hora.observe(viewLifecycleOwner) {
                    if (IsMasse) {
                        horaryModelItem.hour_start = it
                        etHoras.setText("${horaryModelItem.hour_start} hrs.")
                    }else{
                        if (first) {
                            horaryModelItem.hour_start = it
                            etHoras.setText("${horaryModelItem.hour_start} hrs.")
                            getHora(hora)
                            first = false
                        } else {
                            horaryModelItem.hour_end = it
                            etHoras.setText("${horaryModelItem.hour_start} a ${horaryModelItem.hour_end} hrs")
                        }
                    }
                }
            }
            tvAddMisas.setOnClickListener {
              if(add()){
                  iHor.etHoras.setText("")
                  iHor.etDias.setText("")
                  if (IsMasse) {
                      agregada("Misa agregada")
                      model.masses=listHorarios

                  } else {
                      agregada("Servicio agregada")
                      model.services=listService
                      iSer.spItemSpi.setSelection(0)
                  }
                  church.value = model
              }else{
                  viewModel.errorResponse.value="Llene todos los campos requeridos"
              }
            }
            btnListo.setOnClickListener {
                if(add()){
                    if(IsMasse){
                        model.masses=listHorarios
                    }else{
                        model.services=listService
                    }
                    church.value = model

                }else{
                    viewModel.errorResponse.value="Llene todos los campos requeridos"
                }
                dismiss()
            }
        }
    }

    fun initObservers() {
        viewModel.catalogoServiciosResponse.observe(viewLifecycleOwner) {
            binding.apply {
                val list: ArrayList<String> = ArrayList()
                list.add("Selecciona un servicio")
                it.forEach {
                    list.add(it.name)
                }
                val adapterSpinner = ArrayAdapter(
                    requireContext(),
                    R.layout.custom_spinner_item, list
                )
                iSer.spItemSpi.adapter = adapterSpinner
                iSer.spItemSpi.setOnItemSelectedListener(object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position > 0) {
                            val item = it.get(position-1)
                            type = TypeModel(item.id, item.name)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                })
            }
        }
        viewModel.errorResponse.observe(viewLifecycleOwner) {
            //hideLoader()
            UtilAlert.Builder().setTitle("Aviso").setMessage(it).build().show(childFragmentManager, "")
        }
    }
    fun add():Boolean{
        var valido=true
        binding.apply {
            if(iHor.etDias.text.toString().isNullOrEmpty()||iHor.etHoras.text.isNullOrEmpty()) {
                valido=false
            }
            else{
                if (IsMasse) {
                    listHorarios.add(HoraryModelItem(horaryModelItem.days,horaryModelItem.hour_start,horaryModelItem.hour_end))

                } else {
                    if (iSer.spItemSpi.selectedItemPosition > 0) {
                        if(!iDir.etItemEdit.text.toString().isNullOrEmpty()){
                            listService.add(ServiceModel(listOf(HoraryModelItem(horaryModelItem.days,horaryModelItem.hour_start,horaryModelItem.hour_end)),
                                type,iDir.etItemEdit.text.toString(),iDes.etItemEdit.text.toString()))
                        }
                    }else{
                        valido=false
                    }
                }
            }
        }
        return valido
    }
    fun agregada(mensaje: String) {
        Toast.makeText(requireContext(),mensaje, Toast.LENGTH_SHORT).show()
    }

    fun getDays(dias: MutableLiveData<MutableList<Day>>) {
        PublicFunctions().selectDayRange(requireContext(), dias)
    }

    fun getHora(hora: MutableLiveData<String>) {
        PublicFunctions().selectFirstHour(requireContext(), hora)
    }


}