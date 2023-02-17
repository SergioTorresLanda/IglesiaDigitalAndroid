package mx.arquidiocesis.eamxcommonutils.multimedia

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentUrlsBinding

class EAMXUrlFragment : FragmentBase() {

    lateinit var binding: EAMXUrlFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initView()
        return (FragmentUrlsBinding.inflate(inflater, container, false)
            .viewUrl.apply {
                settings.javaScriptEnabled
                webViewClient = object : WebViewClient() {
                    @Deprecated("Deprecated in Java")
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        urlWeb: String
                    ): Boolean {
                        loadUrl(urlWeb)
                        return true
                    }
                }
                loadUrl(Uri.parse(arguments?.getString("url")).toString())
            }).rootView
    }
    private fun initView() {
        showLoader("lOADER")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        callBack.restoreToolbar()
    }
}