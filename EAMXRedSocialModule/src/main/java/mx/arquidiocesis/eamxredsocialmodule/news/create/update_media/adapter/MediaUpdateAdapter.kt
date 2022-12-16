package mx.arquidiocesis.eamxredsocialmodule.news.create.update_media.adapter

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.Repository.Repository
import mx.arquidiocesis.eamxredsocialmodule.databinding.EamxItemUpdateMediaBinding
import mx.arquidiocesis.eamxredsocialmodule.databinding.ItemMediaPickerBinding
import mx.arquidiocesis.eamxredsocialmodule.viewmodel.RedSocialViewModel


class MediaUpdateAdapter(val items: ArrayList<ClipData.Item> = ArrayList()) :
    RecyclerView.Adapter<MediaUpdateAdapter.ViewHolder>() {

    var onDeleteListener: (ClipData.Item) -> Unit = {}

    var onUpdateListener: (ClipData.Item) -> Unit = {}

    var onItemClickListener: (ClipData.Item) -> Unit = {}

    var indexToUpdate = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.eamx_item_update_media,
            parent,
            false
        )
        return ViewHolder(itemView)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        Glide
            .with(holder.itemView.context)
            .asBitmap()
            .load(item.uri)
            .into(holder.vBind.ivThumbnail)

        val meta = RedSocialViewModel(Repository(holder.vBind.itemRoot.context)).getFileMetaData(item.uri)

        if (meta?.isImage() == false) {
            holder.vBind.ivPlay.visibility = View.VISIBLE
            holder.vBind.vTransparency.visibility = View.VISIBLE
        }


        holder.vBind.ivDelete.setOnClickListener {
            onDeleteListener(item)
        }

        holder.vBind.ivUpdate.setOnClickListener {
            onUpdateListener(item)
        }

        holder.vBind.itemRoot.setOnClickListener {
            onItemClickListener(item)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vBind = EamxItemUpdateMediaBinding.bind(itemView)
    }

}