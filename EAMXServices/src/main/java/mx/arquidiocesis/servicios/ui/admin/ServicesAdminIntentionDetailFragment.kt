package mx.arquidiocesis.servicios.ui.admin

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.servicios.R
import mx.arquidiocesis.servicios.adapter.InteractionDetailAdapter
import mx.arquidiocesis.servicios.databinding.FragmentServicesAdminIntentionDetailBinding
import mx.arquidiocesis.servicios.model.admin.view.AdminIntentionDetailModel
import mx.arquidiocesis.servicios.repository.AdminServicesIntentionsRepository
import mx.arquidiocesis.servicios.viewModel.AdminServicesIntentionsViewModel
import android.content.Context.DOWNLOAD_SERVICE
import android.content.IntentFilter
import androidx.core.net.toUri
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXMessageError
import mx.arquidiocesis.eamxcommonutils.util.toast
import mx.arquidiocesis.eamxcommonutils.util.sharedScreenShootWithOtherApps


class ServicesAdminIntentionDetailFragment : FragmentBase() {

    companion object {

        const val INTERACTION_DATE = "Interaction_Date"
        const val INTERACTION_HOUR = "Interaction_Hour"
        const val INTERACTION_TYPE = "Interaction_Type"
        const val TYPE_CHURCH = "CHURCH"
        const val TYPE_COMMUNITY = "COMMUNITY"

        @JvmStatic
        fun newInstance(callBack : EAMXHome) =
            ServicesAdminIntentionDetailFragment().apply {
                this.callBack = callBack
            }
    }

    private lateinit var binding : FragmentServicesAdminIntentionDetailBinding
    val viewModel : AdminServicesIntentionsViewModel by lazy {
        getViewModel {
            AdminServicesIntentionsViewModel(AdminServicesIntentionsRepository(requireContext()))
        }
    }

    private val date : String by lazy {
        requireArguments().getString(INTERACTION_DATE) ?: ""
    }

    private val hour : String by lazy {
        requireArguments().getString(INTERACTION_HOUR) ?: ""
    }

    private val typeDetail : String by lazy {
        requireArguments().getString(INTERACTION_TYPE) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServicesAdminIntentionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initObservers()
    }

    private fun initView() {
        binding.apply {
            tvDownload.setOnClickListener {v -> handleDownload(v)}
            ivDownload.setOnClickListener {v -> handleDownload(v)}
            tvSend.setOnClickListener {v -> handleSend(v)}
            ivArrow.setOnClickListener {v -> handleSend(v)}
        }
    }

    private fun handleSend(v: View?) {
        requireActivity().sharedScreenShootWithOtherApps(
            view = binding.root,
            typeImage = "image/png",
            titleInButtonSheet = getString(R.string.app_name),
            titleImage = getString(R.string.title_screen_shot_image),
            descriptionImage =  getString(R.string.title_screen_shot_image_description)
        )
    }

    private fun handleDownload(v: View?) {
        showLoader()
        viewModel.getUrlDownloadIntention(date, hour)
    }

    private fun initData() {
        if(setVisibilityViewByType()){
            binding.apply {
                tvAssociation.text = getString(R.string.label_community)
                tvTimeTitle.visibility = View.GONE
                tvTime.visibility = View.GONE
            }
        }

        showLoader()
        viewModel.getIntentionDetail(date, hour)
    }

    private fun initObservers() {
        viewModel.responseIntentionDetailModelP.observe(viewLifecycleOwner, handleDetail())
        viewModel.responseUrlDownloadIntentionP.observe(viewLifecycleOwner, handleUrlDownload())
        viewModel.errorP.observe(viewLifecycleOwner, handleError())
    }

    private fun handleDetail(): (AdminIntentionDetailModel) -> Unit = { itemDetail ->
        hideLoader()
        val adapterDetail = InteractionDetailAdapter(itemDetail.list)
        binding.apply {
            item = itemDetail
            rvIntention.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = adapterDetail
            }
        }
    }

    private fun setVisibilityViewByType() : Boolean {
        return when(typeDetail){
            TYPE_CHURCH -> false
            TYPE_COMMUNITY -> true
            else -> false
        }
    }

    private fun handleUrlDownload():(String) -> Unit = { url ->
        hideLoader()
        toast(getString(R.string.message_start_download))
        val request : DownloadManager.Request= DownloadManager.Request(url.toUri())
            .setTitle(getString(R.string.app_name))
            .setDescription(getString(R.string.text_download_file))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)

        val managerDownload: DownloadManager = activity?.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val downloadIdentifier = managerDownload.enqueue(request)

        val listenerDownload = object:BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1)!!
                if(id == downloadIdentifier){
                    requireActivity().toast(getString(R.string.download_succesfull))
                }
            }
        }
        activity?.registerReceiver(listenerDownload, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private fun handleError(): (EAMXMessageError) -> Unit = { error ->
        hideLoader()
        UtilAlert.Builder()
            .setTitle("Aviso")
            .setMessage(error.message ?: "")
            .setIsCancel(false)
            .setListener {
                if(error.back){
                    activity?.onBackPressed()
                }
            }
            .build()
            .show(childFragmentManager, "")
    }
}