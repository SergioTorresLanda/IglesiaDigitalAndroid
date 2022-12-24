package mx.arquidiocesis.eamxprofilemodule.ui.information

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.eamx_profile_info_fragment.*
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXSignOut
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.log
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxcommonutils.util.permission.UtilValidPermission
import mx.arquidiocesis.eamxcommonutils.util.toast
import mx.arquidiocesis.eamxprofilemodule.R
import mx.arquidiocesis.eamxprofilemodule.adapter.*
import mx.arquidiocesis.eamxprofilemodule.customviews.ChurchesMapFragment
import mx.arquidiocesis.eamxprofilemodule.customviews.CongregationDialogFragment
import mx.arquidiocesis.eamxprofilemodule.databinding.EamxProfileInfoFragmentBinding
import mx.arquidiocesis.eamxprofilemodule.model.*
import mx.arquidiocesis.eamxprofilemodule.model.local.DataModelSharedPreferences
import mx.arquidiocesis.eamxprofilemodule.model.local.UserNames
import mx.arquidiocesis.eamxprofilemodule.model.update.base.ActivityChurchModel
import mx.arquidiocesis.eamxprofilemodule.model.userdetail.User
import mx.arquidiocesis.eamxprofilemodule.model.userdetail.toChurchAndDescriptionModel
import mx.arquidiocesis.eamxprofilemodule.repository.RepositoryProfile
import mx.arquidiocesis.eamxprofilemodule.ui.profile.PERMISSION_CAMERA
import mx.arquidiocesis.eamxprofilemodule.ui.profile.PERMISSION_LOCATION
import mx.arquidiocesis.eamxprofilemodule.ui.profile.PERMISSION_STORAGE
import mx.arquidiocesis.eamxprofilemodule.viewmodel.*
import mx.arquidiocesis.registrosacerdote.view.EAMXPriestRegisterFragment


class EAMXProfileInfoFragment : FragmentBase() {

    private val TAG_LOADER: String = "EAMXProfileInfoFragment"

    private lateinit var binding: EamxProfileInfoFragmentBinding
//    private val SETTINGS_REQUEST_CODE = 2

    lateinit var signOut: EAMXSignOut

    private val viewModelProfile: EAMXViewModelProfile by lazy {
        getViewModel {
            EAMXViewModelProfile(
                RepositoryProfile(requireContext())
            )
        }
    }

    lateinit var listener: (UserNames) -> Unit

    private lateinit var activitiesAdapter: ActivitiesAdapter
    lateinit var churchAdapter: ChurchAdapter
    private lateinit var diaconoChurchAdapter: DiaconoChurchAdapter
    private var diaconoChurchList: MutableList<ChurchModel> = mutableListOf()
    private var churchList: MutableList<ChurchAndDescriptionModel> = mutableListOf()
    private var activitiesList: MutableList<ActivitiesModel> = mutableListOf()
    var isLoading = false
    var idComunnity: Int? = 0
    var prefix: DataWithDescription = DataWithDescription("", 0)
    var styleLife = 0
    var arrayStyleLife: MutableList<DataWithNameCode> = mutableListOf()

    companion object {
        @JvmStatic
        fun newInstance(
            signOut: EAMXSignOut,
            callBack: EAMXHome,
            listener: (UserNames) -> Unit
        ): EAMXProfileInfoFragment {
            val profileInfo = EAMXProfileInfoFragment()
            profileInfo.signOut = signOut
            profileInfo.callBack = callBack
            profileInfo.listener = listener
            return profileInfo
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EamxProfileInfoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    /*override fun onCreat() {
        Log.d("Me salgo","Siiii");
        super.onStop()
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initView()
    }

    private fun initObservers() {
        viewModelProfile.stateLifeModel.observe(viewLifecycleOwner) { items ->
            var idxspStyle = 0
            arrayStyleLife = items.data
            binding.apply {
                spStyleLife.apply {
                    adapter = AdapterCustomSpinnerWithCode(
                        activity?.baseContext!!,
                        android.R.layout.simple_spinner_dropdown_item,
                        items.data.map {
                            DataWithDescriptionCode(
                                id = it.id,
                                description = it.name ?: "",
                                code = it.code ?: ""
                            )
                        }
                    )
                }
            }
            items.data.forEachIndexed { index, element ->
                if (element.id == styleLife)
                    idxspStyle = index
            }
            spStyleLife.setSelection(idxspStyle)

            viewModelProfile.getTopics()
        }

        viewModelProfile.prefixModel.observe(viewLifecycleOwner) { items ->
            var idxPrefix = 0
            val styleUserItem = spStyleLife.selectedItem as DataWithDescriptionCode
            if (items.data.size > 0 && styleUserItem.id != 4) {
                binding.apply {
                    spPrefix.apply {
                        adapter = AdapterCustomSpinner(
                            activity?.baseContext!!,
                            android.R.layout.simple_spinner_dropdown_item,
                            items.data
                        )
                    }
                }
                spPrefix.visibility = View.VISIBLE
                tvPrefix.visibility = View.VISIBLE

                idxPrefix = items.data.indexOf(prefix)
                if (idxPrefix != -1) {
                    spPrefix.setSelection(idxPrefix)
                }
            }
        }

        viewModelProfile.topicsModel.observe(viewLifecycleOwner) { items ->
            binding.apply {
                spTopics.apply {
                    adapter = AdapterCustomSpinner(
                        activity?.baseContext!!,
                        android.R.layout.simple_spinner_dropdown_item,
                        items.data
                    )
                }
            }

            binding.spTopics.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val item = binding.spTopics.selectedItem as DataWithDescription
                    if (position > 0 && !activitiesAdapter.existId(item.id)) {
                        activitiesAdapter.addItem(
                            ActivitiesModel(
                                item.id,
                                item.description
                            )
                        )
                    }
                }
            }

            viewModelProfile.getProvidedServices()
        }

        viewModelProfile.servicesModel.observe(viewLifecycleOwner) { items ->
            binding.apply {
                initAdapterChurch(items.data.map { item ->
                    DataWithDescription(
                        id = item.id,
                        description = item.description
                    )
                })

                spServicesChurch.apply {
                    adapter = AdapterCustomSpinner(
                        activity?.baseContext!!,
                        android.R.layout.simple_spinner_dropdown_item,
                        items.data.filter { it.description != SELECT_OTHER }
                    )
                }
            }
            viewModelProfile.getUserDetail()
        }

        viewModelProfile.responseUserDetail.observe(viewLifecycleOwner) { item ->
            hideLoader()
            item?.let {
                it.data.let { user ->
                    user.User.let { dataUser ->
                        binding.apply {

                            "Profile user -> set image".log()
                            listener(
                                UserNames(
                                    uri = dataUser.image ?: ""
                                )
                            )
                            etName.setText(dataUser.name)
                            etFatherSurname.setText(dataUser.first_surname)
                            etMotherSurname.setText(dataUser.second_surname ?: "")
                            etTelephoneNumber.setText(dataUser.phone_number.replace("+52", ""))
                            etEmail.setText(dataUser.email)

                            "Profile user -> set data".log()

                            val enabledData = viewModelProfile.userNeedCompletedProfile()
                            dataUser.interest_topics?.let { topics ->
                                topics.forEach { item ->
                                    if (!activitiesAdapter.existId(item.id)) {
                                        val nameCustom =
                                            if (item.name == null || item.name.isEmpty()) {
                                                val local = Gson().fromJson(
                                                    eamxcu_preferences.getData(
                                                        "TOPIC",
                                                        EAMXTypeObject.STRING_OBJECT
                                                    ).toString(),
                                                    DataModelSharedPreferences::class.java
                                                )
                                                if (local != null && local.data.isNotEmpty()) {
                                                    local.data.find { data -> data.id == item.id }?.name
                                                        ?: ""
                                                } else {
                                                    ""
                                                }
                                            } else {
                                                item.name
                                            }
                                        activitiesAdapter.addItem(
                                            activity = ActivitiesModel(
                                                item.id,
                                                nameCustom
                                            ),
                                            enabled = enabledData
                                        )
                                    }
                                }
                            }
                            dataUser.is_provider?.let { is_provider ->
                                when (is_provider) {
                                    "NO" -> {
                                        binding.rbNo.isChecked = true
                                        binding.etSearchChurch.visibility = View.GONE
                                        binding.llResponsibleCommunity.visibility = View.GONE
                                        binding.rvChurch.visibility = View.GONE
                                    }
                                    "CHURCH" -> {
                                        binding.rbYes.isChecked = true
                                        binding.etSearchChurch.visibility = View.VISIBLE
                                        binding.llResponsibleCommunity.visibility = View.GONE
                                    }
                                    "COMMUNITY" -> {
                                        binding.rbYesC.isChecked = true
                                        binding.llResponsibleCommunity.visibility = View.VISIBLE
                                        binding.etSearchChurch.visibility = View.GONE
                                    }
                                }
                            }
                            dataUser.profile?.let { profile ->
                                binding.swResponsibleCommunity.isChecked =
                                    profile == "COMMUNITY_RESPONSIBLE"
                                tvSwResposibleCommunity.text =
                                    if (profile == "COMMUNITY_RESPONSIBLE") "Sí" else "No"
                            } ?: kotlin.run {
                                binding.swResponsibleCommunity.isChecked = false
                                tvSwResposibleCommunity.text = "No"
                            }
                            dataUser.life_status?.let { life ->
                                styleLife = life.id
                                var idxspStyle = 0
                                arrayStyleLife.forEachIndexed { index, element ->
                                    if (element.id == styleLife)
                                        idxspStyle = index
                                }
                                spStyleLife.setSelection(idxspStyle)
                                val styleUserItem =
                                    spStyleLife.selectedItem as DataWithDescriptionCode
                                setView(
                                    styleUserItem.description ?: "",
                                    false,
                                    uiEnabled = true,
                                    code = styleUserItem.code
                                )
                                showDataLocal(dataUser)
                                dataUser.prefix?.let { prefixData ->
                                    spPrefix.setSelection(prefixData.id ?: 0)
                                    prefix = DataWithDescription(
                                        prefixData.description ?: "",
                                        prefixData.id ?: 0
                                    )
                                }

                            }
                            isLoading = dataUser.life_status != null

                        }
                    }
                }
            }
        }

        viewModelProfile.errorResponse.observe(viewLifecycleOwner) { message ->
            hideLoader()
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(message)
                .setListener {
                    hideLoader()
                }
                .setIsCancel(false)
                .build()
                .show(childFragmentManager, TAG_LOADER)
        }

        viewModelProfile.updateMessageResponse.observe(viewLifecycleOwner) { message ->
            hideLoader()
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(message)
                .setListener {
                    requireActivity().run {
                        startActivity(
                            Intent().apply {
                                setClassName(
                                    requireActivity(),
                                    "mx.arquidiocesis.eamxgeneric.activities.EAMXHomeActivity"
                                )
                            })

                        finish()
                    }
                }
                .setIsCancel(false)
                .build().show(childFragmentManager, TAG_LOADER)

            listener(
                UserNames(
                    name = binding.etName.text.toString(),
                    first_surname = binding.etFatherSurname.text.toString(),
                    second_surname = binding.etMotherSurname.text.toString(),
                    uri = ""
                )
            )

            viewModelProfile.userCompleteProfile()
        }

        viewModelProfile.errorForm.observe(viewLifecycleOwner) { error ->

            binding.etName.error = if (error.containsKey(binding.etName.id)) {
                error[binding.etName.id].toString()
            } else {
                null
            }

            binding.etFatherSurname.error = if (error.containsKey(binding.etFatherSurname.id)) {
                error[binding.etFatherSurname.id].toString()
            } else {
                null
            }

            binding.etMotherSurname.error = if (error.containsKey(binding.etMotherSurname.id)) {
                error[binding.etMotherSurname.id].toString()
            } else {
                null
            }

            binding.etTelephoneNumber.error = if (error.containsKey(binding.etTelephoneNumber.id)) {
                error[binding.etTelephoneNumber.id].toString()
            } else {
                null
            }
        }
    }

    private fun showDataLocal(dataUser: User) {
        dataUser.life_status?.let { data ->
            when (data.name) {
                SINGLE, MARRIED, WIDOWER, LAIC -> {
                    dataUser.services_provided?.let {
                        rbYes.isChecked = it.isNotEmpty()
                        rbNo.isChecked = it.isEmpty()
                        if (it.isNotEmpty()) {
                            it.forEach {
                                addSearchChurch(it.toChurchAndDescriptionModel())
                            }
                        }
                    }
                }
                LAIC_CONSECRATED, RELIGIOUS -> {
                    dataUser.congregation?.let {
                        if (it.name.isNullOrEmpty()) return@let
                        binding.apply {
                            etSearchCongregations.setText(it.name)
                            etPastoralActivity.setText(dataUser.pastoral_work)
                        }
                        viewModelProfile.congregationItem = CongregationModel(it.name, it.id)
                    }
                    dataUser.community?.let {
                        binding.apply {
                            if (it.name != null) {
                                etSearchCommunity.setText(it.name)
                            }
                            if (it.id != null) {
                                idComunnity = it.id ?: 0
                            }

                        }
                    }
                }
                LAIC -> {
                    dataUser.is_provider?.let {
                        binding.apply {
                            if (it != null) {
                                when (it) {
                                    "NO" -> {
                                        rbNo.isChecked = true
                                        etSearchChurch.visibility = View.GONE
                                        llResponsibleCommunity.visibility = View.GONE
                                        rvChurch.visibility = View.GONE
                                        //TODO AGREGAR LIMPIEZA DE TODO
                                    }
                                    "CHURCH" -> {
                                        rbYes.isChecked = true
                                        etSearchChurch.visibility = View.VISIBLE
                                        llResponsibleCommunity.visibility = View.GONE
                                    }
                                    "COMMUNITY" -> {
                                        rbYesC.isChecked = true
                                        llResponsibleCommunity.visibility = View.VISIBLE
                                        etSearchChurch.visibility = View.GONE
                                    }
                                }
                            }
                        }
                    }
                    dataUser.community?.let {
                        binding.apply {
                            if (it.name != null) {
                                etSearchCommunity.setText(it.name)
                            }
                            if (it.id != null) {
                                idComunnity = it.id ?: 0
                            }

                        }
                        // viewModelProfile.congregationItem = CongregationModel(it.name, it.id)
                    }
                }
                DIACO -> {
                    dataUser.location_modules?.let { d ->
                        if (d.isNotEmpty()) {
                            d.forEach { i ->
                                addDiaconoChurch(ChurchModel(i.id, i.name, "", i.imageUrl, "", ""))
                            }
                        }
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun initView() {
        //Cuando es la primera vez que se abre la app el valor de firsttime
        // es false por que a un no existe y ese es el valor por fefault
        //this.enabledInputs(viewModelProfile.userNeedCompletedProfile())
        initElements(binding, activity, this)

        initButtons()
        initAdapterTopics()
        initAdapterDiaconoChurch()
        initPermission()
    }

    private fun initPermission() {

        if (UtilValidPermission().validListPermissionsAndBuildRequest(
                this@EAMXProfileInfoFragment,
                arrayListOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                PERMISSION_STORAGE
            )
        ) {
            showLoader()
            viewModelProfile.getStateLife()

        } else {
            showLoader()
            viewModelProfile.getStateLife()
        }

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    PERMISSION_STORAGE -> {
                        showLoader()
                        viewModelProfile.getStateLife()
                    }
                }
            }
            else -> {
                toast("Es necesario otorgar el permiso")
                activity?.onBackPressed()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (UtilValidPermission().allPermissionsAreAgree(grantResults)) {
            when (requestCode) {
                PERMISSION_STORAGE -> {
                    showLoader()
                    viewModelProfile.getStateLife()
                }
                PERMISSION_LOCATION ->{

                }
            }
        }else{
//            initPermission()
            when (requestCode) {
                PERMISSION_LOCATION ->{
                    UtilAlert.Builder()
                        .setTitle(getString(R.string.title_dialog_warning))
                        .setMessage("Debes otorgar el permiso de ubicación para poder continuar")
                        .setTextButtonOk("Ir a la configuración")
                        .setListener {
                            startActivity(
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    .setData(Uri.parse("package:" + context?.packageName))
                            )
                        }
                        .build()
                        .show(childFragmentManager, "")
                }
            }

        }
    }

    private fun initButtons() {
        binding.etSearchChurch.setOnClickListener {
            if (UtilValidPermission().validListPermissionsAndBuildRequest(
                    this@EAMXProfileInfoFragment, arrayListOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), PERMISSION_LOCATION
                )
            ){
                    ChurchesMapFragment(false) { church ->
                        this.addSearchChurch(
                            ChurchAndDescriptionModel(
                                church = church,
                                activity = ActivityChurchModel()
                            )
                        )
                    }.show(childFragmentManager, TAG_LOADER)
                }


        }
        binding.etSearchCommunity.setOnClickListener {
            ChurchesMapFragment(true) { church ->
                viewModelProfile.congregationItem = null
                idComunnity = church.id
                if (idComunnity == 0) {
                    etSearchCommunity.setText("Registro pendiente")
                } else {
                    etSearchCommunity.setText(church.name)
                }
            }.show(childFragmentManager, TAG_LOADER)
        }
        binding.etSearchCongregations.setOnClickListener {

            activity?.supportFragmentManager?.let {
                val dialog = CongregationDialogFragment.newInstance()
                dialog.congregationSelected = { item ->
                    binding.etSearchCongregations.setText(item.description)
                    viewModelProfile.congregationItem = item
                }
                dialog.show(it, TAG_LOADER)
            }


        }

        binding.etSearchChurchDiaco.setOnClickListener {
            ChurchesMapFragment { church ->
                this.addDiaconoChurch(church)
            }.show(childFragmentManager, TAG_LOADER)
        }

        binding.btnSave.setOnClickListener {
            viewModelProfile.updateUser(
                binding,
                activitiesAdapter,
                idComunnity,
                diaconoChurchList,
                churchList,
            ) {
                if (viewModelProfile.executeUpdateProfile(binding.spStyleLife.selectedItem)) {
                    showLoader()
                } else {
                    viewModelProfile.saveData(binding, activitiesAdapter)
                    NavigationFragment.Builder()
                        .setActivity(requireActivity())
                        .setView(requireView().parent as ViewGroup)
                        .setFragment(EAMXPriestRegisterFragment.newInstance(callBack))
                        .build().nextWithReplace()
                }
            }
        }
    }

    fun setView(typeUser: String, cleanData: Boolean, uiEnabled: Boolean, code: String) {

        loadPrefix(code)

        if (!uiEnabled) {
            return
        }
        when (typeUser) {
            SELECT_ITEM, PRIEST -> {
                binding.llDiacono.visibility = View.GONE
                binding.llSearchChurch.visibility = View.GONE
                binding.llSearchCongragations.visibility = View.GONE
                binding.llResponsibleCommunity.visibility = View.GONE
                if (typeUser == PRIEST) {
                    binding.btnSave.text = "Siguiente"
                }
            }
            SINGLE, MARRIED, WIDOWER -> {
                if (cleanData) {
                    binding.etSearchChurch.setText("")
                    binding.rbNo.isChecked = true
                    diaconoChurchAdapter.clear()
                }
                binding.llDiacono.visibility = View.GONE
                binding.llSearchChurch.visibility = View.VISIBLE
                binding.llSearchCongragations.visibility = View.GONE
                binding.rvChurch.visibility = View.GONE
                binding.llResponsibleCommunity.visibility = View.GONE
            }
            LAIC -> {
                binding.apply {
                    if (cleanData) {
                        binding.etSearchChurch.setText("")
                        binding.rbNo.isChecked = true
                        diaconoChurchAdapter.clear()
                    }
                    binding.llDiacono.visibility = View.GONE
                    binding.llSearchChurch.visibility = View.VISIBLE
                    binding.llSearchCongragations.visibility = View.GONE
                    binding.rvChurch.visibility = View.GONE
                    binding.llResponsibleCommunity.visibility = View.GONE
                    binding.tvMessageSearchChurch.visibility = View.GONE
                }

            }
            LAIC_CONSECRATED, RELIGIOUS -> {
                binding.apply {
                    if (cleanData) {
                        binding.etSearchCongregations.setText("")
                        viewModelProfile.congregationItem = null
                        viewModelProfile.congregationItem = null
                        swResponsibleCommunity.isChecked = true
                        etSearchCommunity.setText("")
                    }
                    llDiacono.visibility = View.GONE
                    llSearchChurch.visibility = View.GONE
                    llSearchCongragations.visibility = View.VISIBLE
                    llResponsibleCommunity.visibility = View.VISIBLE
                }

            }
            DIACO -> {
                if (cleanData) {
                    churchAdapter.clear()
                }
                binding.llSearchChurch.visibility = View.GONE
                binding.llSearchCongragations.visibility = View.GONE
                binding.llDiacono.visibility = View.VISIBLE
                binding.rvDiacono.visibility = View.GONE
                binding.llResponsibleCommunity.visibility = View.GONE
            }
            PRIEST -> {
                churchAdapter.clear()

                binding.etSearchCongregations.setText("")
                viewModelProfile.congregationItem = null

                binding.etSearchChurch.setText("")
                binding.rbNo.isChecked = true
                diaconoChurchAdapter.clear()

                binding.llSearchChurch.visibility = View.GONE
                binding.llSearchCongragations.visibility = View.GONE
                binding.llDiacono.visibility = View.GONE
                binding.rvDiacono.visibility = View.GONE
                binding.llResponsibleCommunity.visibility = View.GONE
            }
        }
    }

    private fun addSearchChurch(church: ChurchAndDescriptionModel) {
        binding.rvChurch.visibility = View.VISIBLE
        if(churchAdapter.itemCount==0) {
            if (!churchAdapter.existId(church.church.id)) {
                viewModelProfile.setUpdateChurchInModuleMyChurch()
                churchAdapter.addItem(church, viewModelProfile.userNeedCompletedProfile())
                binding.etSearchChurch.visibility = View.INVISIBLE
            }
        }
    }

    private fun addDiaconoChurch(church: ChurchModel) {
        binding.rvDiacono.visibility = View.VISIBLE
        if (!diaconoChurchAdapter.existId(church.id)) {
            diaconoChurchAdapter.addItem(church, viewModelProfile.userNeedCompletedProfile())
        }
    }

    private fun initAdapterTopics() {
        activitiesAdapter = ActivitiesAdapter(activitiesList, binding.rvThemesInterest)
        binding.rvThemesInterest.apply {
            layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            adapter = activitiesAdapter
        }
    }

    private fun initAdapterDiaconoChurch() {
        diaconoChurchAdapter = DiaconoChurchAdapter(diaconoChurchList, binding.rvDiacono)
        binding.rvDiacono.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvDiacono.adapter = diaconoChurchAdapter
    }

    private fun initAdapterChurch(list: List<DataWithDescription>) {
        churchAdapter = ChurchAdapter(churchList, binding.rvChurch, list){
            binding.etSearchChurch.visibility = View.VISIBLE
        }
        binding.rvChurch.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvChurch.adapter = churchAdapter
    }

    private fun enabledInputs(isFirstTime: Boolean) {
        binding.apply {
            etName.isEnabled = isFirstTime
            etFatherSurname.isEnabled = isFirstTime
            etMotherSurname.isEnabled = isFirstTime
            spTopics.isEnabled = isFirstTime
            spStyleLife.isEnabled = isFirstTime
            rvThemesInterest.isEnabled = isFirstTime
            rbNo.isEnabled = isFirstTime
            rbYes.isEnabled = isFirstTime
            etSearchChurch.isEnabled = isFirstTime
            etSearchChurchDiaco.isEnabled = isFirstTime
            etSearchCongregations.isEnabled = isFirstTime
            etSearchCommunity.isEnabled = isFirstTime
            etPastoralActivity.isEnabled = isFirstTime
            spServicesChurch.isEnabled = isFirstTime
            btnSave.isEnabled = isFirstTime
            swResponsibleCommunity.isEnabled = isFirstTime
            spPrefix.isEnabled = isFirstTime

            if (!isFirstTime) {
                llDiacono.visibility = View.GONE
                llSearchChurch.visibility = View.GONE
                llSearchCongragations.visibility = View.GONE
                etSearchCongregations.visibility = View.GONE
            }

        }
    }

    private fun loadPrefix(lifestate: String) {
        spPrefix?.adapter = null
        spPrefix.visibility = View.GONE
        tvPrefix.visibility = View.GONE

        if (lifestate != "LAIC")
            viewModelProfile.getPrefix(lifestate)
    }
}