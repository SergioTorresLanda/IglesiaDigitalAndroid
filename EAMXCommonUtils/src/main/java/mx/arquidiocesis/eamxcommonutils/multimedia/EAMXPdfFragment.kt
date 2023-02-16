package mx.arquidiocesis.eamxcommonutils.multimedia

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.google.common.reflect.Reflection.getPackageName
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentPdfBinding

class EAMXPdfFragment : FragmentBase() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return (FragmentPdfBinding.inflate(inflater, container, false)
            .viewPdf.apply {
                val pdf = arguments?.getString("pdf")

                settings.setSupportZoom(true)
                settings.javaScriptEnabled
                settings.allowFileAccess
                webViewClient = object :WebViewClient(){
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        urlpdf: String
                    ): Boolean {
                        loadUrl(urlpdf)
                        return true
                    }
                }
                    loadUrl("https://docs.google.com/gview?embedded=true&url=" + pdf)

            }).rootView
    }

}