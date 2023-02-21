package mx.arquidiocesis.eamxcommonutils.multimedia

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentPlayerBinding

class EAMXPlayerFragment : FragmentBase() {
    lateinit var binding: FragmentPlayerBinding
    var player: ExoPlayer? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        player = ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->
            binding.playerv.player = exoPlayer
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val titulo = arguments?.getString("titulo")
        val audio = arguments?.getString("audio")
        if (!audio.isNullOrEmpty()) {
            binding.apply {
                playerv.apply {
                    setPlayer(player)
                    player?.setMediaItem(MediaItem.fromUri(audio))
                    player?.prepare()
                    player?.pause()
                    player?.playbackParameters
                    player?.play()
                }
                tituloplayer.text =
                    if (!titulo.isNullOrEmpty()) titulo else "Ha ocurrido un error, intente nuevamente."
            }
        }
    }

    override fun onPause() {
        super.onPause()
        player?.stop()
    }
}