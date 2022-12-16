package mx.arquidiocesis.eamxredsocialmodule.news.detail.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.databinding.EamxItemCorouselBinding
import mx.arquidiocesis.eamxredsocialmodule.model.Media
import mx.arquidiocesis.eamxredsocialmodule.model.MultimediaModel


class EAMXCarouselAdapter(private val media: List<MultimediaModel>) :
    RecyclerView.Adapter<EAMXCarouselAdapter.ViewHolder>() {

    var onItemClickListener: (media:MultimediaModel, index:Int) -> Unit = { _,_ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.eamx_item_corousel, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentMedia = media[position]
        if (!currentMedia.format.contains("image")) {
            holder.vBind.groupPlay.visibility = View.VISIBLE
        }
        Glide.with(holder.vBind.vThumbnail)
            .load(currentMedia.url)
            .into(holder.vBind.vThumbnail)

        holder.vBind.vThumbnail.setOnClickListener {
            onItemClickListener(currentMedia,position)
        }
    }

    override fun getItemCount(): Int = media.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vBind = EamxItemCorouselBinding.bind(itemView)
    }
}