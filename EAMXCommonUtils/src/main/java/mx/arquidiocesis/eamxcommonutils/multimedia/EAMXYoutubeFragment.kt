package mx.arquidiocesis.eamxcommonutils.multimedia

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.storage.ktx.storageMetadata
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentYoutubeBinding

class EAMXYoutubeFragment : FragmentBase() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var posicion = 0
        val url = ArrayList<String>()
        return(FragmentYoutubeBinding.inflate(inflater, container, false)
            .viewYoutube.apply {
                url.add("https://www.youtube.com/watch?v=A7n_HEH4Te8")
                url.add("https://www.youtube.com/watch?v=zf-bK670_7I")
                url.add("https://www.youtube.com/watch?v=447UjfXps6I")
                settings.javaScriptEnabled
                val iframe = "<iframe width='100%' src='" + url.get(posicion).replace("watch?v=", "embed/") + "' frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>"
            loadData(iframe, "text/html", "UTF-8")
                posicion++
                if (posicion == url.size){
                    posicion = 0
                }

            }).rootView

        // Inflate the layout for this fragment

    }

}