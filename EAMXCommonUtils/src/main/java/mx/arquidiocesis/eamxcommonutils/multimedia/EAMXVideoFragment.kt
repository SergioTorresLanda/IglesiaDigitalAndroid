package mx.arquidiocesis.eamxcommonutils.multimedia

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentVideoBinding



class EAMXVideoFragment : FragmentBase() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return (FragmentVideoBinding.inflate(inflater, container, false)
            .videoView.apply {
                pausePlayer().also {
                    setSource(Uri.parse(arguments?.getString("url")).toString())

                }
                /*
                setVideoURI(Uri.parse(arguments?.getString("url")))
                setMediaController(MediaController(activity).also {
                    it.setAnchorView(this)
                    it.setMediaPlayer(this)
                })
                setOnPreparedListener {
                    Log.d("videoView", "Ready to play")
                    start()
                }

                 */
            }).rootView
        //videoView = binding.videoView
        //val videoPath = "https://media.geeksforgeeks.org/wp-content/uploads/20201217192146/Screenrecorder-2020-12-17-19-17-36-828.mp4?_=1"
        //videoView!!.setVideoURI(Uri.parse(arguments?.getString("url")))
        //val mediaController = MediaController(activity)
        //mediaController.setAnchorView(videoView)
        //mediaController.setMediaPlayer(videoView)
        //videoView!!.setMediaController(mediaController)
        /*
        videoView!!.setOnPreparedListener {
            Log.d("videoView", "Ready to play")
            videoView!!.start()
        }
        return binding.root

         */
        //return binding.root
    }

    //fun getLayout() = mx.arquidiocesis.eamxcommonutils.R.layout.fragment_video


}
