package mx.arquidiocesis.registrosacerdote.view

import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.eamx_priest_register_fragment.*
import mx.arquidiocesis.eamxcommonutils.base.DatePickerFragment
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.model.ViewPagerModel
import mx.arquidiocesis.eamxcommonutils.util.*
import mx.arquidiocesis.eamxcommonutils.util.ViewPager.ViewPagerPrincipal
import mx.arquidiocesis.registrosacerdote.R
import mx.arquidiocesis.registrosacerdote.adapter.ActivitiesAdapter
import mx.arquidiocesis.registrosacerdote.adapter.AdapterCustomSpinner
import mx.arquidiocesis.registrosacerdote.constants.Constants
import mx.arquidiocesis.registrosacerdote.customviews.CongregationDialogFragment
import mx.arquidiocesis.registrosacerdote.model.ActivitiesModel
import mx.arquidiocesis.registrosacerdote.model.catalog.InterestTopic
import mx.arquidiocesis.registrosacerdote.model.update.base.BaseOnlyId
import mx.arquidiocesis.registrosacerdote.repository.Repository
import mx.arquidiocesis.registrosacerdote.viewmodel.PriestRegisterViewModel
import okhttp3.HttpUrl.Companion.toHttpUrl
import retrofit2.http.Url
import kotlin.collections.ArrayList


class EAMXPriestRegisterFragment : FragmentBase() {

    companion object {
        fun newInstance(callBack: EAMXHome): EAMXPriestRegisterFragment {
            val priestRegisterFragment = EAMXPriestRegisterFragment()
            priestRegisterFragment.callBack = callBack
            return priestRegisterFragment
        }

        const val KEY_LIFE_STATUS = "LIFE_STATUS"
        const val KEY_INTEREST_TOPICS = "INTEREST_TOPICS"

    }

    private lateinit var activitiesAdapter: ActivitiesAdapter

    private val priestRegisterViewModel: PriestRegisterViewModel by lazy {
        getViewModel {
            PriestRegisterViewModel(Repository(context = requireContext()))
        }
    }
    private var dateBirthToSet: String = ""
    private var dateOrdinationToSet: String = ""
    private var congregationValue: Int = 0
    private var activitiesList: MutableList<ActivitiesModel> = mutableListOf()
    private var flagDiocesanOrReligious = 0
    private var birthDate = ""
    private var ordinationDate = ""
    private var stream = ""
    private var fragment = DialogFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.eamx_priest_register_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "MiPerfil_RegistroSacerdote")
            })
        }
        initObservers()
        initListener()
        initServices()
        initAdapterActivities()
    }

    private fun initObservers() {
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
        val ordinationDate = eamxcu_preferences.getData(
            EAMXEnumUser.ORDINATION_DATE.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String
        val birthDate = eamxcu_preferences.getData(
            EAMXEnumUser.BIRTHDATE.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String
        val description = eamxcu_preferences.getData(
            EAMXEnumUser.DESCRIPTION.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String
        val stream = eamxcu_preferences.getData(
            EAMXEnumUser.STREAM.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String

        etNamePriest.text = Editable.Factory.getInstance().newEditable(name)
        etNamePriest.isEnabled = false
        etFatherSurname.text = Editable.Factory.getInstance().newEditable(lastName)
        etFatherSurname.isEnabled = false
        etMotherSurname.text = Editable.Factory.getInstance().newEditable(middleName)
        etMotherSurname.isEnabled = false
        etEmail.text = Editable.Factory.getInstance().newEditable(email)
        etEmail.isEnabled = false
        tvBirthDate.text = Editable.Factory.getInstance().newEditable(birthDate.replace("-","/"))
        tvBirthDate.isEnabled = false
        tvOrdinationDate.text = Editable.Factory.getInstance().newEditable(ordinationDate.replace("-","/"))
        tvOrdinationDate.isEnabled = false
        etDescription.text = Editable.Factory.getInstance().newEditable(description)
        etUrl.text = Editable.Factory.getInstance().newEditable(stream)


        priestRegisterViewModel.activitiesLiveData.observe(viewLifecycleOwner) {
            it.let {
                val listActivities: ArrayList<ActivitiesModel> = ArrayList()
                listActivities.add(
                    ActivitiesModel(
                        id = 0,
                        name = getString(R.string.txt_registry_priest_activities_spinner)
                    )
                )
                listActivities.addAll(it.map { item ->
                    ActivitiesModel(
                        id = item.id,
                        name = item.name
                    )
                })

                spActivities.adapter = AdapterCustomSpinner(
                    activity?.baseContext!!,
                    android.R.layout.simple_spinner_dropdown_item,
                    listActivities
                )

                spActivities.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val item = spActivities.selectedItem as ActivitiesModel
                        if (position > 0 && !activitiesAdapter.existId(item.id)) {
                            activitiesAdapter.addItem(item)
                        }

                        if (position == Constants.OTHER_ACTIVITIES) {
                            etOtherActivity.visibility = View.VISIBLE
                            activitiesAdapter.clear()
                        }

                        if (position != Constants.OTHER_ACTIVITIES) {
                            etOtherActivity.visibility = View.GONE
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                    }
                }
            }

            hideLoader()
        }

        priestRegisterViewModel.showLoaderView.observe(viewLifecycleOwner) {
            showLoader()
        }

        priestRegisterViewModel.validateForm.observe(viewLifecycleOwner) {


            if (it.containsKey(Constants.KEY_NAME)) {
                etNamePriest.error = getString(R.string.txt_registry_priest_error_field_empty)
            }

            if (it.containsKey(Constants.KEY_FIRST_SURNAME)) {
                etMotherSurname.error = getString(R.string.txt_registry_priest_error_field_empty)
            }

            if (it.containsKey(Constants.KEY_SECOND_SURNAME)) {
                etFatherSurname.error = getString(R.string.txt_registry_priest_error_field_empty)
            }

            if (it.containsKey(Constants.KEY_EMAIL))
                if (it[Constants.KEY_EMAIL] == Constants.INVALID_EMAIL) {
                    etEmail.error = getString(R.string.txt_registry_priest_invalidate_email)
                } else {
                    etEmail.error = getString(R.string.txt_registry_priest_error_field_empty)
                }

            if (it.containsKey(Constants.KEY_BIRTHDATE)) {
                tvBirthDate.error = getString(R.string.txt_registry_priest_error_field_empty)
            }

            if (it.containsKey(Constants.KEY_ORDINATION)) {
                tvOrdinationDate.error = getString(R.string.txt_registry_priest_error_field_empty)
            }

            if (it.containsKey(Constants.KEY_CONGREGATION)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_warning))
                    .setMessage(getString(R.string.selected_congragation))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            }

            if (it.containsKey(Constants.KEY_ACTIVITY)) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_warning))
                    .setMessage(getString(R.string.selected_activity))
                    .setIsCancel(false)
                    .build().show(childFragmentManager, tag)
            }
            hideLoader()
        }

        priestRegisterViewModel.errorResponse.observe(viewLifecycleOwner) { message ->
            hideLoader()
            var messageCustom = SpannableString(message)
            if (message.contains(getString(R.string.email_to_error))) {
                messageCustom = setStyleNew(message, getString(R.string.email_to_error))
            }

            UtilAlert.Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessageCustom(messageCustom).setIsCancel(false)
                .setIsCancel(false)
                .build().show(childFragmentManager, tag)
        }

        priestRegisterViewModel.registerResponse.observe(viewLifecycleOwner) {
            hideLoader()
            fragment = ViewPagerPrincipal(
                listOf(
                    ViewPagerModel(
                        "Estamos trabajando en la actualización de los datos. Agradecemos su comprensión.", //Gracias padre ${name} has sido registrado como sacerdote y ahora tienes derecho a:
                        BitmapFactory.decodeResource(resources, R.drawable.onbording_registro),
                        2, listOf(
                            "Ver y editar tu perfil",
                            "Consultar el historial de donaciones recibidas",
                            "Publicar en red social a nombre propio"
                        )
                    )
                )
            ) {
                fragment.dismiss()
            }
            fragment.show(childFragmentManager, "LOADER")
        }
    }
    private fun initServices() {
        showLoader()
        priestRegisterViewModel.getActivitiesList()
    }
    private fun initListener() {
        tvBirthDate.setOnClickListener { showDatePickerBirthDate() }
        ivBirthDate.setOnClickListener { showDatePickerBirthDate() }
        tvOrdinationDate.setOnClickListener { showDatePickerOrdination() }
        ivOrdinationDate.setOnClickListener { showDatePickerOrdination()}
        btnSave.setOnClickListener { priestRegister() }
        rbDiocesan.setOnClickListener { showDiocesan() }
        rbReligius.setOnClickListener { showReligious() }
        spOrderOrCongregation.setOnClickListener { showDialogCongregation() }

    }


    fun showDatePickerBirthDate() {
        val datePicker =
            DatePickerFragment(isMax = true) { day, month, year -> onBirthDate(day, month, year) }
        datePicker.show(parentFragmentManager, "datePicker")
    }

    private fun onBirthDate(day: Int, month: Int, year: Int) {
        val dayCurrent = if (day < 10) "0$day" else day
        val monthCurrent = month + 1
        val montCurrent = if (month < 9) "0$monthCurrent" else monthCurrent
        tvBirthDate.setText("$dayCurrent/$montCurrent/$year")
        birthDate = "$year-$montCurrent-$dayCurrent"
        tvBirthDate.error = null
    }

    private fun showDatePickerOrdination() {
        val datePicker =
            DatePickerFragment { day, month, year -> onDateOrdination(day, month, year) }
        datePicker.show(parentFragmentManager, "datePicker")
    }

    private fun onDateOrdination(day: Int, month: Int, year: Int) {
        val dayCurrent = if (day < 10) "0$day" else day
        val monthCurrent = month + 1
        val montCurrent = if (month < 9) "0$monthCurrent" else monthCurrent
        tvOrdinationDate.setText("$dayCurrent/$montCurrent/$year")
        ordinationDate = "$year-$montCurrent-$dayCurrent"
        tvOrdinationDate.error = null
    }

    private fun priestRegister() {
        if (spActivities.selectedItemPosition == Constants.OTHER_ACTIVITIES) {
            activitiesAdapter.clear()
            activitiesList.add(
                ActivitiesModel(
                    id = spActivities.selectedItemPosition,
                    name = etOtherActivity.text.toString()
                )
            )
        }

        val lifeStatus =
            eamxcu_preferences.getData(KEY_LIFE_STATUS, EAMXTypeObject.INT_OBJECT) as Int
        val interestTopics =
            eamxcu_preferences.getData(KEY_INTEREST_TOPICS, EAMXTypeObject.STRING_OBJECT) as String
        val gson = Gson()
        val interestTopicsList: List<InterestTopic> =
            gson.fromJson(interestTopics, object : TypeToken<List<InterestTopic>>() {}.type)

        if (activitiesList.isEmpty()) {
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(getString(R.string.selected_activity))
                .setIsCancel(false)
                .build().show(childFragmentManager, tag)

            return
        }

        priestRegisterViewModel.validateFormPriestRegister(
            etNamePriest.text.toString(),
            etFatherSurname.text.toString(),
            etMotherSurname.text.toString(),
            etDescription.text.toString(),
            tvBirthDate.text.toString().transformaData(),
            tvOrdinationDate.text.toString().transformaData(),
            etEmail.text.toString(),
            activitiesList,
            BaseOnlyId(congregationValue),
            etUrl.text.toString(),
            flagDiocesanOrReligious,
            BaseOnlyId(lifeStatus),
            interestTopicsList,
        )
    }

    private fun initAdapterActivities() {
        activitiesAdapter = ActivitiesAdapter(activitiesList, rvActivity)
        rvActivity.apply {
            layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            adapter = activitiesAdapter
        }
    }

    private fun showDiocesan() {
        spOrderOrCongregation.visibility = View.GONE
        flagDiocesanOrReligious = 0
    }

    private fun showReligious() {
        spOrderOrCongregation.visibility = View.VISIBLE
        flagDiocesanOrReligious = 1
    }

    private fun showDialogCongregation() {
        activity?.supportFragmentManager?.let {
            val dialog = CongregationDialogFragment.newInstance()
            dialog.congregationSelected = { item ->
                tvOrderOrCongregation.text = item.description
                congregationValue = item.id
            }
            dialog.show(it, "Show Dialog Congregation")
        }
    }

    private fun setStyleNew(text: String, searchText: String): SpannableString {
        val spannableString = SpannableString(text)

        spannableString.apply {
            setSpan(
                StyleSpan(Typeface.NORMAL),
                0,
                text.indexOf(searchText),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                StyleSpan(Typeface.BOLD),
                text.indexOf(searchText),
                text.indexOf(searchText) + searchText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                UnderlineSpan(),
                text.indexOf(searchText),
                text.indexOf(searchText) + searchText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                StyleSpan(Typeface.NORMAL),
                text.indexOf(searchText) + searchText.length,
                text.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return spannableString
    }
}

