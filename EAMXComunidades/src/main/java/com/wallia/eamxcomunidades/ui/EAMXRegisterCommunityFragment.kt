package com.wallia.eamxcomunidades.ui

import android.Manifest
import android.content.Intent
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Patterns
import android.view.*
import android.widget.*
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wallia.eamxcomunidades.R
import com.wallia.eamxcomunidades.adapter.CommunityLinkedAdapter
import com.wallia.eamxcomunidades.adapter.ServiceActivityAdapter
import com.wallia.eamxcomunidades.adapter.SocialMediaAdapter
import com.wallia.eamxcomunidades.config.Constants
import com.wallia.eamxcomunidades.database.instance.MeetRoomDataBaseCommunity
import com.wallia.eamxcomunidades.databinding.*
import com.wallia.eamxcomunidades.repository.Repository
import com.wallia.eamxcomunidades.viewmodel.ComunidadesViewModel
import com.wallia.eamxcomunidades.model.*
import com.wallia.eamxcomunidades.model.church.Day
import com.wallia.eamxcomunidades.utils.CustomLinearLayoutManager
import com.wallia.eamxcomunidades.utils.PublicFunctions
import mx.arquidiocesis.eamxcommonutils.util.loadByUrl
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.*
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.managerpictures.viewmodel.ManagerUploadImageViewModel
import mx.arquidiocesis.eamxcommonutils.managerpictures.viewmodel.ManagerUploadImageViewModel.Companion.CAMERA_CODE
import mx.arquidiocesis.eamxcommonutils.managerpictures.viewmodel.ManagerUploadImageViewModel.Companion.GALLERY_CODE
import mx.arquidiocesis.eamxcommonutils.managerpictures.viewmodel.ManagerUploadImageViewModel.Companion.PERMISSION_CAMERA
import mx.arquidiocesis.eamxcommonutils.managerpictures.viewmodel.ManagerUploadImageViewModel.Companion.PERMISSION_GALLERY
import mx.arquidiocesis.eamxcommonutils.managerpictures.repository.RepositoryUploadImage
import mx.arquidiocesis.eamxcommonutils.managerpictures.view.managerResultActivity
import mx.arquidiocesis.eamxcommonutils.managerpictures.view.openWindowToChooseUploadImage
import mx.arquidiocesis.eamxcommonutils.managerpictures.viewmodel.ManagerUploadImageViewModel.Companion.SOURCE_COMMUNITY
import mx.arquidiocesis.eamxcommonutils.model.ReviewModel
import mx.arquidiocesis.eamxcommonutils.util.*
import mx.arquidiocesis.eamxcommonutils.util.Adapter.ReviewAdapter
import mx.arquidiocesis.eamxcommonutils.util.Repository.ReviewRepository
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxcommonutils.util.permission.UtilValidPermission
import mx.arquidiocesis.eamxcommonutils.util.review.ReviewFragment
import mx.arquidiocesis.eamxcommonutils.util.viewModel.ReviewViewModel
import mx.arquidiocesis.eamxmaps.PublicMaps
import mx.arquidiocesis.eamxprofilemodule.ui.admin.AdminModulesFragment
import java.io.IOException

class EAMXRegisterCommunityFragment : FragmentBase() {

    private var map = MutableLiveData<GoogleMap>()
    private var maker: MutableLiveData<Marker>? = MutableLiveData<Marker>()
    private var publicMaps = maker?.let {
        PublicMaps(map, it)
    }
    private var client = MutableLiveData<GoogleApiClient>()
    private var location: MutableLiveData<Location>? = MutableLiveData<Location>()
    private var LOCATIONP = 100
    private var ubicacionInicial = true
    private var status = ""
    private var idComunity = 0
    private var firtsTime = true
    var latitude = 0.0
    var longitude = 0.0
    var star = 0f
    var nombre = ""

    private lateinit var mapFragment: SupportMapFragment

    private val serviceActivityList: MutableList<ServiceActivityModel> by lazy {
        mutableListOf()
    }

    private val socialMediaList: MutableList<SocialMediaModel> by lazy {
        mutableListOf()
    }

    private val communityLinkedList: MutableList<CommunityLinkedModel> by lazy {
        mutableListOf()
    }

    private val socialMediaAdapter: SocialMediaAdapter by lazy {
        SocialMediaAdapter(socialMediaList, bindingCommunity.rvSocialMedia)
    }

    private val serviceActivityAdapter: ServiceActivityAdapter by lazy {
        ServiceActivityAdapter(serviceActivityList, bindingCommunity.rvServiceActivity)
    }

    private val communityLinkedAdapter: CommunityLinkedAdapter by lazy {
        CommunityLinkedAdapter(communityLinkedList, bindingCommunity.rvServiceActivity)
    }

    private val dataSourceCommunitylist: MutableList<String> by lazy {
        mutableListOf()
    }

    private val viewModel: ComunidadesViewModel by lazy {
        ComunidadesViewModel(
            Repository(
                requireContext(),
                database = MeetRoomDataBaseCommunity.getRoomInstance(requireContext())
            )
        )
    }

    private val viewModelImageManager: ManagerUploadImageViewModel by lazy {
        getViewModel {
            ManagerUploadImageViewModel(
                requireContext(),
                RepositoryUploadImage(requireContext())
            )
        }
    }

    private val reviewViewModel: ReviewViewModel by lazy {
        getViewModel {
            ReviewViewModel(ReviewRepository(requireContext()))
        }
    }

    private val dialogSocialMedia: BottomSheetDialog by lazy {
        val dialog = BottomSheetDialog(requireContext())
        bindingBottomSheetSocialNetwork = BottomSheetSocialNetworkBinding.inflate(layoutInflater)

        bindingBottomSheetSocialNetwork.apply {
            spSocialNetwork.apply {
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long,
                    ) {
                        if (position > 0) {
                            teUserSocial.isEnabled = true
                            teUserSocial.requestFocus()
                        } else {
                            teUserSocial.isEnabled = false
                        }
                    }
                }
            }

            ivAddSocialMedia.setOnClickListener {
                if (addSocialMedia(bindingBottomSheetSocialNetwork)) {
                    bindingBottomSheetSocialNetwork.teUserSocial.text?.clear()
                }
            }

            tvAddSocialMedia.setOnClickListener {
                if (addSocialMedia(bindingBottomSheetSocialNetwork)) {
                    bindingBottomSheetSocialNetwork.teUserSocial.text?.clear()
                }
            }

            btnReady.setOnClickListener {
                if (addSocialMedia(bindingBottomSheetSocialNetwork)) {
                    bindingBottomSheetSocialNetwork.teUserSocial.text?.clear()

                    dialog.dismiss()
                }
            }
        }

        dialog.setContentView(bindingBottomSheetSocialNetwork.root)
        dialog
    }

    private val dialogServiceActivity: BottomSheetDialog by lazy {
        val dayGeneralSelected = MutableLiveData<MutableList<Day>>()
        val hourGeneralSelected = MutableLiveData<String>()
        val schedule = ScheduleXXXXX()
        val dialog = BottomSheetDialog(requireContext())
        bindingBottomSheetService = BottomSheetServicesBinding.inflate(layoutInflater)
        bindingBottomSheetService.apply {
            teNameServiceActivity.requestFocus()
            ivScheduleDays.setOnClickListener {
                tvScheduleDays.text = ""
                dayGeneralSelected.value = PublicFunctions().getDays()
                PublicFunctions().selectDayRange(requireContext(), dayGeneralSelected)
            }
            ivScheduleHour.setOnClickListener {
                tvScheduleHour.text = ""
                PublicFunctions().selectFirstHour(requireContext(), hourGeneralSelected)
            }
            ivAddService.setOnClickListener {
                if (addServices(
                        bindingBottomSheetService,
                        dayGeneralSelected.value ?: mutableListOf(),
                        schedule
                    )
                ) {
                    cleanFormService(bindingBottomSheetService)
                }
            }
            tvAddService.setOnClickListener {
                if (addServices(
                        bindingBottomSheetService,
                        dayGeneralSelected.value ?: mutableListOf(),
                        schedule
                    )
                ) {
                    cleanFormService(bindingBottomSheetService)
                }
            }
            btnReady.setOnClickListener {
                if (addServices(
                        bindingBottomSheetService,
                        dayGeneralSelected.value ?: mutableListOf(),
                        schedule
                    )
                ) {
                    cleanFormService(bindingBottomSheetService)
                    dialog.dismiss()
                }
            }

            dayGeneralSelected.observe(viewLifecycleOwner) {
                val daysStr = PublicFunctions().obtenerDias(it)
                val dayList = daysStr.split(" a ")

                tvScheduleDays.text = if (dayList.size > 1 && dayList[0] == dayList[1]) {
                    dayList[0]
                } else {
                    PublicFunctions().obtenerDias(it)
                }
            }

            hourGeneralSelected.observe(viewLifecycleOwner) { result ->
                result.let { hour ->
                    tvScheduleHour.text = when (tvScheduleHour.text.toString().isEmpty()) {
                        true -> {
                            val leftzero = if (hour.trim().length < 5) {
                                "0"
                            } else {
                                ""
                            }
                            schedule.startHour = leftzero + hour.trim()
                            PublicFunctions().selectFirstHour(requireContext(), hourGeneralSelected)
                            hour
                        }
                        false -> {
                            val leftzero = if (hour.trim().length < 5) {
                                "0"
                            } else {
                                ""
                            }
                            schedule.endHour = leftzero + hour.trim()
                            if (schedule.startHourMinusEndHour()) {
                                "${tvScheduleHour.text} -$hour"
                            } else {
                                toast(getString(R.string.message_schedule_invalid))
                                schedule.startHour = ""
                                schedule.endHour = ""
                                ""
                            }
                        }
                    }
                }
            }
        }
        dialog.setContentView(bindingBottomSheetService.root)
        dialog
    }

    private val dialogEditData: BottomSheetDialog by lazy {
        val daySelected = MutableLiveData<MutableList<Day>>()
        val daySelectedOther = MutableLiveData<MutableList<Day>>()
        val hourSelected = MutableLiveData<String>()
        val hourSelectedOther = MutableLiveData<String>()
        val schedule = ScheduleXXXXX()
        val scheduleOther = ScheduleXXXXX()
        val dialog = BottomSheetDialog(requireContext())
        bindingBottomSheetEdit = BottomSheetEditCommunityBinding.inflate(layoutInflater)
        bindingBottomSheetEdit.apply {
            itemDetail = bindingCommunity.itemDetail
            mapFragment =
                childFragmentManager.findFragmentById(R.id.mapDetail) as SupportMapFragment
            publicMaps?.buildGoogleApiClient(client, requireContext())
            mapFragment.getMapAsync(publicMaps)

            val groupByDays = itemDetail
                ?.serviceHoursGeneral
                ?.groupBy { item -> item.day }
                ?.toList()

            var hourStart: String? = null
            var hourEnd: String? = null

            if (groupByDays?.isNotEmpty() == true) {
                hourStart = groupByDays.first().second.first().schedules?.first()?.hourStart
                hourEnd = groupByDays.first().second.first().schedules?.first()?.hourEnd
            }

            val datesFirst = mutableListOf<ServiceHour>()
            val datesSecond = mutableListOf<ServiceHour>()

            groupByDays?.forEach { listServices ->
                listServices.second.forEach { serviceHour ->
                    val existData =
                        serviceHour.schedules?.firstOrNull { itemSchedule -> itemSchedule.hourStart == hourStart && itemSchedule.hourEnd == hourEnd }
                    existData?.let {
                        datesFirst.add(serviceHour)
                    } ?: kotlin.run {
                        datesSecond.add(serviceHour)
                    }
                }
            }

            hourSelected.value = "$hourStart - $hourEnd"
            daySelected.value = datesFirst.map { itemDay ->
                Day(
                    checked = true,
                    id = itemDay.day ?: 0,
                    name = (itemDay.day ?: 0).numberToDayNameCompleted()
                )
            }.toMutableList()

            if (datesSecond.isNotEmpty())
                hourSelectedOther.value =
                    "${datesSecond.first().schedules?.first()?.hourStart ?: ""} - ${datesSecond.first().schedules?.first()?.hourEnd ?: ""}"

            daySelectedOther.value = datesSecond.map { itemDay ->
                Day(
                    checked = true,
                    id = itemDay.day ?: 0,
                    name = (itemDay.day ?: 0).numberToDayNameCompleted()
                )
            }.toMutableList()

            ivDays.setOnClickListener {
                tvScheduleDays.text = ""
                val previousDaysSelected =
                    itemDetail?.serviceHoursFirst?.groupBy { item -> item.day }?.map {
                        it.key
                    }?.toList()

                val daysSelected =
                    itemDetail?.serviceHoursFirst?.groupBy { item -> item.day }?.map {
                        it.key
                    }?.toList()

                val daysToShow = PublicFunctions().getDays(previousDaysSelected, true)

                for (day in daysToShow) {
                    if (daysSelected?.firstOrNull { itemSelected -> itemSelected == day.id } != null) {
                        day.checked = true
                    }
                }
                daySelected.value = daysToShow
                PublicFunctions().selectDayRange(requireContext(), daySelected, false)
            }

            ivDaysOther.setOnClickListener {
                tvScheduleDaysOther.text = ""
                val previousDaysSelected =
                    itemDetail?.serviceHoursSecond?.groupBy { item -> item.day }?.map {
                        it.key
                    }?.toList()

                val daysSelected =
                    itemDetail?.serviceHoursSecond?.groupBy { item -> item.day }?.map {
                        it.key
                    }?.toList()

                val daysToShow = PublicFunctions().getDays(previousDaysSelected, true)

                for (day in daysToShow) {
                    if (daysSelected?.firstOrNull { itemSelected -> itemSelected == day.id } != null) {
                        day.checked = true
                    }
                }

                daySelectedOther.value = daysToShow
                PublicFunctions().selectDayRange(requireContext(), daySelectedOther, false)
            }

            ivHours.setOnClickListener {
                tvScheduleHour.text = ""
                PublicFunctions().selectFirstHour(requireContext(), hourSelected)
            }

            ivHoursOther.setOnClickListener {
                tvScheduleHourOther.text = ""
                PublicFunctions().selectFirstHour(requireContext(), hourSelectedOther)
            }

            btnReady.setOnClickListener {
                if (validEditCommunityGeneral(bindingBottomSheetEdit)) {
                    bindingBottomSheetEdit.apply {
                        itemDetail?.let {
                            it.charisma = etCharisma.text.toString()
                            it.leader = etNameAssigned.text.toString()
                            it.description = etDescription.text.toString()
                            it.email = etEmail.text.toString()
                            it.phone = etPhone.text.toString()
                            it.address = etAddress.query.toString()
                            it.latitude = maker?.value?.position?.latitude
                            it.longitude = maker?.value?.position?.longitude
                        }
                    }
                    updateData(itemDetail, daySelected, schedule, daySelectedOther, scheduleOther)
                    dialog.dismiss()
                }
            }

            if (itemDetail != null) {
                if (!itemDetail!!.address.isNullOrEmpty()) {
                    etAddress.setQuery(itemDetail!!.address, true)
                }
                if (itemDetail!!.latitude != null && itemDetail!!.longitude != null) {
                    if (itemDetail!!.latitude != 0.0 && itemDetail!!.longitude != 0.0) {
                        publicMaps?.addMaker(
                            itemDetail!!.latitude!!,
                            itemDetail!!.longitude!!
                        )
                    } else {
                        if (UtilValidPermission().validListPermissionsAndBuildRequest(
                                this@EAMXRegisterCommunityFragment,
                                arrayListOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ), LOCATIONP
                            )
                        ) {
                            viewModel.getLocation()
                            showLoader()
                            ubicacionInicial = true
                        }
                    }
                }
            }

            searchLocation(etAddress.query.toString())

            etAddress.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (etAddress.query.isNotEmpty()) {
                        searchLocation(etAddress.query.toString())
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                    }
                    return false
                }
            })

            daySelected.observe(viewLifecycleOwner) {
                it?.let { days ->
                    itemDetail?.serviceHoursFirst =
                        days.filter { filter -> filter.checked }.map { dayItem ->
                            ServiceHour(
                                day = dayItem.id,
                                schedules = listOf(
                                    ScheduleX(
                                        hourStart = if (schedule.startHour?.length!! < 5) "0${schedule.startHour}" else schedule.startHour,
                                        hourEnd = if (schedule.endHour?.length!! < 5) "0${schedule.endHour}" else schedule.endHour,
                                    )
                                )
                            )
                        }.toList()
                    tvScheduleDays.text = PublicFunctions().obtenerDias(days)
                }
            }

            hourSelected.observe(viewLifecycleOwner) { result ->
                result.let { hour ->
                    if (!hour.contains("null")) {
                        tvScheduleHour.text = when (tvScheduleHour.text.toString().isEmpty()) {
                            true -> {
                                if (result.length == 13) {
                                    schedule.startHour = result.split(" - ")[0]
                                    schedule.endHour = result.split(" - ")[1]
                                    result
                                } else {
                                    schedule.startHour = hour.trim()
                                    PublicFunctions().selectFirstHour(requireContext(),
                                        hourSelected)
                                    hour
                                }
                            }
                            false -> {
                                schedule.endHour = hour.trim()
                                if (schedule.startHourMinusEndHour()) {
                                    "${tvScheduleHour.text} -$hour"
                                } else {
                                    toast(getString(R.string.message_schedule_invalid))
                                    schedule.startHour = ""
                                    schedule.endHour = ""
                                    ""
                                }
                            }
                        }
                    }
                }
            }

            daySelectedOther.observe(viewLifecycleOwner) {
                it?.let { days ->
                    itemDetail?.serviceHoursSecond =
                        days.filter { filter -> filter.checked }.map { dayItem ->
                            ServiceHour(
                                day = dayItem.id,
                                schedules = listOf(
                                    ScheduleX(
                                        hourStart = if (schedule.startHour?.length!! < 5) "0${schedule.startHour}" else schedule.startHour,
                                        hourEnd = if (schedule.endHour?.length!! < 5) "0${schedule.endHour}" else schedule.endHour,
                                    )
                                )
                            )
                        }.toList()
                    tvScheduleDaysOther.text = PublicFunctions().obtenerDias(days)
                }
            }

            hourSelectedOther.observe(viewLifecycleOwner) { result ->
                result.let { hour ->
                    tvScheduleHourOther.text =
                        when (tvScheduleHourOther.text.toString().isEmpty()) {
                            true -> {
                                if (result.length == 13) {
                                    scheduleOther.startHour = result.split(" - ")[0]
                                    scheduleOther.endHour = result.split(" - ")[1]
                                    result
                                } else {
                                    scheduleOther.startHour = hour.trim()
                                    PublicFunctions().selectFirstHour(
                                        requireContext(),
                                        hourSelectedOther
                                    )
                                    hour
                                }
                            }
                            false -> {
                                scheduleOther.endHour = hour.trim()
                                if (scheduleOther.startHourMinusEndHour()) {
                                    "${tvScheduleHourOther.text} -$hour"
                                } else {
                                    toast(getString(R.string.message_schedule_invalid))
                                    scheduleOther.startHour = ""
                                    scheduleOther.endHour = ""
                                    ""
                                }
                            }
                        }
                }
            }

            dialog.setContentView(root)
        }
        dialog
    }

    private fun validEditCommunityGeneral(bindingBottomSheetEdit: BottomSheetEditCommunityBinding): Boolean {
        bindingBottomSheetEdit.apply {

            if (etDescription.text.toString().isEmpty()) {
                etDescription.error = getString(R.string.message_error_description)
            }

            if (etCharisma.text.toString().isEmpty()) {
                etCharisma.error = getString(R.string.message_error_charisma)
            }

            if (etEmail.text.toString().isEmpty()) {
                etEmail.error = getString(R.string.message_error_email)
            }

            if (Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches().not()) {
                etEmail.error = getString(R.string.message_error_email_invalid)
            }

            if (etPhone.text.toString().isEmpty()) {
                etPhone.error = getString(R.string.message_error_phone)
            }

            if (etPhone.text.toString().length != 10) {
                etPhone.error = getString(R.string.message_error_phone_invalid)
            }

            if (tvScheduleDays.text.toString() == getString(R.string.hint_scheduler_days) ||
                tvScheduleDays.text.toString() == getString(R.string.hint_scheduler_hour_space) ||
                tvScheduleDays.text.toString().isEmpty()
            ) {
                toast(getString(R.string.message_days_missed))
                return false
            }

            if (tvScheduleHour.text.toString() == getString(R.string.hint_scheduler_hour) ||
                tvScheduleHour.text.toString() == getString(R.string.hint_scheduler_hour_space) ||
                tvScheduleHour.text.toString().isEmpty()
            ) {
                toast(getString(R.string.message_schedule_missed))
                return false
            }

            if (tvScheduleHour.text.toString().length < 3) {
                toast(getString(R.string.message_schedule_missed))
                return false
            }

            if (tvScheduleDaysOther.text.toString().length < 5 && tvScheduleHourOther.text.toString().length < 3) {
                toast(getString(R.string.message_schedule_other_missed))
                return false
            }

            if (etPhone.error != null ||
                etEmail.error != null ||
                etCharisma.error != null ||
                etDescription.error != null
            ) {
                return false
            }

            etPhone.error = null
            etEmail.error = null
            etCharisma.error = null
            etDescription.error = null
        }
        return true
    }

    private val dialogCommunityLinked: BottomSheetDialog by lazy {
        val dialog = BottomSheetDialog(requireContext())
        showLoader()
        viewModel.getCommunitiesSearchByName("")
        bindingBottomSheetCommunity = BottomSheetOtherCommunityBinding.inflate(layoutInflater)
        bindingBottomSheetCommunity.apply {
            acTestSuggestion.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    acTestSuggestion.tag = acTestSuggestion.adapter.getItem(position)
                }

            ivAddOther.setOnClickListener {
                if (addCommunityLinked(bindingBottomSheetCommunity)) {
                    cleanFormCommunity(bindingBottomSheetCommunity)
                }
            }
            tvAddOther.setOnClickListener {
                if (addCommunityLinked(bindingBottomSheetCommunity)) {
                    cleanFormCommunity(bindingBottomSheetCommunity)
                }
            }
            btnReady.setOnClickListener {
                if (addCommunityLinked(bindingBottomSheetCommunity)) {
                    cleanFormCommunity(bindingBottomSheetCommunity)
                    dialog.dismiss()
                }
            }
        }
        dialog.setContentView(bindingBottomSheetCommunity.root)
        dialog
    }

    private lateinit var bindingBottomSheetSocialNetwork: BottomSheetSocialNetworkBinding
    private lateinit var bindingBottomSheetCommunity: BottomSheetOtherCommunityBinding
    private lateinit var bindingBottomSheetEdit: BottomSheetEditCommunityBinding
    private lateinit var bindingBottomSheetService: BottomSheetServicesBinding
    private lateinit var bindingCommunity: FragmentEamxRegisterCommunityBinding

    companion object {
        @JvmStatic
        fun newInstance(
            status: String,
            idComunity: Int = 0,
        ): EAMXRegisterCommunityFragment {
            return EAMXRegisterCommunityFragment().apply {
                this.status = status
                this.idComunity = idComunity
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingCommunity = FragmentEamxRegisterCommunityBinding.inflate(inflater, container, false)
        return bindingCommunity.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as EAMXHome).showToolbar(true, AppMyConstants.editarComunidad)
        initRecyclerViews()
        initOnClick()
        if (firtsTime) {
            initData()
            firtsTime = false
        }

        initObservers()
    }

    private fun initRecyclerViews() {
        bindingCommunity.apply {
            rvSocialMedia.layoutManager =
                CustomLinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvSocialMedia.adapter = socialMediaAdapter

            rvServiceActivity.layoutManager =
                CustomLinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvServiceActivity.adapter = serviceActivityAdapter

            rvCommunityLinked.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvCommunityLinked.adapter = communityLinkedAdapter
        }
    }

    private fun initOnClick() {
        bindingCommunity.apply {
            ivEdit.setOnClickListener { openCommunityEditor() }
            ivAddSocialNetwork.setOnClickListener { openSocialNetwork() }
            cvEmptySocialNetwork.setOnClickListener { openSocialNetwork() }
            tvAddSocialNetwork.setOnClickListener { openSocialNetwork() }
            ivAddServices.setOnClickListener { openServices() }
            tvAddServices.setOnClickListener { openServices() }
            cvServiceActivity.setOnClickListener { openServices() }
            ivCommunity.setOnClickListener { openCommunity() }
            tvCommunity.setOnClickListener { openCommunity() }
            ivCall.setOnClickListener { openCall() }
            ivEmail.setOnClickListener { openEmail() }
            btnRideME.setOnClickListener { openRideMe() }
            tvCommunity.setOnClickListener { openCommunity() }
            ivCamera.setOnClickListener { openChooseSourceImageCommunity() }
            btnCancel.setOnClickListener { cancelEditCommunity() }
            btnAdd.setOnClickListener { saveData() }
            btnComment.setOnClickListener { changeFragment() }
        }
    }

    private fun initData() {
        showLoader()
        if (idComunity == 0) {
            viewModel.getActivities()
            viewModel.getCommunityDetail()
        } else {
            viewModel.getActivities(idComunity)
            viewModel.getCommunityDetail(idComunity)
        }

    }

    private fun initObservers() {
        viewModel.getCommunityDetailResponse.observe(viewLifecycleOwner, handleCommunityDetail())
        viewModel.getPartnerCommunitiesResponse.observe(
            viewLifecycleOwner,
            handlePartherCommunities()
        )
        viewModel.getActivitiesResponse.observe(viewLifecycleOwner, handleActivities())
        viewModel.getCommunitiesSearchByNameResponse.observe(
            viewLifecycleOwner,
            handleCommunityByName()
        )
        viewModelImageManager.responseUploadImage.observe(viewLifecycleOwner, handleUploadImage())
        viewModelImageManager.responseError.observe(viewLifecycleOwner, handleErrorUploadImage())
        viewModel.completeCommunityResponse.observe(viewLifecycleOwner, handlerCommunity())
        viewModel.locationResponse.observe(viewLifecycleOwner, handleLocation())
        viewModel.errorResponse.observe(viewLifecycleOwner, handleError())
        client.observe(viewLifecycleOwner) {
            it.connect()
        }
        map.observe(viewLifecycleOwner) {
            it.setOnMarkerClickListener(publicMaps)
            if (bindingCommunity.itemDetail?.latitude != null && bindingCommunity.itemDetail?.longitude != null) {
                publicMaps?.addMaker(
                    bindingCommunity.itemDetail?.latitude ?: 0.0,
                    bindingCommunity.itemDetail?.longitude ?: 0.0
                )
            } else {
                location?.let { locationSafe ->
                    locationSafe.value?.let { itemLocation ->
                        publicMaps?.addMaker(itemLocation.latitude, itemLocation.longitude)
                    }
                }
            }
            it.setOnMapClickListener {
                publicMaps?.moveMarker(it.latitude, it.longitude)
            }
            hideLoader()
        }
        maker?.observe(viewLifecycleOwner) {

        }

        bindingCommunity.apply {
            socialMediaAdapter.isFinish.observe(viewLifecycleOwner) {
                if (socialMediaAdapter.socialMediaList.isEmpty()) {
                    if (cvEmptySocialNetwork.visibility != View.VISIBLE) {
                        tvAddSocialNetwork.visibility = View.GONE
                        ivAddSocialNetwork.visibility = View.GONE
                        cvSocialNetwork.visibility = View.GONE

                        cvEmptySocialNetwork.visibility = View.VISIBLE
                    }
                } else {
                    if (cvEmptySocialNetwork.visibility != View.GONE) {
                        tvAddSocialNetwork.visibility = View.VISIBLE
                        ivAddSocialNetwork.visibility = View.VISIBLE
                        cvSocialNetwork.visibility = View.VISIBLE

                        cvEmptySocialNetwork.visibility = View.GONE
                    }
                }
            }
        }

        bindingCommunity.apply {
            serviceActivityAdapter.isFinishServices.observe(viewLifecycleOwner) {
                if (serviceActivityAdapter.socialMediaList.isEmpty()) {
                    if (cvServiceActivity.visibility != View.VISIBLE) {
                        tvAddServices.visibility = View.GONE
                        ivAddServices.visibility = View.GONE

                        cvServiceActivity.visibility = View.VISIBLE
                    }
                } else {
                    if (cvServiceActivity.visibility != View.GONE) {
                        tvAddServices.visibility = View.VISIBLE
                        ivAddServices.visibility = View.VISIBLE

                        cvServiceActivity.visibility = View.GONE
                    }
                }
            }
        }

        reviewViewModel.opinionListResponse.observe(viewLifecycleOwner) { item ->
            hideLoader()
            var list: MutableList<ReviewModel> = mutableListOf()
            if (item.my_review != null) {
                list.add(item.my_review!!)
                if (!item.other_reviews.isNullOrEmpty()) {
                    item.other_reviews!!.forEach {
                        list.add(it)
                    }
                }
            } else if (!item.other_reviews.isNullOrEmpty()) {
                list = item.other_reviews as MutableList<ReviewModel>
            }

            bindingCommunity.apply {
                tvNumComnetarios.text = "${list.size} comentarios"
                rvComments.apply {
                    layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    adapter = ReviewAdapter(
                        requireContext(),
                        list,
                        rvComments,
                        false
                    ) { item, Etiqueta ->

                    }
                }
            }

        }

    }

    fun searchLocation(location: String) {
        var addressList: List<Address>? = null
        if (location == null || location == "") {
            Toast.makeText(requireContext(), "provide location", Toast.LENGTH_SHORT).show()
        } else {
            val geoCoder = Geocoder(requireContext())
            try {
                addressList = geoCoder.getFromLocationName(location, 1)

            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (!addressList?.isEmpty()!!) {
                val address = addressList!![0]
                latitude = address.latitude
                longitude = address.longitude
                publicMaps?.moveMarker(address.latitude, address.longitude)
            } else {
                UtilAlert
                    .Builder()
                    .setTitle("Aviso")
                    .setMessage("Dirección no valida")
                    .build()
                    .show(childFragmentManager, "")
            }

        }
    }

    private fun handlerCommunity(): (Boolean) -> Unit = {
        hideLoader()

        if (status == Constants.PENDING_COMPLETION) {
            UtilAlert.Builder()
                .setTitleCustom(buildTextSuccess())
                .setMessage(getString(R.string.label_instruction_community))
                .setTextButtonCancel(getString(R.string.txt_more_late))
                .setTextButtonOk(getString(R.string.txt_continue_fragment_bank_util))
                .setListener {
                    when (it) {
                        UtilAlert.ACTION_ACCEPT -> {
                            val transaction =
                                requireActivity().supportFragmentManager.beginTransaction()
                            transaction.replace(
                                (requireView().parent as ViewGroup).id,
                                AdminModulesFragment.newInstance()
                            ).disallowAddToBackStack()
                                .commit()
                        }
                        UtilAlert.ACTION_CANCEL -> {
                            activity?.onBackPressed()
                        }
                    }
                }
                .build()
                .show(childFragmentManager, this.javaClass.name)
        } else {
            UtilAlert.Builder()
                .setTitleCustom(buildTextSuccess())
                .setTextButtonOk(getString(R.string.txt_continue_fragment_bank_util))
                .setListener {
                    when (it) {
                        UtilAlert.ACTION_ACCEPT -> {
                            activity?.onBackPressed()
                        }
                    }
                }
                .build()
                .show(childFragmentManager, this.javaClass.name)
        }
    }

    private fun buildTextSuccess(): SpannableString {
        val textBuilder = StringBuilder().apply {
            append(
                String.format(
                    getString(R.string.message_community_save_success),
                    bindingCommunity.itemDetail!!.name
                )
            )
        }

        val spannableString = SpannableString(textBuilder.toString())
        spannableString.setSpan(
            StyleSpan(Typeface.NORMAL),
            0,
            textBuilder.length - 1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannableString
    }

    private fun updateData(
        item: CommunityDetailResponse?,
        daySelected: MutableLiveData<MutableList<Day>>,
        schedule: ScheduleXXXXX,
        daySelectedOther: MutableLiveData<MutableList<Day>>,
        scheduleOther: ScheduleXXXXX,
    ) {
        val listFirstDays = daySelected.value?.filter { filter -> filter.checked }?.map { dayItem ->
            ServiceHour(
                day = dayItem.id,
                schedules = listOf(
                    ScheduleX(
                        hourStart = if (schedule.startHour?.length!! < 5) "0${schedule.startHour}" else schedule.startHour,
                        hourEnd = if (schedule.endHour?.length!! < 5) "0${schedule.endHour}" else schedule.endHour,
                    )
                )
            )
        }?.toList() ?: listOf()

        val listSecondDays = daySelectedOther.value?.filter { filter -> filter.checked }
            ?.map { dayItem ->
                ServiceHour(
                    day = dayItem.id,
                    schedules = listOf(
                        ScheduleX(
                            hourStart = if (scheduleOther.startHour?.length!! < 5) "0${scheduleOther.startHour}" else scheduleOther.startHour,
                            hourEnd = if (scheduleOther.endHour?.length!! < 5) "0${scheduleOther.endHour}" else scheduleOther.endHour,
                        )
                    )
                )
            }?.toList() ?: listOf()

        item!!.serviceHoursGeneral = listFirstDays + listSecondDays

        bindingCommunity.apply {
            itemDetail = item
        }
    }

    private fun handleUploadImage(): (Boolean) -> Unit = { result ->
        hideLoader()
        when (result) {
            true -> {
                bindingCommunity.ivCommunityDetail.loadByUrl(
                    url = eamxcu_preferences.getData(
                        EAMXEnumUser.URL_PICTURE_COMMUNITY.name,
                        EAMXTypeObject.STRING_OBJECT
                    ) as String
                )
                toast(getString(R.string.message_upload_image_success))
            }
            false -> toast(getString(R.string.message_upload_image_fail))
        }
    }

    private fun handleErrorUploadImage(): (String) -> Unit = { message ->
        hideLoader()
        toast(message)
    }

    private fun handleCommunityByName(): (CommunitiesByNameResponse) -> Unit = { result ->
        hideLoader()
        bindingBottomSheetCommunity.apply {
            result.let { list ->
                if (list.isNotEmpty()) {
                    result.forEach { item -> item.name?.let { dataSourceCommunitylist.add(it) } }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        dataSourceCommunitylist
                    )
                    acTestSuggestion.threshold = 1
                    acTestSuggestion.setAdapter(adapter)
                }
            }
        }
    }

    private fun handleError(): (String) -> Unit = {
        hideLoader()
        UtilAlert
            .Builder()
            .setTitle(getString(R.string.title_dialog_warning))
            .setMessage(it)
            .build()
            .show(childFragmentManager, "")
    }

    private fun handleCommunityDetail(): (CommunityDetailResponse) -> Unit = { data ->
        hideLoader()
        bindingCommunity.apply {
            nombre = data.name.toString()
            idComunity = data.id!!
            reviewViewModel.getComentarios(idComunity, 1)
            if (!data.rating.isNullOrEmpty()) {
                star = PublicFunctions().redondearStar(data.rating!!.toFloat())
                rbIglesia.rating = star
            }

            itemDetail = data
            data.imageUrl?.let {
                ivCommunityDetail.loadByUrl(
                    url = it
                )
            }
            data.longitude?.let {
                longitude=it
            }
            data.latitude?.let {
                latitude=it
            }
        }
        setSocialMediaRemote(data)
    }

    private fun handleLocation(): (Location) -> Unit = { data ->
        if (ubicacionInicial) {
            if (data != null) {
                location?.value = data
                ubicacionInicial = false
            } else {
                hideLoader()
                UtilAlert.Builder()
                    .setTitle("Aviso")
                    .setMessage("Dirección no valida")
                    .build()
                    .show(childFragmentManager, tag)
            }
        }
    }

    private fun handlePartherCommunities(): (PartnerCommunitiesResponse) -> Unit = { data ->
        bindingCommunity.apply {
            data.forEach { itemCummunity ->
                setAdapterCommunityLinked(
                    communityNameP = itemCummunity.name ?: "",
                    instituteOrAssociationP = itemCummunity.instituteOrAssociation?.name ?: "",
                    managerNameP = "",
                    phoneP = "",
                    emailP = ""
                )
            }
        }
    }

    private fun handleActivities(): (ActivitiesResponse) -> Unit = { data ->
        serviceActivityList.clear()

        bindingCommunity.apply {

            if (data == null || data.isEmpty()) {
                if (cvServiceActivity.visibility != View.VISIBLE) {
                    tvAddServices.visibility = View.GONE
                    ivAddServices.visibility = View.GONE

                    cvServiceActivity.visibility = View.VISIBLE
                }
            } else {
                data.forEach { itemActivity ->
                    setAdapterActivity(
                        nameActivity = itemActivity.name ?: "",
                        addressedToP = itemActivity.gearedToward ?: "",
                        descriptionP = itemActivity.description ?: "",
                        scheduleDaysP = itemActivity.getDays(),
                        scheduleHourP = itemActivity.getHours(),
                        daysList = itemActivity.getDaysInt(),
                        scheduleItem = itemActivity.getScheduler()
                    )
                }
            }
        }
    }

    private fun setSocialMediaRemote(data: CommunityDetailResponse) {
        socialMediaList.clear()

        if (data.instagram == null && data.twitter == null && data.facebook == null && data.website == null && data.streamingChannel == null) {
            bindingCommunity.apply {

                tvAddSocialNetwork.visibility = View.GONE
                ivAddSocialNetwork.visibility = View.GONE
                cvSocialNetwork.visibility = View.GONE

                cvEmptySocialNetwork.visibility = View.VISIBLE
            }
        } else {
            bindingCommunity.apply {
                tvAddSocialNetwork.visibility = View.VISIBLE
                ivAddSocialNetwork.visibility = View.VISIBLE
                cvSocialNetwork.visibility = View.VISIBLE

                cvEmptySocialNetwork.visibility = View.GONE
            }

            data.instagram?.let {
                if (it.isNotEmpty()) {
                    setAdapterSocialMedia(
                        nameNetwork = getString(R.string.txt_instagram),
                        value = it
                    )
                }
            }
            data.twitter?.let {
                if (it.isNotEmpty()) {
                    setAdapterSocialMedia(nameNetwork = getString(R.string.txt_twtter), value = it)
                }
            }
            data.facebook?.let {
                if (it.isNotEmpty()) {
                    setAdapterSocialMedia(
                        nameNetwork = getString(R.string.txt_facebook),
                        value = it
                    )
                }
            }
            data.website?.let {
                if (it.isNotEmpty()) {
                    setAdapterSocialMedia(nameNetwork = getString(R.string.txt_website), value = it)
                }
            }
            data.streamingChannel?.let {
                if (it.isNotEmpty()) {
                    setAdapterSocialMedia(nameNetwork = getString(R.string.txt_youtube), value = it)
                }
            }
        }
    }

    private fun cancelEditCommunity() {
        UtilAlert.Builder()
            .setTypeAlert(getString(R.string.title_dialog_warning))
            .setMessage(getString(R.string.message_community_cancel_edit))
            .setTextButtonOk(getString(R.string.btn_yes))
            .setTextButtonCancel(getString(R.string.btn_no))
            .setListener {
                if (UtilAlert.ACTION_ACCEPT == it) {
                    activity?.onBackPressed()
                }
            }
            .build()
            .show(childFragmentManager, this.javaClass.name)
    }

    private fun saveData() {
        if (validData(bindingCommunity.itemDetail!!, serviceActivityList)) {
            return
        }
        showLoader()
        val request = buildDataGeneralData(bindingCommunity.itemDetail!!)
        if (idComunity == 0) {
            viewModel.completeCommunity(request)
        } else {
            viewModel.completeCommunity(idComunity, request)
        }
    }

    private fun buildDataGeneralData(dataGeneral: CommunityDetailResponse): CompleteCommunityRequest {
        return CompleteCommunityRequest(
            typeId = dataGeneral.type?.id ?: 0,
            description = dataGeneral.description,
            charisma = dataGeneral.charisma,
            address = dataGeneral.address,
            longitude = longitude,
            latitude = latitude,
            email = dataGeneral.email,
            phone = dataGeneral.phone,
            instagram = getSocialMedia(getString(R.string.txt_instagram)),
            facebook = getSocialMedia(getString(R.string.txt_facebook)),
            twitter = getSocialMedia(getString(R.string.txt_twtter)),
            website = getSocialMedia(getString(R.string.txt_website)),
            streamingChannel = getSocialMedia(getString(R.string.txt_youtube)),
            activities = mapActivitiesX(),
            serviceHours = mapServicesHour(dataGeneral)
        )
    }

    private fun getSocialMedia(name: String): String {
        return socialMediaList.firstOrNull { item ->
            item.nameNetwork == name
        }?.value ?: ""
    }

    private fun mapActivitiesX(): List<ActivityX> {
        return serviceActivityList.map { item ->
            ActivityX(
                name = item.name,
                gearedToward = item.addressedTo,
                description = item.description,
                serviceHours = item.daysList.map { dayItem ->
                    ServiceHourXXXX(
                        day = dayItem,
                        schedule = item.schedule?.let { listOf(it) }
                    )
                }
            )
        }
    }

    private fun mapServicesHour(dataGeneral: CommunityDetailResponse): List<ServiceHourXXXXX> {
        return dataGeneral.serviceHoursGeneral?.let {
            it.map { day ->
                ServiceHourXXXXX(
                    day = day.day,
                    schedule = day.schedules?.map { scheduleItem ->
                        ScheduleXXXXXX(
                            startHour = scheduleItem.hourStart?.trim(),
                            endHour = scheduleItem.hourEnd?.trim(),
                        )
                    }
                )
            }
        } ?: listOf()
    }

    private fun validData(
        itemDetail: CommunityDetailResponse,
        serviceActivityList: List<ServiceActivityModel>,
    ): Boolean {
        val message = StringBuffer()
        //Edit
        if (itemDetail.description.isNullOrEmpty()) {
            message.append(getString(R.string.message_error_description) + "\n")
        }

        if (itemDetail.charisma.isNullOrEmpty()) {
            message.append(getString(R.string.message_error_charisma) + "\n")
        }

        if (itemDetail.email.isNullOrEmpty()) {
            message.append(getString(R.string.message_error_email) + "\n")
        }

        if (Patterns.EMAIL_ADDRESS.matcher(itemDetail.email.toString()).matches().not()) {
            message.append(getString(R.string.message_error_email_invalid) + "\n")
        }

        if (itemDetail.phone.isNullOrEmpty()) {
            message.append(getString(R.string.message_error_phone) + "\n")
        }

        if (itemDetail.phone?.length != 10) {
            message.append(getString(R.string.message_error_phone_invalid) + "\n")
        }

        if (itemDetail.hours.isEmpty() ||
            itemDetail.hours == getString(R.string.hint_scheduler_hour)
        ) {
            message.append(getString(R.string.message_schedule_missed) + "\n")
        }

        if (itemDetail.days.isEmpty() ||
            itemDetail.days == getString(R.string.hint_scheduler_hour)
        ) {
            message.append(getString(R.string.message_days_missed) + "\n")
        }

        if (itemDetail.days.isEmpty()) {
            message.append(getString(R.string.message_days_missed) + "\n")
        }
        itemDetail.longitude?.let {
            if(itemDetail.longitude==0.0){
                message.append(getString(R.string.message_location_missed) + "\n")
            }
        }
        itemDetail.latitude?.let {
            if(itemDetail.latitude==0.0){
                message.append(getString(R.string.message_location_missed) + "\n")
            }
        }
        //valid services
        /*if (serviceActivityList.isEmpty()) {
            message.append(getString(R.string.message_activity_missed) + "\n")
        }*/

        if (message.isNotEmpty()) {
            toast(message)
        }

        return message.isNotEmpty()
    }

    private fun openChooseSourceImageCommunity() {
        openWindowToChooseUploadImage(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        managerResultActivity(
            requestCode,
            resultCode,
            data,
            eamxcu_preferences.USER_COMMUNITY_ID,
            SOURCE_COMMUNITY,
            viewModelImageManager
        ) {
            when (it) {
                EAMXAction.SHOW -> showLoader()
                else -> hideLoader()
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
                PERMISSION_CAMERA -> {
                    openCamera(CAMERA_CODE)
                }
                PERMISSION_GALLERY -> {
                    openGallery(GALLERY_CODE)
                }
                LOCATIONP -> {
                    viewModel.getLocation()
                    showLoader()
                    ubicacionInicial = true
                }
            }
        }
    }

    private fun openCall() {
        requireActivity().startCallTo(phone = bindingCommunity.itemDetail?.phone ?: "")
    }

    private fun openEmail() {
        requireActivity().sendEmailTo(email = bindingCommunity.itemDetail?.email ?: "")
    }

    private fun openRideMe() {
        requireActivity().openGoogleMapsTo(
            latitude = bindingCommunity.itemDetail?.latitude?.toString() ?: "",
            longitude = bindingCommunity.itemDetail?.longitude?.toString() ?: "",
        )
    }

    private fun openCommunityEditor() {
        dialogEditData.show()
    }

    private fun openSocialNetwork() {
        dialogSocialMedia.show()
    }

    private fun addSocialMedia(binding: BottomSheetSocialNetworkBinding): Boolean {
        val item = socialMediaList.firstOrNull { item ->
            item.value == binding.teUserSocial.text.toString() &&
                    item.nameNetwork == binding.spSocialNetwork.selectedItem.toString()
        }

        if (item != null) {
            toast(getString(R.string.message_social_media_repeat))
            return false
        }

        if (binding.teUserSocial.text.toString().isEmpty()) {
            binding.teUserSocial.error = getString(R.string.message_input_requiered)
            return false
        }
        if (Patterns.WEB_URL.matcher(binding.teUserSocial.text.toString()).matches().not()) {
            binding.teUserSocial.error = getString(R.string.message_error_url_invalid)
            return false
        }
        binding.teUserSocial.error = null

        setAdapterSocialMedia(
            nameNetwork = binding.spSocialNetwork.selectedItem.toString(),
            value = binding.teUserSocial.text.toString()
        )

        return true
    }

    private fun setAdapterSocialMedia(nameNetwork: String, value: String) {
        socialMediaAdapter.addItem(
            item = SocialMediaModel(
                nameNetwork = nameNetwork,
                value = value
            )
        )
    }

    private fun openServices() {
        dialogServiceActivity.show()
    }

    private fun addServices(
        binding: BottomSheetServicesBinding,
        dayList: MutableList<Day>,
        schedule: ScheduleXXXXX,
    ): Boolean {
        val item = serviceActivityList.firstOrNull { item ->
            item.name == binding.teNameServiceActivity.text.toString() &&
                    item.addressedTo == binding.teAddressedTo.text.toString() &&
                    item.description == binding.teDescription.text.toString() &&
                    item.scheduleDays == binding.tvScheduleDays.text.toString() &&
                    item.scheduleHour == binding.tvScheduleDays.text.toString()
        }

        if (item != null) {
            toast(getString(R.string.message_service_activity_repeat))
            return false
        }

        if (binding.teNameServiceActivity.text.toString().isEmpty()) {
            binding.teNameServiceActivity.error =
                getString(R.string.message_error_services_Activity)
        }

        if (binding.teAddressedTo.text.toString().isEmpty()) {
            binding.teAddressedTo.error = getString(R.string.message_error_addressedTo)
        }

        if (binding.tvScheduleDays.text.toString().isEmpty() ||
            binding.tvScheduleDays.text.toString() == getString(R.string.hint_scheduler_hour_space) ||
            binding.tvScheduleDays.text.toString() == getString(R.string.hint_scheduler_days)
        ) {
            toast(getString(R.string.message_days_missed))
            return false
        }

        if (binding.tvScheduleHour.text.toString().isEmpty() ||
            binding.tvScheduleHour.text.toString() == getString(R.string.hint_scheduler_hour)
        ) {
            toast(getString(R.string.message_schedule_missed))
            return false
        }

        if (binding.teNameServiceActivity.error != null ||
            binding.teAddressedTo.error != null
        ) {
            return false
        }

        binding.teAddressedTo.error = null
        binding.teNameServiceActivity.error = null

        setAdapterActivity(
            nameActivity = binding.teNameServiceActivity.text.toString(),
            addressedToP = binding.teAddressedTo.text.toString(),
            descriptionP = binding.teDescription.text.toString(),
            scheduleDaysP = binding.tvScheduleDays.text.toString(),
            scheduleHourP = binding.tvScheduleHour.text.toString(),
            daysList = dayList.filter { it.checked }.map { itemFil -> itemFil.id }.toList(),
            scheduleItem = schedule
        )

        toast(getString(R.string.message_add_success_activity))
        return true
    }

    private fun setAdapterActivity(
        nameActivity: String,
        addressedToP: String,
        descriptionP: String,
        scheduleDaysP: String,
        scheduleHourP: String,
        daysList: List<Int>,
        scheduleItem: ScheduleXXXXX,
    ) {
        serviceActivityAdapter.addItem(
            item = ServiceActivityModel(
                name = nameActivity,
                addressedTo = addressedToP,
                description = descriptionP,
                scheduleDays = scheduleDaysP,
                scheduleHour = scheduleHourP,
                daysList = daysList,
                schedule = scheduleItem
            )
        )
    }

    private fun cleanFormService(binding: BottomSheetServicesBinding) {
        binding.apply {
            teNameServiceActivity.text?.clear()
            teAddressedTo.text?.clear()
            teDescription.text?.clear()
            tvScheduleDays.hint = getString(R.string.hint_scheduler_days)
            tvScheduleHour.hint = getString(R.string.hint_scheduler_hour)
        }
    }

    private fun openCommunity() {
        dialogCommunityLinked.show()
    }

    private fun addCommunityLinked(binding: BottomSheetOtherCommunityBinding): Boolean {
        binding.apply {
            val item = serviceActivityList.firstOrNull { item ->
                item.name == teManagerNameOfCommunity.text.toString() &&
                        item.addressedTo == teManagerNameOfCommunity.text.toString() &&
                        item.description == teEmail.text.toString()
            }

            if (item != null) {
                toast(getString(R.string.message_service_activity_repeat))
                return false
            }

            if (teManagerNameOfCommunity.text.toString().isEmpty()) {
                teManagerNameOfCommunity.error = getString(R.string.message_input_requiered)
            }

            if (acTestSuggestion.text.toString().isEmpty()) {
                acTestSuggestion.error = getString(R.string.message_input_requiered)
            }

            if (teEmail.text.toString().isEmpty()) {
                teEmail.error = getString(R.string.message_input_requiered)
            }

            if (teManagerNameOfCommunity.error != null ||
                acTestSuggestion.error != null ||
                teEmail.error != null
            ) {
                return false
            }

            acTestSuggestion.error = null
            teManagerNameOfCommunity.error = null
            teEmail.error = null

            setAdapterCommunityLinked(
                communityNameP = acTestSuggestion.text.toString(),
                instituteOrAssociationP = "",
                managerNameP = teManagerNameOfCommunity.text.toString(),
                phoneP = tePhone.text.toString(),
                emailP = teEmail.text.toString()
            )
        }

        toast(getString(R.string.message_add_success))
        return true
    }

    private fun setAdapterCommunityLinked(
        communityNameP: String,
        instituteOrAssociationP: String,
        managerNameP: String,
        phoneP: String,
        emailP: String,
    ) {
        communityLinkedAdapter.addItem(
            item = CommunityLinkedModel(
                communityName = communityNameP,
                instituteOrAssociation = instituteOrAssociationP,
                managerName = managerNameP,
                phone = phoneP,
                email = emailP
            )
        )
    }

    private fun cleanFormCommunity(binding: BottomSheetOtherCommunityBinding) {
        binding.apply {
            acTestSuggestion.text?.clear()
            teManagerNameOfCommunity.text?.clear()
            tePhone.text?.clear()
            teEmail.text?.clear()
        }
    }

    private fun changeFragment() {
        val bundle = Bundle()
        bundle.putInt("idIglesia", idComunity)
        bundle.putFloat("star", star)
        bundle.putString("nombre", nombre)
        NavigationFragment.Builder()
            .setActivity(requireActivity())
            .setView(requireView().parent as ViewGroup)
            .setFragment(ReviewFragment.newInstance(this))
            .setBundle(bundle)
            .setAllowStack(true)
            .build().nextWithReplace()

    }

}