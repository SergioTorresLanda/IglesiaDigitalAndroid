package mx.arquidiocesis.eamxredsocialmodule.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.arquidiocesis.eamxredsocialmodule.databinding.ItemMediaPickerBinding
import mx.arquidiocesis.eamxredsocialmodule.news.create.model.EAMXMultimedia


class MediaAdapter(
    var items: MutableList<EAMXMultimedia> = mutableListOf(),
    var recyclerView: RecyclerView,
    val listener: (EAMXMultimedia) -> Unit
) :
    RecyclerView.Adapter<MediaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMediaPickerBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        if (position > 3) {
            holder.view.root.layoutParams = ViewGroup.LayoutParams(0, 0)
            return
        }
        if (itemCount > 2) {
            holder.view.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, this@MediaAdapter.recyclerView.height / 2
            )
        }else{
            holder.view.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, this@MediaAdapter.recyclerView.height
            )
        }
        var more=false
        if (position == 3 && itemCount> 4) {
            more=true
        }
        holder.bind(items[position],more ,itemCount,listener)
    }

    override fun getItemCount(): Int = items.size


    fun updateList(item: MutableList<EAMXMultimedia>) {
        items = item
        recyclerView.post(Runnable { notifyDataSetChanged() })
    }

    class ViewHolder(val view: ItemMediaPickerBinding) : RecyclerView.ViewHolder(view.root) {

        fun bind(
            item: EAMXMultimedia,
            more:Boolean,
            size: Int,
            listener: (EAMXMultimedia) -> Unit
        ) {
            view.apply {
                ivPlay.visibility = View.GONE
                vTransparency.visibility = View.GONE
                if (!item.isImage()) {
                    ivPlay.visibility = View.VISIBLE
                    vTransparency.visibility = View.VISIBLE
                }
                if (more) {
                    vTransparency.visibility = View.VISIBLE
                    tvMoreMedia.visibility = View.VISIBLE
                    ivPlay.visibility = View.GONE
                    tvMoreMedia.text = "+ ${size - 4}"
                } else {
                    vTransparency.visibility = View.GONE
                    tvMoreMedia.visibility = View.GONE
                }

                Glide.with(root.context)
                    .asBitmap()
                    .load(item.path)
                    .into(ivThumbnail)


                root.setOnClickListener {
                    listener(item)
                }
            }
        }
    }
}