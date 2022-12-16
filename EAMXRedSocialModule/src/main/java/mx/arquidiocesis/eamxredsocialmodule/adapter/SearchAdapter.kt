package mx.arquidiocesis.eamxredsocialmodule.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.arquidiocesis.eamxredsocialmodule.databinding.ItemBuscarBinding
import mx.arquidiocesis.eamxredsocialmodule.model.SearchModel
import mx.arquidiocesis.eamxredsocialmodule.ui.FOLLOW
import mx.arquidiocesis.eamxredsocialmodule.ui.UNFOLLOW


class SearchAdapter(val listener: (String, SearchModel) -> Unit) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var list: MutableList<SearchModel> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBuscarBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(listener, list[position])

    override fun getItemCount(): Int = list.size


    @SuppressLint("NotifyDataSetChanged")
    fun setNewList(items: MutableList<SearchModel>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemBuscarBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
            }
        }

        fun bind(
            listener: (String, SearchModel) -> Unit,
            item: SearchModel
        ) {
            binding.apply {
                txtName.text = item.name
                if (!item.image.isNullOrEmpty()) {
                    Glide.with(root.context)
                        .asBitmap()
                        .load(item.image)
                        .into(imgPriest)
                }

                ivSegir.visibility = View.VISIBLE
                ivDejarSegir.visibility = View.GONE
                item.relationship?.let {
                    if (it.statusId == 1 || it.statusId == 3) {
                        ivSegir.visibility = View.GONE
                        ivDejarSegir.visibility = View.VISIBLE
                    }
                }
                ivSegir.setOnClickListener {
                    listener(FOLLOW, item)
                }
                ivDejarSegir.setOnClickListener {
                    listener(UNFOLLOW, item)
                }
            }
        }
    }
}