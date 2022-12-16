package mx.arquidiocesis.eamxredsocialmodule.news.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.databinding.EamxActivityVideoPlayerBinding

class EAMXVideoPlayer : AppCompatActivity() {

    companion object {
        const val VIDEO_URL = "VIDEO_URL"
    }

    lateinit var vBind: EamxActivityVideoPlayerBinding
    private var videoUrl: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retrieveArguments()
        vBind = DataBindingUtil.setContentView(this, R.layout.eamx_activity_video_player)
        vBind.vPlayer.setSource(videoUrl)
    }


    private fun retrieveArguments() {
        videoUrl = intent.getStringExtra(VIDEO_URL)
    }

}