package mx.arquidiocesis.eamxcommonutils.multimedia

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentUrlsBinding
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager

class EAMXUrlFragment : FragmentBase() {

    lateinit var binding: EAMXUrlFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "Home_ModalWeb ")
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        showLoader()
        return (FragmentUrlsBinding.inflate(inflater, container, false)
            .viewUrl.apply {
                settings.apply {
                    javaScriptEnabled = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    setSupportZoom(true)
                    loadWithOverviewMode = true;
                }
                webViewClient = object : WebViewClient() {
                    @Deprecated("Deprecated in Java")
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        urlWeb: String,
                    ): Boolean {
                        loadUrl(urlWeb)
                        return true
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        hideLoader()
                    }

                    override fun onReceivedHttpError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        errorResponse: WebResourceResponse?,
                    ) {
                        super.onReceivedHttpError(view, request, errorResponse)
                        hideLoader()
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?,
                    ) {
                        super.onReceivedError(view, request, error)
                        hideLoader()
                    }

                    override fun onPageCommitVisible(view: WebView?, url: String?) {
                        super.onPageCommitVisible(view, url)
                        hideLoader()
                    }
                }
                loadUrl(Uri.parse(arguments?.getString("url")).toString())
            }).rootView
    }
}