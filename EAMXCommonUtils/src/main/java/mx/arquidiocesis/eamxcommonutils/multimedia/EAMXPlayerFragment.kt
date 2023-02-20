package mx.arquidiocesis.eamxcommonutils.multimedia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentPlayerBinding
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager

class EAMXPlayerFragment : Fragment() {
    lateinit var binding: FragmentPlayerBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initView()
        val titulo = arguments?.getString("titulo")
        val audio = arguments?.getString("audio")
        if (audio != null) {
            binding.playerv.apply {
        val player = ExoPlayer.Builder(requireContext()).build()
                setPlayer(player)
                val media = MediaItem.fromUri(arguments?.getString("audio").toString())
                player.setMediaItem(media)
                player.prepare()
                player.pause()
                player.playbackParameters
                player.play()
    }
            if (titulo != null) {
                binding.tituloplayer.setText(titulo.toString())
            } else {
                binding.tituloplayer.setText("Ha ocurrido un error, intente nuevamente.")
    }
    }
        }
    private fun initView() {
        //showLoader("lOADER")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        //callBack.restoreToolbar()
    }
}



