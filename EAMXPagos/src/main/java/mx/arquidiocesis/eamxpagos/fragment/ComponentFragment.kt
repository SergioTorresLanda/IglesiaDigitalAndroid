package mx.arquidiocesis.eamxpagos.fragment


import DonacionesViewModel
import RepositoryDonation
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Job
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import mx.arquidiocesis.eamxcommonutils.util.eamxLog
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxpagos.Repository.RepositoryServices
import mx.arquidiocesis.eamxpagos.config.OriginType
import mx.arquidiocesis.eamxpagos.databinding.FragmentGenericPaymentBinding
import mx.arquidiocesis.eamxpagos.fragment.ui.WebAppInterface
import mx.arquidiocesis.eamxpagos.model.MentionRequestPost
import mx.arquidiocesis.eamxpagos.viewmodel.IntencionViewModel


class ComponentFragment(private val link: String, private val originType: OriginType) :
    FragmentBase() {
    private var firebaseAnalytics: EAMXFirebaseManager? = null
    lateinit var job: Job
    private val viewmodel: DonacionesViewModel by lazy {
        getViewModel {
            DonacionesViewModel(RepositoryDonation(requireContext()))
        }
    }

    private val viewModel: IntencionViewModel by lazy {
        getViewModel {
            IntencionViewModel(RepositoryServices(requireContext()))
        }
    }
    lateinit var binding: FragmentGenericPaymentBinding
    var mentionInformation = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGenericPaymentBinding.inflate(inflater, container, false)
        firebaseAnalytics = EAMXFirebaseManager(inflater.context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mentionInformation = arguments?.getString("mentionInformation").toString()
        val concepto = arguments?.getString("concepto")
        val monto = arguments?.getString("monto")


        if (concepto != null) {
            binding.txDescription.setText(concepto.toString())
        } else {
            binding.txDescription.setText("Ha ocurrido un error, intente nuevamente.")
        }
        if (monto != null) {
            binding.txMonto.setText(monto.toString())
        } else {
            binding.txMonto.setText("Ha ocurrido un error, intente nuevamente.")
        }
        binding.wvDonation.apply {
            showLoader()
            /*firebaseAnalytics?.setLogEvent("ui_interaction", Bundle().apply {
                putString("flow", "view_form_card")
                putString("section", "dontation")
                putString("screen_class", "form_card")
                putString("element", link)
            })*/
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
                    showError()
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    hideLoader()
                    return true
                }
            }
        }
        binding.btnTest.setOnClickListener {
            UtilAlert
                .Builder()
                .setTitle("Advertencia")
                .setListener { action ->
                    requireActivity().run {
                        startActivity(
                            Intent().apply {
                                setClassName(
                                    requireActivity(),
                                    "mx.arquidiocesis.eamxgeneric.activities.Home_Home"
                                )
                            })
                        finish()
                    }

                }
                .setMessage("Estás seguro que deseas salir, se perderá todo el proceso")
                .build()
                .show(childFragmentManager, "")

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
                .setTitle("Aviso")
                .setListener { action ->
                    requireActivity().run {
                        startActivity(
                            Intent().apply {
                                setClassName(
                                    requireActivity(),
                                    "mx.arquidiocesis.eamxgeneric.activities.Home_Home"
                                )
                            })
                        finish()
                    }

                }
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
            if (originType.equals(OriginType.INTENCIONES)) {
                sendMention()
            }
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

    private fun showError() {
        binding.wvDonation.loadDataWithBaseURL(
            null, "<!doctype html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <title>400 Bad Request</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div>\n" +
                    "        <br>\n" +
                    "        <br>\n" +
                    "        <br>\n" +
                    "        <br>\n" +
                    "        <h3 FONT SIZE=7>Por el momento el servicio no está disponible,</h3>\n" +
                    "        <h3  FONT SIZE=7>inténtelo más tarde.</h3>\n" +
                    "        </div>\n" +
                    "</body>\n" +
                    "</html>", "text/html", "utf-8", null
        )
    }


    fun sendMention() {
        if (mentionInformation != null) {
            val delim = "#"
            val list = mentionInformation.split(delim)
            showLoader()
            viewModel.sendMention(
                MentionRequestPost(
                    list[0].replace("\"", "").trim(),
                    list[1],
                    list[2].toInt(),
                    list[3],
                    list[4],
                    list[5].toInt(),
                    list[6].replace("\"", "").trim()
                )
            )
            hideLoader()
        } else {
            UtilAlert.Builder()
                .setTitle("Aviso")
                .setMessage("Tu mención no se completó correctamente, intentalo nuevamente.")
                .build()
                .show(childFragmentManager, "")
        }
    }

}
