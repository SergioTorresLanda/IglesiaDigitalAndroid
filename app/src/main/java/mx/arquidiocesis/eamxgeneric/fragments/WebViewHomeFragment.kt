package mx.arquidiocesis.eamxgeneric.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxgeneric.databinding.FragmentWebViewBinding

class WebViewHomeFragment(val ruta: String) : FragmentBase() {
    lateinit var binding: FragmentWebViewBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initObservers()
        binding = FragmentWebViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

    }

    fun initObservers() {


    }

    fun initView() {
        binding.wvHome.apply {
            settings.setJavaScriptEnabled(true)

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, link: String): Boolean {
                    view?.loadUrl(link)
                    return true
                }
            }
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(
                    view: WebView,
                    newProgress: Int
                ) {
                    super.onProgressChanged(view, newProgress)
                    if (progress < 100) {
                        showLoader()
                    } else {
                        hideLoader()
                    }
                }
            }
            loadUrl(ruta)
        }

    }
}