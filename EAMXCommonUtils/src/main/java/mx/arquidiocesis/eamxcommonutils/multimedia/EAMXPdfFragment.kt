package mx.arquidiocesis.eamxcommonutils.multimedia

import android.content.Intent
import android.content.Intent.getIntent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebViewRenderProcessClient
import android.widget.VideoView
import androidx.annotation.RequiresApi
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentPdfBinding
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentVideoBinding

class EAMXPdfFragment : FragmentBase() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return (FragmentPdfBinding.inflate(inflater, container, false)
            .viewPdf.apply {
                //arguments?.getString("pdf_url")
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        url: String
                    ): Boolean {
                        view?.loadUrl(url)
                        return true
                    }
                }
                webViewClient
                settings.setSupportZoom(true)
                settings.javaScriptEnabled
                loadUrl("https://docs.google.com/gview??embedded=true&url=" + arguments?.getString("pdf_url"))
            }).rootView
        //val pdf_url = arguments?.getString("pdf_url")
        //val intent = Intent()
        //intent.putExtra("pdf_url","https://demo.codeseasy.com/downloads/CodesEasy.pdf")
        //val url = pdf_url
        //webViewpdf?.webViewClient
        //webViewpdf?.loadUrl("https://docs.google.com/gview??embedded=true&url=" + url)

    }

}