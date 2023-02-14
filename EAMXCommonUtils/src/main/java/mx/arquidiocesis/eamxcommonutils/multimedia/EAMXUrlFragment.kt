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

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EAMXUrlFragment : FragmentBase() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var webViewurl: WebView?= null

    lateinit var binding: EAMXUrlFragment
    var Information = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return(FragmentUrlsBinding.inflate(inflater, container, false)
            .viewUrl.apply {
                settings.javaScriptEnabled
                webViewClient = object :WebViewClient(){
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        urlWeb: String
                    ): Boolean {
                        loadUrl(urlWeb)
                        return true
                    }
                }
                loadUrl(Uri.parse(arguments?.getString("urlWeb")).toString())
            }).rootView

        /*
        val urlWeb = arguments?.getString("urlWeb").toString()
        val view: View = inflater.inflate(R.layout.fragment_urls, container, false)
        webViewurl = view.findViewById(R.id.viewUrl)
        webViewurl?.settings?.javaScriptEnabled

        webViewurl?.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView?, url: String
            ): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        webViewurl?.loadUrl("https://es.wikipedia.org/wiki/Reproductor_de_audio_(software)")
        return view

         */
    }
}