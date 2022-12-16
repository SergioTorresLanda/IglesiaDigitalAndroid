package com.wallia.eamxcomunidades.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.wallia.eamxcomunidades.R
import com.wallia.eamxcomunidades.databinding.*
import com.wallia.eamxcomunidades.model.*
import com.wallia.eamxcomunidades.viewmodel.ComunidadesViewModel
import mx.arquidiocesis.eamxcommonutils.common.EAMXBaseFragment
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.toast

class CustomDialogSaveCommunityFragment(
    private val viewModel : ComunidadesViewModel,
    private val dataGeneral : CommunityDetailResponse,
    private val socialMediaList : List<SocialMediaModel>,
    private val servicesList : List<ServiceActivityModel>,
    private val listener: (Boolean) -> Unit) : DialogFragment() {

    lateinit var binding : FragmentCustomDialogSaveCommunityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomDialogSaveCommunityBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()
    }

    private fun initObserver() {
        viewModel.completeCommunityResponse.observe(viewLifecycleOwner, handlerCommunity())
        viewModel.errorResponse.observe(viewLifecycleOwner, handlerError())
    }

    private fun handlerError(): (String) -> Unit = { message ->
        listener(false)
        dismiss()
        UtilAlert.Builder()
            .setTitle(getString(R.string.title_dialog_warning))
            .setMessage(message)
            .build()
            .show(childFragmentManager, this.javaClass.name)
    }

    private fun handlerCommunity(): (Boolean) -> Unit = {
        listener(false)
        toast(String.format(getString(R.string.message_community_save_success), dataGeneral.name))
        activity?.onBackPressed()
    }

    private fun initView(){
        binding.apply {
            tvTitle.text = String.format(getString(R.string.label_warning_save_community), dataGeneral.name)
            btnContinue.setOnClickListener{saveCommunity()}
            btnLater.setOnClickListener{dismissLater()}
        }            
    }

    private fun dismissLater() {
        dismiss()
    }

    private fun saveCommunity() {
        listener(true)
        val request = buildDataGeneralData()
        viewModel.completeCommunity(request)
    }

    private fun buildDataGeneralData() : CompleteCommunityRequest{
        return CompleteCommunityRequest(
            typeId = dataGeneral.type?.id ?: 0,
            description = dataGeneral.description,
            charisma = dataGeneral.charisma,
            address = dataGeneral.address,
            longitude = dataGeneral.longitude,
            latitude = dataGeneral.latitude,
            email =  dataGeneral.email,
            phone = dataGeneral.phone,
            instagram = getSocialMedia(getString(R.string.txt_instagram)),
            facebook = getSocialMedia(getString(R.string.txt_facebook)),
            twitter = getSocialMedia(getString(R.string.txt_twtter)),
            website = getSocialMedia(getString(R.string.txt_website)),
            streamingChannel = dataGeneral.streamingChannel,
            activities = mapActivitiesX(),
            serviceHours = mapServicesHour()
        )
    }

    private fun getSocialMedia(name :  String) : String{
        return socialMediaList.firstOrNull { item -> item.nameNetwork == name }?.value ?: ""
    }

    private fun mapActivitiesX() :  List<ActivityX> {
        return servicesList.map { item ->
            ActivityX(
                name = item.name,
                gearedToward = item.description,
                description = item.addressedTo,
                serviceHours = item.daysList.map { dayItem ->
                    ServiceHourXXXX(
                        day = dayItem,
                        schedule = item.schedule?.let{ listOf(it) }
                    )
                }
            )
        }
    }

    private fun mapServicesHour() :  List<ServiceHourXXXXX> {
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
}