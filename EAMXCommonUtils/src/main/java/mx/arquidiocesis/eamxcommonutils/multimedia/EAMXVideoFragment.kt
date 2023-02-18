package mx.arquidiocesis.eamxcommonutils.multimedia

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentPlayerBinding
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentTextBinding
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentVideoBinding

class EAMXVideoFragment : Fragment() {
    lateinit var binding: FragmentVideoBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        //initView()
        //val binding: FragmentVideoBinding
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
                /*pausePlayer().also {
                    setSource(Uri.parse(arguments?.getString("video")).toString())
                }*/
                val player = ExoPlayer.Builder(requireContext()).build()
                setPlayer(player)
                val media = MediaItem.fromUri(arguments?.getString("video").toString())
                player.setMediaItem(media)
                player.prepare()
                player.pause()
                player.playbackParameters
                player.play()
            }

        }
        return binding.root
    }

    private fun initView() {
        //showLoader("lOADER")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //callBack.restoreToolbar()
    }
}
