package mx.arquidiocesis.eamxevent.ui

import android.Manifest
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat.*
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_event_other_detail.*
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXFieldValidation
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.base.TimePickerFragment
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxevent.R
import mx.arquidiocesis.eamxevent.constants.Constants
import mx.arquidiocesis.eamxevent.model.*
import mx.arquidiocesis.eamxevent.repository.RepositoryEvent
import mx.arquidiocesis.eamxevent.model.enum.Day as week
import mx.arquidiocesis.eamxcommonutils.multimedia.MapsFragment
import mx.arquidiocesis.eamxcommonutils.multimedia.PERMISSION_LOCATION
import mx.arquidiocesis.eamxcommonutils.util.*
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxcommonutils.util.permission.UtilValidPermission
import mx.arquidiocesis.eamxevent.databinding.FragmentEventOtherDetailBinding
import mx.arquidiocesis.eamxevent.model.enum.TypeActivity

class EventOtherDetailFragment : FragmentBase() {

    lateinit var binding: FragmentEventOtherDetailBinding
    private var hourFirst = ""
    private var hourEnd = ""
    private val TAG_LOADER: String = "EventFragment"
    private var types: Array<TypeActivity> = TypeActivity.values()
    private var idType: Int = 0
    private var idTypeOriginal: Int = 0
    private var eventoId: Int = 0
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    var listDays: MutableList<Day> = mutableListOf()
    val email = eamxcu_preferences.getData(EAMXEnumUser.USER_EMAIL.name, EAMXTypeObject.STRING_OBJECT) as String
    val userId = eamxcu_preferences.getData(EAMXEnumUser.USER_ID.name, EAMXTypeObject.INT_OBJECT) as Int
    val phone = eamxcu_preferences.getData(EAMXEnumUser.USER_PHONE.name, EAMXTypeObject.STRING_OBJECT) as String
    val namex = eamxcu_preferences.getData(EAMXEnumUser.USER_NAME.name, EAMXTypeObject.STRING_OBJECT) as String

    lateinit var viewmodel: ViewModelEvent

    companion object {
        fun newInstance(callBack: EAMXHome): EventOtherDetailFragment {
            val fragment = EventOtherDetailFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    private val viewModelEvent: ViewModelEvent by lazy {
        getViewModel {
            ViewModelEvent(
                RepositoryEvent(requireContext())
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        viewmodel = ViewModelEvent(RepositoryEvent(requireContext()))
        // Inflate thelayout for this fragment
        binding = FragmentEventOtherDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "Actividades_CrearModulo")
            })
        }
        listDays.add(Day(false, week.Domingo.ordinal, week.Domingo.day))
        listDays.add(Day(false, week.Lunes.ordinal, week.Lunes.day))
        listDays.add(Day(false, week.Martes.ordinal, week.Martes.day))
        listDays.add(Day(false, week.Miercoles.ordinal, week.Miercoles.day))
        listDays.add(Day(false, week.Jueves.ordinal, week.Jueves.day))
        listDays.add(Day(false, week.Viernes.ordinal, week.Viernes.day))
        listDays.add(Day(false, week.Sabado.ordinal, week.Sabado.day))
        initView()
        initObservers()
        etResponsable.isEnabled=false
        etEmail.setText(email)
        etEmail.isEnabled=false
        etResponsable.setText(namex)
        etResponsable.isEnabled=false
        etNumberPhone.setText(phone.replace("+52", ""))
        etNumberPhone.isEnabled=false
        switchStatus.isChecked = true
        switchCobro.isChecked = false
        switchVols.isChecked = false
        switchDons.isChecked = false
        requireArguments().let {
            val id = it.getString("eventoId")
            if (id != "") {
                println("XXXY::: ")
                println(id)
                callBack.showToolbar(true, AppMyConstants.updateOther)
                id?.let { it1 ->
                    eventoId = it1.toInt()
                }
                getAllActors()
                //getAllOthers(eventoId)
                initInfo(it)

            } else {
                println("XXXZ::: ")
                println(id)
                callBack.showToolbar(true, AppMyConstants.detailOthers)
            }
        }
    }

    private fun initInfo(it:Bundle){
        idType= it.getInt("tipoEvento")
        idTypeOriginal = it.getInt("tipoEvento")
        binding.spTipoOther.setSelection(idType)
        etNombreC.setText(it.getString("nombre"))
        etgetAddressOther.setText(it.getString("direccion"))

        listDays[0].checked = it.getStringArrayList("dias")!!.contains("Domingo")
        if (!listDays[0].checked) {
            binding.iDays.iDayDo.tvCDay.setTextColor(Color.GRAY)
            binding.iDays.iDayDo.tvCDay.setShadowLayer(5F, 3F, 3F, Color.GRAY)
        } else {
            binding.iDays.iDayDo.tvCDay.setTextColor(Color.rgb(0, 117, 227))
            binding.iDays.iDayDo.tvCDay.setShadowLayer(5F, 3F, 3F, Color.rgb(0, 117, 227))
        }

        listDays[1].checked = it.getStringArrayList("dias")!!.contains("Lunes")
        if (!listDays[1].checked) {
            binding.iDays.iDayLu.tvCDay.setTextColor(Color.GRAY)
            binding.iDays.iDayLu.tvCDay.setShadowLayer(5F, 3F, 3F, Color.GRAY)
        } else {
            binding.iDays.iDayLu.tvCDay.setTextColor(Color.rgb(0, 117, 227))
            binding.iDays.iDayLu.tvCDay.setShadowLayer(5F, 3F, 3F, Color.rgb(0, 117, 227))
        }
        listDays[2].checked = it.getStringArrayList("dias")!!.contains("Martes")
        if (!listDays[2].checked) {
            binding.iDays.iDayMa.tvCDay.setTextColor(Color.GRAY)
            binding.iDays.iDayMa.tvCDay.setShadowLayer(5F, 3F, 3F, Color.GRAY)
        } else {
            binding.iDays.iDayMa.tvCDay.setTextColor(Color.rgb(0, 117, 227))
            binding.iDays.iDayMa.tvCDay.setShadowLayer(5F, 3F, 3F, Color.rgb(0, 117, 227))
        }
        listDays[3].checked = it.getStringArrayList("dias")!!.contains("Miércoles")
        if (!listDays[3].checked) {
            binding.iDays.iDayMi.tvCDay.setTextColor(Color.GRAY)
            binding.iDays.iDayMi.tvCDay.setShadowLayer(5F, 3F, 3F, Color.GRAY)
        } else {
            binding.iDays.iDayMi.tvCDay.setTextColor(Color.rgb(0, 117, 227))
            binding.iDays.iDayMi.tvCDay.setShadowLayer(5F, 3F, 3F, Color.rgb(0, 117, 227))
        }
        listDays[4].checked = it.getStringArrayList("dias")!!.contains("Jueves")
        if (!listDays[4].checked) {
            binding.iDays.iDayJu.tvCDay.setTextColor(Color.GRAY)
            binding.iDays.iDayJu.tvCDay.setShadowLayer(5F, 3F, 3F, Color.GRAY)
        } else {
            binding.iDays.iDayJu.tvCDay.setTextColor(Color.rgb(0, 117, 227))
            binding.iDays.iDayJu.tvCDay.setShadowLayer(5F, 3F, 3F, Color.rgb(0, 117, 227))
        }
        listDays[5].checked = it.getStringArrayList("dias")!!.contains("Viernes")
        if (!listDays[5].checked) {
            binding.iDays.iDayVi.tvCDay.setTextColor(Color.GRAY)
            binding.iDays.iDayVi.tvCDay.setShadowLayer(5F, 3F, 3F, Color.GRAY)
        } else {
            binding.iDays.iDayVi.tvCDay.setTextColor(Color.rgb(0, 117, 227))
            binding.iDays.iDayVi.tvCDay.setShadowLayer(5F, 3F, 3F, Color.rgb(0, 117, 227))
        }
        listDays[6].checked = it.getStringArrayList("dias")!!.contains("Sábado")
        if (!listDays[6].checked) {
            binding.iDays.iDaySa.tvCDay.setTextColor(Color.GRAY)
            binding.iDays.iDaySa.tvCDay.setShadowLayer(5F, 3F, 3F, Color.GRAY)
        } else {
            binding.iDays.iDaySa.tvCDay.setTextColor(Color.rgb(0, 117, 227))
            binding.iDays.iDaySa.tvCDay.setShadowLayer(5F, 3F, 3F, Color.rgb(0, 117, 227))
        }
        switchCobro.isChecked = it.getInt("cobro") != 0
        etMonto.setText(it.getInt("cobro").toString())

        val horaFirst = it.getString("hourStart")!!.split(":")
        FirstSchedule(horaFirst[0].toInt(), horaFirst[1].toInt())
        val horaEnd = it.getString("hourEnd")!!.split(":")
        EndSchedule(horaEnd[0].toInt(), horaEnd[1].toInt())
        //etResponsable.setText(it.responsable)
        //etNumberPhone.setText(it.telefono!!.replace("+52", ""))
        descripcionLbl.setText(it.getString("descripcion"))
        publicoLbl.setText(it.getString("publico"))
        donativosLbl.setText(it.getString("donantesTxt"))
        voluntariosLbl.setText(it.getString("voluntariosTxt"))
        switchDons.isChecked = it.getString("donantesTxt") != "None"
        llDonativos.isVisible = switchDons.isChecked
        switchVols.isChecked = it.getString("voluntariosTxt") != "None"
        llVoluntarios.isVisible = switchVols.isChecked
        switchStatus.isChecked = true
        //llBotones.visibility = View.VISIBLE
    }

    private fun initObservers() {
        viewmodel.responseAllActors.observe(viewLifecycleOwner) { item ->
            hideLoader()
            if (item.isNotEmpty()) {
                llBotonesOther.visibility = View.VISIBLE
            }
        }
        viewModelEvent.showLoaderView.observe(viewLifecycleOwner) {
            showLoader()
        }
        viewModelEvent.validateForm.observe(viewLifecycleOwner) {
            if (it.containsKey(Constants.KEY_NAME)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_name))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_ADDRESS) || it.containsKey(Constants.KEY_LONGITUDE) || it.containsKey(
                    Constants.KEY_LATITUDE
                )
            ) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_location))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_ZONE)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_zone))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_DAYS)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_day))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_HOUR_FIRST)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_hour_first))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_HOUR_END)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_hour_end))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_RESPONSABILITY)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_responsability))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_EMAIL)) {
                if (it[Constants.KEY_EMAIL] == Constants.INVALID_EMAIL) {
                    etEmail.error =
                        getString(R.string.txt_invalidate_email)
                } else {
                    UtilAlert.Builder()
                        .setTitle(getString(R.string.title_dialog_error))
                        .setMessage(getString(R.string.txt_empty_email))
                        .setIsCancel(false)
                        .build().show(childFragmentManager, tag)
                }
            } else if (it.containsKey(Constants.KEY_PHONE)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_phone))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_PHONE)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_invalid_phone))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            } else if (it.containsKey(Constants.KEY_REQUISIT)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_requi))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            }else if (it.containsKey(Constants.KEY_NAMEOTHER)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_nameother))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            }else if (it.containsKey(Constants.KEY_DESCOTHER)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_descother))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            }else if (it.containsKey(Constants.KEY_TYPEOTHER)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_typeother))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            }else if (it.containsKey(Constants.KEY_ADDRESSOTHER)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_locationother))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            }else if(it.containsKey(Constants.KEY_DAYSOTHER)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_error))
                    .setMessage(getString(R.string.txt_empty_dayother))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            }
            hideLoader()
        }
        viewModelEvent.saveResponse.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert.Builder()
                .setTitle("Éxito")
                .setMessage(it)
                .setListener {
                    activity?.onBackPressedDispatcher?.onBackPressed()
                }
                .setIsCancel(false)
                .build()
                .show(childFragmentManager, TAG_LOADER)
        }
        viewModelEvent.errorResponse.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(it)
                .setListener {
                    hideLoader()
                }
                .setIsCancel(false)
                .build()
                .show(childFragmentManager, TAG_LOADER)
        }
    }

    private fun initView() {
        binding.iDays.iDayMa.tvCDay.text = "Lu"
        binding.iDays.iDayMa.tvCDay.text = "Ma"
        binding.iDays.iDayMi.tvCDay.text = "Mi"
        binding.iDays.iDayJu.tvCDay.text = "Ju"
        binding.iDays.iDayVi.tvCDay.text = "Vi"
        binding.iDays.iDaySa.tvCDay.text = "Sa"
        binding.iDays.iDayDo.tvCDay.text = "Do"

        binding.apply {

            etNombreC.hint = "Nombre de la actividad"
            etgetAddressOther.hint =
                "Actualiza la ubicación de tu actividad en el mapa para obtener la dirección"
            etNombreC.addTextChangedListener {
                if (etNombreC.text.toString().isNotEmpty()) {
                    tilNombreC.error = null
                    enableIconStart(tilNombreC, true)
                } else {
                    tilNombreC.error = getString(R.string.enter_diner_name)
                    enableIconStart(tilNombreC, null)
                }
            }

            tvFirstH.addTextChangedListener {
                if (tvFirstH.text.toString().isEmpty()) {
                    tvFirstH.error = null
                } else {
                    tvFirstH.error = getString(R.string.enter_diner_fh)
                }
            }

            tvEndH.addTextChangedListener {
                if (tvEndH.text.toString().isNotEmpty()) {
                    tvEndH.error = null
                } else {
                    tvEndH.error = getString(R.string.enter_diner_eh)
                }
            }

            descripcionLbl.addTextChangedListener {
                val c = descripcionLbl.text.toString().length
                tvDescripcionConteo.text = "$c/500"
            }
            publicoLbl.addTextChangedListener {
                val c = publicoLbl.text.toString().length
                tvPublicoConteo.text = "$c/500"
            }
            donativosLbl.addTextChangedListener {
                val c = donativosLbl.text.toString().length
                tvDonsConteo.text = "$c/500"
            }
            voluntariosLbl.addTextChangedListener {
                val c = voluntariosLbl.text.toString().length
                tvVolunsConteo.text = "$c/500"
            }
        }
        initButtons()
    }

    private fun initButtons() {
        binding.apply {

            iDays.iDayDo.tvCDay.setOnClickListener {
                listDays[0].checked = !listDays[0].checked
                if (!listDays[0].checked) {
                    binding.iDays.iDayDo.tvCDay.setTextColor(Color.GRAY)
                    binding.iDays.iDayDo.tvCDay.setShadowLayer(5F, 3F, 3F, Color.GRAY)
                    binding.iDays.iDayDo.tvCDay.
                    setShadowLayer(5F, 3F, 3F, Color.GRAY)

                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                } else {
                    binding.iDays.iDayDo.tvCDay.setTextColor(Color.rgb(0, 117, 227))
                    binding.iDays.iDayDo.tvCDay.setShadowLayer(5F, 3F, 3F, Color.rgb(0, 117, 227))
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                }
            }
            iDays.iDayLu.tvCDay.setOnClickListener {
                listDays[1].checked = !listDays[1].checked
                if (!listDays[1].checked) {
                    binding.iDays.iDayLu.tvCDay.setTextColor(Color.GRAY)
                    binding.iDays.iDayLu.tvCDay.setShadowLayer(5F, 3F, 3F, Color.GRAY)
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                } else {
                    binding.iDays.iDayLu.tvCDay.setTextColor(Color.rgb(0, 117, 227))
                    binding.iDays.iDayLu.tvCDay.setShadowLayer(5F, 3F, 3F, Color.rgb(0, 117, 227));
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                }
            }
            iDays.iDayMa.tvCDay.setOnClickListener {
                listDays[2].checked = !listDays[2].checked
                if (!listDays[2].checked) {
                    binding.iDays.iDayMa.tvCDay.setTextColor(Color.GRAY)
                    binding.iDays.iDayMa.tvCDay.setShadowLayer(5F, 3F, 3F, Color.GRAY)

                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                } else {
                    binding.iDays.iDayMa.tvCDay.setTextColor(Color.rgb(0, 117, 227))
                    binding.iDays.iDayMa.tvCDay.setShadowLayer(5F, 3F, 3F, Color.rgb(0, 117, 227));
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                }
            }
            iDays.iDayMi.tvCDay.setOnClickListener {
                listDays[3].checked = !listDays[3].checked
                if (!listDays[3].checked) {
                    binding.iDays.iDayMi.tvCDay.setTextColor(Color.GRAY)
                    binding.iDays.iDayMi.tvCDay.setShadowLayer(5F, 3F, 3F, Color.GRAY)
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                } else {
                    binding.iDays.iDayMi.tvCDay.setTextColor(Color.rgb(0, 117, 227))
                    binding.iDays.iDayMi.tvCDay.setShadowLayer(5F, 3F, 3F, Color.rgb(0, 117, 227));
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                }
            }
            iDays.iDayJu.tvCDay.setOnClickListener {
                listDays[4].checked = !listDays[4].checked
                if (!listDays[4].checked) {
                    binding.iDays.iDayJu.tvCDay.setTextColor(Color.GRAY)
                    binding.iDays.iDayJu.tvCDay.setShadowLayer(5F, 3F, 3F, Color.GRAY)

                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                } else {
                    binding.iDays.iDayJu.tvCDay.setTextColor(Color.rgb(0, 117, 227))
                    binding.iDays.iDayJu.tvCDay.setShadowLayer(5F, 3F, 3F, Color.rgb(0, 117, 227))
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                }
            }
            iDays.iDayVi.tvCDay.setOnClickListener {
                listDays[5].checked = !listDays[5].checked
                if (!listDays[5].checked) {
                    binding.iDays.iDayVi.tvCDay.setTextColor(Color.GRAY)
                    binding.iDays.iDayVi.tvCDay.setShadowLayer(5F, 3F, 3F, Color.GRAY)
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                } else {
                    binding.iDays.iDayVi.tvCDay.setTextColor(Color.rgb(0, 117, 227))
                    binding.iDays.iDayVi.tvCDay.setShadowLayer(5F, 3F, 3F, Color.rgb(0, 117, 227));
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                }
            }
            iDays.iDaySa.tvCDay.setOnClickListener {
                listDays[6].checked = !listDays[6].checked
                if (!listDays[6].checked) {
                    binding.iDays.iDaySa.tvCDay.setTextColor(Color.GRAY)
                    binding.iDays.iDaySa.tvCDay.setShadowLayer(5F, 3F, 3F, Color.GRAY)
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                } else {
                    binding.iDays.iDaySa.tvCDay.setTextColor(Color.rgb(0, 117, 227))
                    binding.iDays.iDaySa.tvCDay.setShadowLayer(5F, 3F, 3F, Color.rgb(0, 117, 227));
                    val gson = Gson()
                    val jsonString = gson.toJson(listDays)
                }
            }

            switchCobro.setOnCheckedChangeListener { _, isChecked ->
                etMonto.setText("0")
                if (isChecked) {
                    lMonto.visibility = View.VISIBLE
                    switchCobro.thumbTintList = getColorStateList(requireContext(), R.color.green_retirar)
                } else {
                    lMonto.visibility = View.GONE
                    switchCobro.thumbTintList = getColorStateList(requireContext(), R.color.hint_color)
                }
            }

            switchStatus.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    switchStatus.thumbTintList = getColorStateList(requireContext(), R.color.green_retirar)
                } else {
                    switchStatus.thumbTintList = getColorStateList(requireContext(), R.color.hint_color)
                }
            }

            switchDons.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    donativosLbl.setText("Información relevante sobre los donativos que se requieren para la actividad. Qué y cómo donar.")
                    switchDons.thumbTintList = getColorStateList(requireContext(), R.color.green_retirar)
                    llDonativos.isVisible=true
                } else {
                    switchDons.thumbTintList = getColorStateList(requireContext(), R.color.hint_color)
                    llDonativos.isVisible=false
                }
            }
            switchVols.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    voluntariosLbl.setText("Información relevante sobre las actividades a desempeñar por los voluntarios. Requisitos y descripción del voluntariado.")
                    switchVols.thumbTintList = getColorStateList(requireContext(), R.color.green_retirar)
                    llVoluntarios.isVisible=true
                } else {
                    switchVols.thumbTintList = getColorStateList(requireContext(), R.color.hint_color)
                    llVoluntarios.isVisible=false
                }
            }

            val adaptador = ArrayAdapter.createFromResource(requireContext(),
                R.array.activityType,
                android.R.layout.simple_spinner_dropdown_item
            )

            spTipoOther.adapter = adaptador
            spTipoOther.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    idType = types[position].pos
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    //lblSeleccion.text = "Sin selección"
                }
            }
            btnParticipantes.setOnClickListener {
               NavigationFragment.Builder()
                    .setActivity(requireActivity())
                    .setView(requireView().parent as ViewGroup)
                    .setFragment(EventActorFragment.newInstance(callBack) as Fragment)
                    .setBundle(Bundle().apply {
                        putInt("event_id", eventoId)
                    })
                    .build().nextWithReplace()
            }

            btnGuardar.setOnClickListener { eventRegister() }
            tvAddress.setOnClickListener { showMap() }
            tvFirstH.setOnClickListener { showTimePickerFirst() }
            tvEndH.setOnClickListener { showTimePickerEnd() }
        }
    }

    fun showMap() {
        if (chechPermissions()) {
            MapsFragment(latitude, longitude, "estará ubicada tu actividad.") { rlatitude, rlongitude, raddress, municipality ->
                etgetAddressOther.setText(raddress)
                latitude = rlatitude
                longitude = rlongitude
                //println(latitude.toString() + "to" + longitude.toString())
            }.show(childFragmentManager, TAG_LOADER)
        }
    }

    private fun chechPermissions(): Boolean {
        return UtilValidPermission().validListPermissionsAndBuildRequest(
            this@EventOtherDetailFragment, arrayListOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_LOCATION
        )
    }

    fun showTimePickerFirst() {
        val timePicker = TimePickerFragment(isMax = true) { hour, minute -> FirstSchedule(hour, minute) }
        timePicker.show(parentFragmentManager, "timePicker")
    }

    private fun FirstSchedule(hour: Int, minute: Int) {
        val hourCurrent = if (hour < 10) "0$hour" else hour
        val minuteCurrent = if (minute < 10) "0$minute" else minute
        tvFirstH.setText("$hourCurrent:$minuteCurrent")
        hourFirst = "$hourCurrent:$minuteCurrent"
        tvFirstH.error = null
    }

    private fun showTimePickerEnd() {
        val timePicker =
            TimePickerFragment(isMax = true) { hour, minute -> EndSchedule(hour, minute) }
        timePicker.show(parentFragmentManager, "timePicker")
    }

    private fun EndSchedule(hour: Int, minute: Int) {
        val hourCurrent = if (hour < 10) "0$hour" else hour
        val minuteCurrent = if (minute < 10) "0$minute" else minute
        tvEndH.setText("$hourCurrent:$minuteCurrent")
        hourEnd = "$hourCurrent:$minuteCurrent"
        tvEndH.error = null
    }

    fun enableIconStart(input: TextInputLayout, success: Boolean?) {
        when (success) {
            true -> {
                input.endIconDrawable =
                    getDrawable(requireContext(), R.drawable.ic_baseline_check_24)
                input.setEndIconTintList(
                    ColorStateList.valueOf(getColor(requireContext(), R.color.success))
                )
            }
            false -> {
                input.endIconDrawable = getDrawable(requireContext(), R.drawable.ic_check_error)
                input.setEndIconTintList(
                    ColorStateList.valueOf(getColor(requireContext(), R.color.error))
                )
            }
            null -> {
                input.endIconDrawable = null
            }
        }
    }

    private fun eventRegister() {
        if (idTypeOriginal != 0 && idTypeOriginal != idType){
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage("Para cambiar el tipo de actividad, primero debes desactivar la actual, guardar cambios y así podrás crear una nueva actividad de diferente tipo.")
                .setListener {
                    hideLoader()
                }
                .setIsCancel(false)
                .build()
                .show(childFragmentManager, TAG_LOADER)
            return
        }
        var donsTxt="None"
        if (switchDons.isChecked){
            donsTxt=donativosLbl.text.toString().trim()
        }
        var volsTxt="None"
        if (switchVols.isChecked){
            volsTxt=voluntariosLbl.text.toString().trim()
        }
        val userId = eamxcu_preferences.getData(EAMXEnumUser.USER_ID.name, EAMXTypeObject.INT_OBJECT) as Int
        val listSchedules: MutableList<Schedules> =
            mutableListOf(Schedules(listDays, tvFirstH.text.toString(), tvEndH.text.toString()))
        val otherObj:OtherEvent=OtherEvent(
            horarios = listSchedules,
            responsable = etResponsable.text.toString().trim(),
            correo = etEmail.text.toString(),
            telefono = etNumberPhone.text.toString(),
            direccion = etgetAddressOther.text.toString(),
            cobro = etMonto.text.toString().toInt(),
            descripcion = descripcionLbl.text.toString().trim(),
            publico = publicoLbl.text.toString().trim(),
            voluntariosBool = if (switchVols.isChecked) 1 else 0,
            donantesBool = if (switchDons.isChecked) 1 else 0,
            voluntariosTxt = volsTxt,
            donantesTxt = donsTxt,
            status = if (switchStatus.isChecked) 1 else 0,
            nombre = etNombreC.text.toString(),
            userId = userId,
            eventoId = eventoId,
            tipoEvento = idType
        )
        viewModelEvent.validateFormRegisterOther(otherObj)
    }

    fun getAllOthers(id: Int) {
        //ya no porque se va a recibir la info desde la tabla...
        //showLoader()
        //viewmodel.requestAllOther(id, 0)
    }

    fun getAllActors() {
        showLoader()
        viewmodel.requestAllActors()
    }
}

