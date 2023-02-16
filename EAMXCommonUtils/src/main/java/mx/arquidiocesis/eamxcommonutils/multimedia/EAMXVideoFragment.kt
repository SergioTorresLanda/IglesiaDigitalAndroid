package mx.arquidiocesis.eamxcommonutils.multimedia

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentTextBinding
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentVideoBinding

class EAMXVideoFragment : FragmentBase() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentVideoBinding
        binding = FragmentVideoBinding.inflate(inflater, container, false)
        val titulo = arguments?.getString("titulo")
        val video = arguments?.getString("video")
        if (titulo != null) {
            binding.tituloVideo.setText(titulo.toString())
        } else {
            binding.tituloVideo.setText("Ha ocurrido un error, intente nuevamente.")
        }
        if (video != null) {
            binding.videoView.apply {
                pausePlayer().also {
                    setSource(Uri.parse(arguments?.getString("url")).toString())

                }
            }

        }
        return view
    }
}
