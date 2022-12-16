package mx.arquidiocesis.eamxdonaciones.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.example.eamxdonaciones.databinding.FragmentWebviewDonationBinding
import kotlinx.coroutines.Job
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import mx.arquidiocesis.eamxcommonutils.util.eamxLog
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxdonaciones.Repository.RepositoryDonation
import mx.arquidiocesis.eamxdonaciones.viewmodel.DonacionesViewModel


class WebViewFragment(private val link: String) : FragmentBase() {
    private var firebaseAnalytics: EAMXFirebaseManager? = null

    lateinit var job: Job

    private val viewmodel: DonacionesViewModel by lazy {
        getViewModel {
            DonacionesViewModel(RepositoryDonation(requireContext()))
        }
    }
    lateinit var binding: FragmentWebviewDonationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWebviewDonationBinding.inflate(inflater, container, false)
        firebaseAnalytics = EAMXFirebaseManager(inflater.context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.wvDonation.apply {
            showLoader()
            firebaseAnalytics?.setLogEvent("ui_interaction", Bundle().apply {
                putString("flow", "view_form_card")
                putString("section", "dontation")
                putString("screen_name", "form_card")
                putString("element", link)
            })
            loadUrl(link)
            eamxLog("rutaLink $link")
            settings.apply {
                javaScriptEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = true
                setSupportZoom(true)
            }
            addJavascriptInterface(
                WebAppInterface(requireContext(), viewmodel, firebaseAnalytics),
                "AndroidInterface",
            )
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    hideLoader()
                    val progress = view?.progress ?: 0
                    if (progress < 100) {
                    }
                }

                override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?
                ) {
                    super.onReceivedHttpError(view, request, errorResponse)
                    hideLoader()
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    hideLoader()
                }


                /*  override fun shouldOverrideUrlLoading(
                      view: WebView?,
                      request: WebResourceRequest?
                  ): Boolean {
                      hideLoader()
                      return true
                  }*/
            }
        }
        job = viewmodel.initCounter()

        initObservers()
    }

    fun initObservers() {
        viewmodel.counterMinutes.observe(viewLifecycleOwner) {
            binding.tvTM.text = it
        }
        viewmodel.counterSeconds.observe(viewLifecycleOwner) {
            binding.tvTS.text = it

        }
        viewmodel.launchMessageCounter.observe(viewLifecycleOwner) { l ->
            l?.let {
                job.cancel()
                requireActivity().onBackPressed()

            }

        }
        viewmodel.succesResponse.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert
                .Builder()
                .setTitle("")
                .setListener { action ->
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
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
        }
        viewmodel.errorResponse.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
        }
    }
}