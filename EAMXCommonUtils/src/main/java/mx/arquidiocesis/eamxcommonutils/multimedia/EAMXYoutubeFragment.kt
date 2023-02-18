package mx.arquidiocesis.eamxcommonutils.multimedia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentYoutubeBinding

class EAMXYoutubeFragment : Fragment() {

    lateinit var binding: FragmentYoutubeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentYoutubeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()


        val titulo = arguments?.getString("titulo")
        val youtube = arguments?.getString("youtube")

        if (youtube != null) {
            binding.viewYoutube.apply {
                val frameVideo =
                    "<html><body>Video From YouTube<br><iframe width=\"420\" height=\"315\" " +
                            "src='" + youtube + "' frameborder=\"0\" allowfullscreen>" +
                            "</iframe></body></html>"
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        return false
                    }
                }
                settings.javaScriptEnabled
                settings.domStorageEnabled
                loadData(frameVideo, "text/html", "utf-8")
            }
        }

        if (titulo != null) {
            binding.Tyoutube.setText(titulo.toString())
        } else {
            binding.Tyoutube.setText("Ha ocurrido un error, intente nuevamente.")
        }


/*
        var posicion = 0
val url = ArrayList<String>()
        url.add("https://www.youtube.com/watch?v=A7n_HEH4Te8")
        url.add("https://www.youtube.com/watch?v=zf-bK670_7I")
        //url.add("https://www.youtube.com/watch?v=447UjfXps6I")
        settings.javaScriptEnabled
        val iframe = "<iframe width='100%' src='" + url.get(posicion).replace("watch?v=", "embed/") + "' frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>"

        loadData(iframe, "text/html", "UTF-8")
        posicion++
        if (posicion == url.size){
            posicion = 0
        }

         */
    }

    private fun initView() {
        //showLoader("lOADER")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //callBack.restoreToolbar()
    }


}