package mx.arquidiocesis.eamxcommonutils.multimedia

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentVideoBinding

class EAMXVideoFragment : Fragment() {
    lateinit var binding: FragmentVideoBinding
    var player: ExoPlayer? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentVideoBinding.inflate(inflater, container, false)
        player = ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->
            binding.videoView.player = exoPlayer
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val titulo = arguments?.getString("titulo")
        val video = arguments?.getString("video")
        if (!video.isNullOrEmpty()) {
            binding.apply {
                videoView.apply {
                    setPlayer(player)
                    player?.setMediaItem(MediaItem.fromUri(video))
                    player?.prepare()
                    player?.pause()
                    player?.playbackParameters
                    player?.play()
                }
                tituloVideo.text =
                    if (!titulo.isNullOrEmpty()) titulo else "Ha ocurrido un error, intente nuevamente."
            }
        }
    }

    override fun onPause() {
        super.onPause()
        player?.stop()
    }
}
