package mx.arquidiocesis.eamxredsocialmodule.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.arquidiocesis.eamxredsocialmodule.databinding.EamxItemUpdateMediaBinding
import mx.arquidiocesis.eamxredsocialmodule.news.create.model.EAMXMultimedia
import mx.arquidiocesis.eamxredsocialmodule.ui.EDITAR
import mx.arquidiocesis.eamxredsocialmodule.ui.ELIMINAR


class UpdateAdapter(
    var items: MutableList<EAMXMultimedia> = mutableListOf(),
    var recyclerView: RecyclerView,
    var indexToUpdate:Int=0,
    val listener: (String, EAMXMultimedia) -> Unit
) :
    RecyclerView.Adapter<UpdateAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = EamxItemUpdateMediaBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position],listener)
    }

    override fun getItemCount(): Int = items.size


    fun updateList(item: MutableList<EAMXMultimedia>) {
        items = item
        recyclerView.post(Runnable { notifyDataSetChanged() })
    }

    class ViewHolder(val view: EamxItemUpdateMediaBinding) : RecyclerView.ViewHolder(view.root) {

        fun bind(
            item: EAMXMultimedia,
            listener: (String, EAMXMultimedia) -> Unit
        ) {
            view.apply {
                Glide.with(view.root.context)
                    .asBitmap()
                    .load(item.path)
                    .into(ivThumbnail)


                if (!item.isImage()) {
                    ivPlay.visibility = View.VISIBLE
                    vTransparency.visibility = View.VISIBLE
                }


                ivDelete.setOnClickListener {
                    listener(ELIMINAR, item)
                }

                ivUpdate.setOnClickListener {
                    listener(EDITAR, item)
                }

                itemRoot.setOnClickListener {
                    listener("", item)
                }
            }
        }
    }
}