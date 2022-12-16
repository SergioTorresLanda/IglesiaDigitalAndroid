package mx.arquidiocesis.servicios.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.util.*
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.servicios.databinding.FragmentServicesAdminIntentionsCommunityBinding
import mx.arquidiocesis.servicios.ui.admin.ServicesAdminIntentionDetailFragment.Companion.INTERACTION_DATE
import mx.arquidiocesis.servicios.ui.admin.ServicesAdminIntentionDetailFragment.Companion.INTERACTION_HOUR
import mx.arquidiocesis.servicios.ui.admin.ServicesAdminIntentionDetailFragment.Companion.INTERACTION_TYPE
import mx.arquidiocesis.servicios.ui.admin.ServicesAdminIntentionDetailFragment.Companion.TYPE_COMMUNITY
import java.util.*
import java.util.Calendar.*

class ServicesAdminIntentionsCommunityFragment : FragmentBase() {

    companion object {
        private val HARD_HOUR = "00:00:00"
        @JvmStatic
        fun newInstance(callBack: EAMXHome) =
            ServicesAdminIntentionsCommunityFragment().apply {
                this.callBack = callBack
            }
    }
    private lateinit var dateString: String
    private lateinit var binding: FragmentServicesAdminIntentionsCommunityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServicesAdminIntentionsCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {
        callBack.showToolbar(true, AppMyConstants.intentionsCommunity)
        binding.apply {
            tvCancel.setOnClickListener{ v -> cancelAction(v)}
            tvOk.setOnClickListener { v -> launchDetail(v) }
            cvDays.minDate = Date().time
            cvDays.setOnDateChangeListener { p0, year, month, day ->
                dateString = getInstance().insertDateMiddleDashAndYyyMMddCurrent(day, month, year)
                tvDate.text =  getInstance().dateInFormatDayNameDDMonthName(day, month, year)
            }
        }
    }

    private fun cancelAction(v: View?) {
       activity?.onBackPressed()
    }

    private fun launchDetail(v: View?) {
        val bundle = Bundle()
        bundle.apply {
            putString(INTERACTION_HOUR, HARD_HOUR)
            putString(INTERACTION_DATE, dateString)
            putString(
                INTERACTION_TYPE,
                TYPE_COMMUNITY
            )
        }
        NavigationFragment.Builder()
            .setActivity(requireActivity())
            .setView(requireView().parent as ViewGroup)
            .setBundle(bundle)
            .setFragment(ServicesAdminIntentionDetailFragment.newInstance(callBack))
            .setAllowStack(true)
            .build().nextWithReplace()
    }

    private fun initData() {
        dateString = "${getInstance().get(YEAR)}-${getInstance().get(MONTH)}-${getInstance().get(DATE)}"
        binding.tvDate.text =  getInstance().dateInFormatDayNameDDMonthNameCurrent()
    }
}