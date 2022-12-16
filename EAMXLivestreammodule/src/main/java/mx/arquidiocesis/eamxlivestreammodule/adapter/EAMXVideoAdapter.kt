package mx.arquidiocesis.eamxlivestreammodule.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.arquidiocesis.eamxcommonutils.util.extraIdUrlVideoYoutube
import mx.arquidiocesis.eamxcommonutils.util.loadByUrl
import mx.arquidiocesis.eamxlivestreammodule.R
import mx.arquidiocesis.eamxlivestreammodule.databinding.ItemEventsBinding
import mx.arquidiocesis.eamxlivestreammodule.model.VideoNewsModel
import java.util.regex.Pattern

class EAMXVideoAdapter(
    private val context: Context,
    private val featuredList: List<VideoNewsModel>,
    private val listener: (VideoNewsModel) -> Unit
) :
    RecyclerView.Adapter<EAMXVideoAdapter.ChurchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChurchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEventsBinding.inflate(inflater, parent, false)
        return ChurchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChurchViewHolder, position: Int) {
        holder.bind(context, featuredList[position], listener)
    }

    override fun getItemCount(): Int = featuredList?.size

    class ChurchViewHolder(val binding: ItemEventsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            context: Context,
            item: VideoNewsModel,
            listener: (VideoNewsModel) -> Unit,
        ) {
            binding.txtNameChurch.text = item.author
            binding.txtNameMisa.text = item.name


            val idYoutube = item.streaming_url.extraIdUrlVideoYoutube()
            binding.imgMisa.loadByUrl("https://img.youtube.com/vi/$idYoutube/0.jpg")

            binding.cvLive.setOnClickListener {
                listener(item)

            }
            binding.llLive.setOnClickListener {
                //  setViews(context, item.views + 1)
                listener(item)
            }
        }
    }

}
